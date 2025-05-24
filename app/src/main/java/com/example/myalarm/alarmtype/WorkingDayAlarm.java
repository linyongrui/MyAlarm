package com.example.myalarm.alarmtype;

public class WorkingDayAlarm extends CustomTypeAlarm {
    public final static String ALARM_TYPE = "WORK";

    public WorkingDayAlarm() {
        super();
        setHoliday(false);
        setWorkingDay(true);
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
