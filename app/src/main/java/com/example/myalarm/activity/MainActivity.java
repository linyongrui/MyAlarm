package com.example.myalarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.example.myalarm.viewmodel.AlarmViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static List<HolidayEntity> holidayEntities = new ArrayList<>();
    private AlarmAdapter alarmAdapter;
    private AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        holidaysInit();

        RecyclerView alarmRecyclerView = findViewById(R.id.alarmRecyclerView);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                startActivity(new Intent(MainActivity.this, NewAlarmActivity.class));
            }
        });
    }


    private void holidaysInit() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            HolidayDao holidayDao = DatabaseClient.getInstance().getHolidayEntityDatabase().holidayDao();

            @Override
            public void run() {
                List<HolidayEntity> holidayEntities = holidayDao.getHolidaysByYear(LocalDate.now().getYear());
                Log.i("terry", "holidayEntities.size(): " + holidayEntities.size());
                if (holidayEntities.isEmpty()) {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://unpkg.com/holiday-calendar@1.1.6/data/CN/2025.json")
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        List<HolidayEntity> newHolidayEntities = HolidayUtils.responseHandle(response);
                        HolidayEntity[] holidayEntitiesArray = newHolidayEntities.stream().toArray(HolidayEntity[]::new);
                        holidayDao.insertAll(holidayEntitiesArray);

                        runOnUiThread(new Runnable() { // Assuming this is inside an Activity or Fragment
                            @Override
                            public void run() {
                                setHolidayEntity(newHolidayEntities);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> setHolidayEntity(holidayEntities));
                }
            }
        });
    }

    private void setHolidayEntity(List<HolidayEntity> holidayEntities) {
        this.holidayEntities = holidayEntities;
    }

}