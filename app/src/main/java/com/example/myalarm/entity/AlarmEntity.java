package com.example.myalarm.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.myalarm.alarmtype.BaseAlarmType;

import java.time.LocalTime;
import java.util.Objects;

@Entity(tableName = "alarmEntity")
public class AlarmEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private BaseAlarmType baseAlarmType;
    private LocalTime time;
    private boolean enabled;

    public AlarmEntity(String name, BaseAlarmType baseAlarmType, LocalTime time) {
        this.name = name;
        this.baseAlarmType = baseAlarmType;
        this.time = time;
        this.enabled = true;
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

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AlarmEntity that = (AlarmEntity) o;
        return id == that.id && enabled == that.enabled && Objects.equals(name, that.name) && Objects.equals(baseAlarmType, that.baseAlarmType) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, baseAlarmType, time, enabled);
    }
}