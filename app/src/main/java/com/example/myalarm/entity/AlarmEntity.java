package com.example.myalarm.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.myalarm.alarmtype.BaseAlarmType;
import com.example.myalarm.alarmtype.EveryDayAlarmType;

@Entity(tableName = "alarmEntity")
public class AlarmEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private BaseAlarmType baseAlarmType;
    private String time;
    private boolean enabled;

    @Ignore
    public AlarmEntity(String time, boolean enabled) {
        this.baseAlarmType = new EveryDayAlarmType();
        this.time = time;
        this.enabled = enabled;
    }

    public AlarmEntity(BaseAlarmType baseAlarmType, String time, boolean enabled) {
        this.baseAlarmType = baseAlarmType;
        this.time = time;
        this.enabled = enabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BaseAlarmType getBaseAlarmType() {
        return baseAlarmType;
    }

    public void setBaseAlarmType(BaseAlarmType baseAlarmType) {
        this.baseAlarmType = baseAlarmType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRepeatStr() {
        return this.baseAlarmType.getRepeatDesc();
    }
}