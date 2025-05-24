package com.example.myalarm.dto;

import com.example.myalarm.alarmtype.CustomTypeAlarm;
import com.example.myalarm.alarmtype.EveryDayAlarm;

public class Alarm {
    private String name;
    private CustomTypeAlarm customTypeAlarm;
    private String time;
    private boolean enabled;

    public Alarm(String time, boolean enabled) {
        this.customTypeAlarm = new EveryDayAlarm();
        this.time = time;
        this.enabled = enabled;
    }

    public Alarm(CustomTypeAlarm customTypeAlarm, String time, boolean enabled) {
        this.customTypeAlarm = customTypeAlarm;
        this.time = time;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomTypeAlarm getCustomTypeAlarm() {
        return customTypeAlarm;
    }

    public void setCustomTypeAlarm(CustomTypeAlarm customTypeAlarm) {
        this.customTypeAlarm = customTypeAlarm;
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
        return this.customTypeAlarm.getRepeatDesc();
    }
}