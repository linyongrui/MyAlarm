package com.example.myalarm.alarmtype;

public class DateAlarmType extends BaseAlarmType {
    public final static String ALARM_TYPE = "DATE";
    private Integer year;
    private Integer month;
    private Integer day;
    private boolean lastDay;

    public DateAlarmType(Integer year, Integer month, Integer day) {
        super();
        this.year = year;
        this.month = month;
        this.day = day;
        this.lastDay = false;
    }

    public DateAlarmType(Integer year, Integer month, Integer day, boolean lastDay) {
        super();
        this.year = year;
        this.month = month;
        this.day = day;
        this.lastDay = lastDay;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public boolean isLastDay() {
        return lastDay;
    }

    public void setLastDay(boolean lastDay) {
        this.lastDay = lastDay;
    }

    @Override
    public String getType() {
        return ALARM_TYPE;
    }

    @Override
    public String getRepeatDesc() {
        StringBuilder repeatDescBuilder = new StringBuilder();
        repeatDescBuilder.append(this.year == null ? "每年" : this.year + "年");
        repeatDescBuilder.append(this.month == null ? "每月" : this.month + "月");
        if (this.lastDay) {
            repeatDescBuilder.append("最后一天");
        } else if (this.day == null) {
            repeatDescBuilder.append("每天");
        } else {
            repeatDescBuilder.append(this.day + "日");
        }
        if (Boolean.TRUE.equals(getSkipHoliday())) {
            repeatDescBuilder.append("(非工作日不响铃)");
        } else if (Boolean.TRUE.equals(getSkipWorkingDay())) {
            repeatDescBuilder.append("(法定工作日不响铃)");
        }
        return repeatDescBuilder.toString();
    }
}
