package com.example.myalarm.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.myalarm.dao.AlarmDao;
import com.example.myalarm.data.converter.AlarmTypeConverter;
import com.example.myalarm.data.converter.LocalTimeConverter;
import com.example.myalarm.entity.AlarmEntity;


@Database(entities = {AlarmEntity.class}, version = 1, exportSchema = false)
@TypeConverters({AlarmTypeConverter.class, LocalTimeConverter.class})
public abstract class AlarmEntityDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();
}