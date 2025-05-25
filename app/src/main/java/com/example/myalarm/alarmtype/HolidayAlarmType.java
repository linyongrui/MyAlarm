package com.example.myalarm.alarmtype;

public class HolidayAlarmType extends BaseAlarmType {
    public final static String ALARM_TYPE = "HOLIDAY";

    public HolidayAlarmType() {
        super();
        setHoliday(true);
        setWorkingDay(false);
    }

    @Override
    public String getType() {
        return ALARM_TYPE;
    }

    @Override
    public String getRepeatDesc() {
        return "法定节假日";
    }
}
