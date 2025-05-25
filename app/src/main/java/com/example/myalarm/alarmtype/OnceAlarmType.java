package com.example.myalarm.alarmtype;

public class OnceAlarmType extends BaseAlarmType {
    public final static String ALARM_TYPE = "ONCE";

    public OnceAlarmType() {
        super();
    }

    @Override
    public String getType() {
        return ALARM_TYPE;
    }

    @Override
    public String getRepeatDesc() {
        return "响一次";
    }
}
