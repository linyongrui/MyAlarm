package com.example.myalarm.data;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private static DatabaseClient instance;
    private final AlarmEntityDatabase alarmEntityDatabase;
    private final HolidayEntityDatabase holidayEntityDatabase;

    private DatabaseClient(Context context) {
        alarmEntityDatabase = Room.databaseBuilder(context.getApplicationContext(), AlarmEntityDatabase.class, "alarm-database")
                .fallbackToDestructiveMigration()
                .build();
        holidayEntityDatabase = Room.databaseBuilder(context.getApplicationContext(), HolidayEntityDatabase.class, "holiday-database")
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

    public AlarmEntityDatabase getAlarmEntityDatabase() {
        return alarmEntityDatabase;
    }

    public HolidayEntityDatabase getHolidayEntityDatabase() {
        return holidayEntityDatabase;
    }
}