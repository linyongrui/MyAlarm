package com.example.myalarm;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myalarm.alarmtype.CustomTypeAlarm;
import com.example.myalarm.alarmtype.DateAlarm;
import com.example.myalarm.alarmtype.EveryDayAlarm;
import com.example.myalarm.alarmtype.HolidayAlarm;
import com.example.myalarm.alarmtype.WeekAlarm;
import com.example.myalarm.alarmtype.WorkingDayAlarm;
import com.example.myalarm.dto.Alarm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView alarmRecyclerView;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化闹钟数据
        initAlarmData();

        // 设置RecyclerView
        alarmRecyclerView = findViewById(R.id.alarmRecyclerView);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmAdapter = new AlarmAdapter(alarmList);
        alarmRecyclerView.setAdapter(alarmAdapter);

        // 设置添加闹钟按钮
        FloatingActionButton addAlarmButton = findViewById(R.id.addAlarmButton);
        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理添加闹钟逻辑
                Toast.makeText(MainActivity.this, "添加新闹钟", Toast.LENGTH_SHORT).show();
                // 这里可以打开闹钟设置对话框
            }
        });
    }

    private void initAlarmData() {
        alarmList = new ArrayList<>();
        alarmList.add(new Alarm(new WeekAlarm(new int[]{1, 1, 1, 1, 1, 1, 0}), "07:20", true));
        alarmList.add(new Alarm("07:24", true));
        alarmList.add(new Alarm(new EveryDayAlarm(), "07:30", true));
        alarmList.add(new Alarm(new DateAlarm(2025, 5, 23), "07:40", true));
        alarmList.add(new Alarm(new DateAlarm(2025, 5, 23, true), "07:41", true));

        CustomTypeAlarm customTypeAlarm = new DateAlarm(2025, null, 23);
        customTypeAlarm.setWorkingDay(true);
        alarmList.add(new Alarm(customTypeAlarm, "07:42", true));

        CustomTypeAlarm customTypeAlarm1 = new DateAlarm(2025, 5, null);
        customTypeAlarm1.setHoliday(true);
        alarmList.add(new Alarm(customTypeAlarm1, "07:43", true));

        alarmList.add(new Alarm(new HolidayAlarm(), "07:50", false));
        alarmList.add(new Alarm(new WorkingDayAlarm(), "07:51", true));
    }
}