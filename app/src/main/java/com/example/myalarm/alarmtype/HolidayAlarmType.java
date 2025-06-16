package com.example.myalarm.alarmtype;

public class HolidayAlarmType extends BaseAlarmType {
    public final static String ALARM_TYPE = "HOLIDAY";

    public HolidayAlarmType() {
        super();
        setSkipWorkingDay(true);
        setSkipHoliday(false);
    }

    @Override
    public String getType() {
        return ALARM_TYPE;
    }

    @Override
    public String getRepeatDesc() {
        return "非工作日";
    }
}
