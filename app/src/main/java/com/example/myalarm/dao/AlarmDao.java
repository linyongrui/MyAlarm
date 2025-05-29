package com.example.myalarm.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myalarm.entity.AlarmEntity;

import java.util.List;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM AlarmEntity ORDER BY time ASC")
    LiveData<List<AlarmEntity>> getAllAlarms();

    @Query("SELECT * FROM AlarmEntity ORDER BY time ASC")
    List<AlarmEntity> getAllActiveAlarms();

    @Insert
    long insertAlarmEntity(AlarmEntity alarmEntity);

    @Update
    void updateAlarmEntity(AlarmEntity alarmEntity);

    @Delete
    void deleteAlarmEntity(AlarmEntity alarmEntity);
}