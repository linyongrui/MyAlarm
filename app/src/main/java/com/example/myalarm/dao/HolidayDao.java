package com.example.myalarm.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myalarm.entity.HolidayEntity;

import java.util.List;

@Dao
public interface HolidayDao {

    @Query("SELECT * FROM HolidayEntity where year=:year ORDER BY date")
    List<HolidayEntity> getHolidaysByYear(int year);

    @Insert
    void insertAll(HolidayEntity... holidayEntities);

    @Insert
    long insertHolidayEntity(HolidayEntity HolidayEntity);

    @Update
    void updateHolidayEntity(HolidayEntity HolidayEntity);

    @Delete
    void deleteHolidayEntity(HolidayEntity HolidayEntity);
}