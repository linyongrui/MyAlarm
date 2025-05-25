package com.example.myalarm.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.myalarm.dao.AlarmDao;
import com.example.myalarm.data.converter.AlarmTypeConverter;
import com.example.myalarm.entity.Alarm;


@Database(entities = {Alarm.class}, version = 1, exportSchema = false)
@TypeConverters({AlarmTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();
}