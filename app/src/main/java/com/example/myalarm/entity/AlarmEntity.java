package com.example.myalarm.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.myalarm.alarmtype.BaseAlarmType;

import java.time.LocalTime;

@Entity(tableName = "alarmEntity")
public class AlarmEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private BaseAlarmType baseAlarmType;
    private LocalTime time;
    private int ringtoneProgress;
    private boolean enabled;
    private int requestCodeSeq;

    public AlarmEntity(String name, BaseAlarmType baseAlarmType, LocalTime time, int ringtoneProgress) {
        this.name = name;
        this.baseAlarmType = baseAlarmType;
        this.time = time;
        this.enabled = true;
        this.ringtoneProgress = ringtoneProgress;
        if (this.time != null) {
            this.time.withSecond(0).withNano(0);
        }
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

        if (this.time != null) {
            this.time.withSecond(0).withNano(0);
        }
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

    public int getRingtoneProgress() {
        return ringtoneProgress;
    }

    public void setRingtoneProgress(int ringtoneProgress) {
        this.ringtoneProgress = ringtoneProgress;
    }

    public int getRequestCodeSeq() {
        return requestCodeSeq;
    }

    public void setRequestCodeSeq(int requestCodeSeq) {
        this.requestCodeSeq = requestCodeSeq;
    }
}