package com.example.myalarm.alarmtype;

public abstract class BaseAlarmType {
    private String name;
    private Boolean isSkipWorkingDay;
    private Boolean isSkipHoliday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSkipWorkingDay() {
        return isSkipWorkingDay;
    }

    public void setSkipWorkingDay(Boolean skipWorkingDay) {
        isSkipWorkingDay = skipWorkingDay;
    }

    public Boolean getSkipHoliday() {
        return isSkipHoliday;
    }

    public void setSkipHoliday(Boolean skipHoliday) {
        isSkipHoliday = skipHoliday;
    }

    public abstract String getType();

    public abstract String getRepeatDesc();
}
