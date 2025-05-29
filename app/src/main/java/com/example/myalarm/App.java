package com.example.myalarm;


import android.app.Application;

import com.example.myalarm.data.DatabaseClient;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseClient.initialize(this);
//        List<AlarmEntity> activeAlarms = DatabaseClient.getInstance().getAlarmEntityDatabase().alarmDao().getAllActiveAlarms();
//        for (AlarmEntity alarm : activeAlarms) {
//            AlarmUtils.setAlarm(this, alarm);
//        }
    }
}
