package com.example.myalarm.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.myalarm.dao.AlarmDao;
import com.example.myalarm.dao.HolidayDao;
import com.example.myalarm.data.converter.AlarmTypeConverter;
import com.example.myalarm.data.converter.LocalTimeConverter;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.entity.HolidayEntity;


@Database(entities = {AlarmEntity.class, HolidayEntity.class}, version = 1, exportSchema = false)
@TypeConverters({AlarmTypeConverter.class, LocalTimeConverter.class})
public abstract class AlarmDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();

    public abstract HolidayDao holidayDao();
}