package com.example.myalarm.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.util.AlarmUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmViewModel extends ViewModel {
    private final LiveData<List<AlarmEntity>> alarmList;
    private final MutableLiveData<String> TimeLeft = new MutableLiveData<>();
    private Timer timer;

    public AlarmViewModel() {
        alarmList = DatabaseClient.getInstance().getAlarmEntityDatabase().alarmDao().getAllAlarms();
        alarmList.observeForever(alarms -> {
            startCountdownTo();
        });
    }

    public LiveData<List<AlarmEntity>> getAlarmList() {
        return alarmList;
    }

    public LiveData<String> getTimeLeft() {
        return TimeLeft;
    }

    public void startCountdownTo() {
        stopTimer();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String timeLeftStr = AlarmUtils.getNextTriggerTimeLeft(alarmList.getValue());
                TimeLeft.postValue(timeLeftStr);
            }
        }, 500, 60 * 1000);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onCleared() {
        stopTimer();
        super.onCleared();
    }
}