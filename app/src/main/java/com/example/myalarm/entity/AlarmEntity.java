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
    private boolean isVibrator;
    private int ringTimes;
    private int alreadyRingTimes;
    private int ringInterval;
    private boolean disabled;
    private boolean tempDisabled;
    private int requestCodeSeq;
    private long nextTriggerTime;
    private long preTriggerTime;

    public AlarmEntity() {

    }

    public AlarmEntity(String name, BaseAlarmType baseAlarmType, LocalTime time, int ringtoneProgress, boolean isVibrator, int ringTimes, int ringInterval) {
        this.name = name;
        this.baseAlarmType = baseAlarmType;
        this.time = time;
        this.ringtoneProgress = ringtoneProgress;
        this.isVibrator = isVibrator;
        this.ringTimes = ringTimes;
        this.ringInterval = ringInterval;
        this.alreadyRingTimes = 0;
        this.requestCodeSeq = 0;
        this.disabled = false;
        this.tempDisabled = false;
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
    }

    public int getRingtoneProgress() {
        return ringtoneProgress;
    }

    public void setRingtoneProgress(int ringtoneProgress) {
        this.ringtoneProgress = ringtoneProgress;
    }

    public boolean isVibrator() {
        return isVibrator;
    }

    public void setVibrator(boolean vibrator) {
        isVibrator = vibrator;
    }

    public int getRingTimes() {
        return ringTimes;
    }

    public void setRingTimes(int ringTimes) {
        this.ringTimes = ringTimes;
    }

    public int getAlreadyRingTimes() {
        return alreadyRingTimes;
    }

    public void setAlreadyRingTimes(int alreadyRingTimes) {
        this.alreadyRingTimes = alreadyRingTimes;
    }

    public int getRingInterval() {
        return ringInterval;
    }

    public void setRingInterval(int ringInterval) {
        this.ringInterval = ringInterval;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isTempDisabled() {
        return tempDisabled;
    }

    public void setTempDisabled(boolean tempDisabled) {
        this.tempDisabled = tempDisabled;
    }

    public int getRequestCodeSeq() {
        return requestCodeSeq;
    }

    public void setRequestCodeSeq(int requestCodeSeq) {
        this.requestCodeSeq = requestCodeSeq;
    }

    public long getNextTriggerTime() {
        return nextTriggerTime;
    }

    public void setNextTriggerTime(long nextTriggerTime) {
        this.nextTriggerTime = nextTriggerTime;
    }

    public long getPreTriggerTime() {
        return preTriggerTime;
    }

    public void setPreTriggerTime(long preTriggerTime) {
        this.preTriggerTime = preTriggerTime;
    }

    public String getRepeatStr() {
        return this.baseAlarmType.getRepeatDesc();
    }
}