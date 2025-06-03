package com.example.myalarm.alarmtype;

public class EveryDayAlarmType extends BaseAlarmType {
    public final static String ALARM_TYPE = "EVERY_DATE";

    public EveryDayAlarmType() {
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
        if (Boolean.TRUE.equals(getSkipHoliday())) {
            repeatDescBuilder.append("(除法定节假日)");
        } else if (Boolean.TRUE.equals(getSkipWorkingDay())) {
            repeatDescBuilder.append("(除法定工作日)");
        }
        return repeatDescBuilder.toString();
    }
}
