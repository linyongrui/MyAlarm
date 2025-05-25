package com.example.myalarm.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.entity.Alarm;

import java.util.List;

public class AlarmViewModel extends ViewModel {
    private final LiveData<List<Alarm>> alarmList;

    public AlarmViewModel() {
        alarmList = DatabaseClient.getInstance().getAppDatabase().alarmDao().getAllAlarms();
    }

    public LiveData<List<Alarm>> getAlarmList() {
        return alarmList;
    }
}