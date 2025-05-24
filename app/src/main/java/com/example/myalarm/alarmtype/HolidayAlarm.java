package com.example.myalarm.alarmtype;

public class HolidayAlarm extends CustomTypeAlarm {
    public final static String ALARM_TYPE = "HOLIDAY";

    public HolidayAlarm() {
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
