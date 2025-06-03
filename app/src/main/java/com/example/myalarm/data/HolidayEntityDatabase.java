package com.example.myalarm.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myalarm.dao.HolidayDao;
import com.example.myalarm.entity.HolidayEntity;


@Database(entities = {HolidayEntity.class}, version = 1, exportSchema = false)
public abstract class HolidayEntityDatabase extends RoomDatabase {
    public abstract HolidayDao holidayDao();
}