package com.example.myalarm.alarmtype;

public class EveryDayAlarm extends CustomTypeAlarm {
    public final static String ALARM_TYPE = "E";

    public EveryDayAlarm() {
        super();
    }

    @Override
    public String getRepeatDesc() {
        return "每天";
    }
}
