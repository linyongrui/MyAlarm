package com.example.myalarm.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myalarm.R;
import com.example.myalarm.adapter.AlarmAdapter;
import com.example.myalarm.dao.HolidayDao;
import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.entity.HolidayEntity;
import com.example.myalarm.util.AlarmUtils;
import com.example.myalarm.util.BackupUtils;
import com.example.myalarm.util.HolidayUtils;
import com.example.myalarm.util.PermissionUtils;
import com.example.myalarm.viewmodel.AlarmViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final int REQUEST_EXPORT_JSON = 1003;
    private static final int REQUEST_IMPORT_JSON = 1004;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.POST_NOTIFICATIONS
    };

    private AlarmAdapter alarmAdapter;
    private AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        holidaysInit();
        permissionCheck();
        PermissionUtils.overlayPermissionCheck(this);

        RecyclerView alarmRecyclerView = findViewById(R.id.alarmRecyclerView);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        alarmAdapter = new AlarmAdapter();
        alarmAdapter.setOnItemClickListener(alarmEntity -> {
            Intent intent = new Intent(this, AlarmFormActivity.class);
            intent.putExtra("alarm_id", alarmEntity.getId());
            startActivity(intent);
        });
        alarmRecyclerView.setAdapter(alarmAdapter);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        alarmViewModel.getAlarmList().observe(this, new Observer<List<AlarmEntity>>() {
            @Override
            public void onChanged(List<AlarmEntity> alarmEntities) {
                alarmAdapter.submitList(alarmEntities);
            }
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        alarmViewModel.startCountdownTo();
        alarmViewModel.getTimeLeft().observe(this, timeLeft -> {
            if (timeLeft != null) {
                toolbar.setSubtitle(timeLeft);
                alarmAdapter.notifyDataSetChanged();
            }
        });

        alarmAdapter.setOnMultiSelectStartListener((multiSelectMode) -> alarmViewModel.setMultiSelectMode(multiSelectMode));
        alarmViewModel.getMultiSelectMode().observe(this, multiSelectMode -> {
            findViewById(R.id.main_delete_header).setVisibility(multiSelectMode ? View.VISIBLE : View.GONE);
            findViewById(R.id.toolbarLayout).setVisibility(multiSelectMode ? View.GONE : View.VISIBLE);
            findViewById(R.id.addAlarmButton).setVisibility(multiSelectMode ? View.GONE : View.VISIBLE);
        });
        findViewById(R.id.main_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmAdapter.setMultiSelectMode(false);
            }
        });
        findViewById(R.id.main_btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<Long> ids = alarmAdapter.getSelectedIds();
                AlarmUtils.deleteAlarmsByIds(context, Set.copyOf(ids));
                alarmAdapter.setMultiSelectMode(false);
            }
        });

        FloatingActionButton addAlarmButton = findViewById(R.id.addAlarmButton);
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holidaysInit();
                permissionCheck();
                startActivity(new Intent(MainActivity.this, AlarmFormActivity.class));
            }
        });

        findViewById(R.id.btn_export_json).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE, "alarm_backup.json");
            startActivityForResult(intent, REQUEST_EXPORT_JSON);
        });

        findViewById(R.id.btn_import_json).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/json");
            startActivityForResult(intent, REQUEST_IMPORT_JSON);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != RESULT_OK) return;

        Uri uri = data.getData();
        if (requestCode == REQUEST_EXPORT_JSON) {
            BackupUtils.exportToJson(this.getApplicationContext(), uri);
        } else if (requestCode == REQUEST_IMPORT_JSON) {
            BackupUtils.importFromJson(this, uri);
        }
    }


    private void holidaysInit() {
        LocalDate today = LocalDate.now();
        int thisYear = today.getYear();
        int nextYear = thisYear + 1;

        Set<Integer> yearInitialized = HolidayUtils.getYearInitialized();
        if (!yearInitialized.contains(thisYear)) {
            getHolidayFromUrl(thisYear, false);
        }

        if (Objects.equals(Month.DECEMBER, today.getMonth()) && !yearInitialized.contains(nextYear)) {
            getHolidayFromUrl(thisYear, false);
            getHolidayFromUrl(nextYear, true);
        }
    }

    private void getHolidayFromUrl(int year, boolean isAppend) {
        String url = HolidayUtils.GET_HOLIDAY_URL.replace("{year}", year + "");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            HolidayDao holidayDao = DatabaseClient.getInstance().getAlarmDatabase().holidayDao();

            @Override
            public void run() {
                List<HolidayEntity> holidayEntities = holidayDao.getHolidaysByYear(year);
                if (holidayEntities.isEmpty()) {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        List<HolidayEntity> newHolidayEntities = HolidayUtils.getHolidayEntityList(response);
                        HolidayEntity[] holidayEntitiesArray = newHolidayEntities.stream().toArray(HolidayEntity[]::new);
                        if (!isAppend) {
                            holidayDao.deleteAllHolidayEntity();
                        }
                        holidayDao.insertAll(holidayEntitiesArray);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                HolidayUtils.setHolidayEntity(newHolidayEntities, isAppend);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> HolidayUtils.setHolidayEntity(holidayEntities, isAppend));
                }
            }
        });
    }

    private void permissionCheck() {
        PermissionUtils.permissionCheck(this, PERMISSIONS, REQUEST_CODE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!PermissionUtils.grantResultCheck(grantResults)) {
            Toast.makeText(getApplicationContext(), "通知权限被拒绝，无法设置使用闹钟", Toast.LENGTH_SHORT).show();
            PermissionUtils.jumpToPermissionGrant(this, Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        }
    }
}