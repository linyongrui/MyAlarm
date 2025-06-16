package com.example.myalarm.alarmtype;

public class WorkingDayAlarmType extends BaseAlarmType {
    public final static String ALARM_TYPE = "WORK";

    public WorkingDayAlarmType() {
        super();
        setSkipWorkingDay(false);
        setSkipHoliday(true);
    }

    @Override
    public String getType() {
        return ALARM_TYPE;
    }

    @Override
    public String getRepeatDesc() {
        return "法定工作日";
    }
}
