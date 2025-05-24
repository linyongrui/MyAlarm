package com.example.myalarm.alarmtype;

public class EveryDayAlarm extends CustomTypeAlarm {
    public final static String ALARM_TYPE = "EVERY_DATE";

    public EveryDayAlarm() {
        super();
    }

    @Override
    public String getType() {
        return ALARM_TYPE;
    }

    @Override
    public String getRepeatDesc() {
        StringBuilder repeatDescBuilder = new StringBuilder();
        repeatDescBuilder.append("每天");
        if (Boolean.TRUE.equals(isWorkingDay())) {
            repeatDescBuilder.append("（法定节假日不响铃）");
        } else if (Boolean.TRUE.equals(isHoliday())) {
            repeatDescBuilder.append("（法定工作日不响铃）");
        }
        return repeatDescBuilder.toString();
    }
}
