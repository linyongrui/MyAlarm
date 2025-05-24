package com.example.myalarm.alarmtype;

public abstract class CustomTypeAlarm {
    private String name;
    private Boolean workingDay;
    private Boolean holiday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(Boolean workingDay) {
        this.workingDay = workingDay;
    }

    public Boolean isHoliday() {
        return holiday;
    }

    public void setHoliday(Boolean holiday) {
        this.holiday = holiday;
    }

    public abstract String getType();

    public abstract String getRepeatDesc();
}
