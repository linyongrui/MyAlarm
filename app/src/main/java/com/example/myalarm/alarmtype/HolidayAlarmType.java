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
        return "非工作日(包括节假日、周末、不包括补班)";
    }
}
