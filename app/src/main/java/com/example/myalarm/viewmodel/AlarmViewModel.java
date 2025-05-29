package com.example.myalarm.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.entity.AlarmEntity;

import java.util.List;

public class AlarmViewModel extends ViewModel {
    private final LiveData<List<AlarmEntity>> alarmList;

    public AlarmViewModel() {
        alarmList = DatabaseClient.getInstance().getAlarmEntityDatabase().alarmDao().getAllAlarms();
    }

    public LiveData<List<AlarmEntity>> getAlarmList() {
        return alarmList;
    }
}