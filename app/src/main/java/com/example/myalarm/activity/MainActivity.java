package com.example.myalarm.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myalarm.AlarmAdapter;
import com.example.myalarm.R;
import com.example.myalarm.dao.HolidayDao;
import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.entity.HolidayEntity;
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

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.POST_NOTIFICATIONS
    };

    private AlarmAdapter alarmAdapter;
    private AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        holidaysInit();
        permissionCheck();

        Context context = getApplicationContext();

        RecyclerView alarmRecyclerView = findViewById(R.id.alarmRecyclerView);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        alarmAdapter = new AlarmAdapter();
        alarmRecyclerView.setAdapter(alarmAdapter);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        alarmViewModel.getAlarmList().observe(this, new Observer<List<AlarmEntity>>() {
            @Override
            public void onChanged(List<AlarmEntity> alarmEntities) {
                alarmAdapter.submitList(alarmEntities);
            }
        });

        FloatingActionButton addAlarmButton = findViewById(R.id.addAlarmButton);
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holidaysInit();
                startActivity(new Intent(MainActivity.this, NewAlarmActivity.class));
            }
        });
    }

    private void holidaysInit() {
        Log.i("terry", "holidaysInit");

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
            HolidayDao holidayDao = DatabaseClient.getInstance().getHolidayEntityDatabase().holidayDao();

            @Override
            public void run() {
                List<HolidayEntity> holidayEntities = holidayDao.getHolidaysByYear(year);
                Log.i("terry", year + " holidayEntity size: " + holidayEntities.size());
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
        PermissionUtils.permissionCheck(this, PERMISSIONS, 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.prantResultCheck(grantResults)) {
            Log.d("terry", "grant successed.");
        } else {
            Toast.makeText(getApplicationContext(), "权限被拒绝，无法设置使用闹钟", Toast.LENGTH_SHORT).show();
            Log.d("terry", "grant failed.");

        }
    }
}