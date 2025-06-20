package com.example.myalarm.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myalarm.entity.AlarmEntity;

import java.util.List;
import java.util.Set;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM AlarmEntity ORDER BY time ASC")
    LiveData<List<AlarmEntity>> liveDataGetAllAlarms();

    @Query("SELECT * FROM AlarmEntity where id=:id")
    LiveData<AlarmEntity> liveDataGetAlarmById(long id);

    @Query("SELECT * FROM AlarmEntity ORDER BY time ASC")
    List<AlarmEntity> getAllAlarms();

    @Query("SELECT * FROM AlarmEntity where id=:id")
    AlarmEntity getActiveAlarmById(long id);

    @Query("SELECT * FROM AlarmEntity where ID in(:alarmIds)")
    List<AlarmEntity> getAlarmByIds(Set<Long> alarmIds);

    @Insert
    long insertAlarmEntity(AlarmEntity alarmEntity);

    @Update
    void updateAlarmEntity(AlarmEntity alarmEntity);

    @Query("DELETE FROM AlarmEntity WHERE ID in(:alarmIds)")
    void deleteAlarmByIds(Set<Long> alarmIds);

    @Query("DELETE FROM AlarmEntity")
    void deleteAllAlarm();
}