package com.example.myalarm.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myalarm.entity.HolidayEntity;

import java.util.List;

@Dao
public interface HolidayDao {

    @Query("SELECT * FROM HolidayEntity where year=:year ORDER BY date")
    List<HolidayEntity> getHolidaysByYear(int year);

    @Insert
    void insertAll(HolidayEntity... holidayEntities);

    @Query("DELETE FROM HolidayEntity")
    void deleteAllHolidayEntity();
}