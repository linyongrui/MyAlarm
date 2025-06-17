package com.example.myalarm.data;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private static DatabaseClient instance;
    private final AlarmDatabase alarmDatabase;

    private DatabaseClient(Context context) {
        alarmDatabase = Room.databaseBuilder(context.getApplicationContext(), AlarmDatabase.class, "alarm_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized void initialize(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
    }

    public static synchronized DatabaseClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseClient must be initialized before use");
        }
        return instance;
    }

    public AlarmDatabase getAlarmDatabase() {
        return alarmDatabase;
    }

}