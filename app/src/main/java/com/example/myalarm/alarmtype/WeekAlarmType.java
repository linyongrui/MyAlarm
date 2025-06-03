package com.example.myalarm.alarmtype;

public class WeekAlarmType extends BaseAlarmType {
    public final static String ALARM_TYPE = "WEEK";
    private int[] weekDayCheck;

    public WeekAlarmType(int[] weekDayCheck) {
        super();
        this.weekDayCheck = weekDayCheck;
    }

    public int[] getWeekDayCheck() {
        return weekDayCheck;
    }

    public void setWeekDayCheck(int[] weekDayCheck) {
        this.weekDayCheck = weekDayCheck;
    }

    @Override
    public String getType() {
        return ALARM_TYPE;
    }

    @Override
    public String getRepeatDesc() {
        StringBuilder repeatDescBuilder = new StringBuilder();
        int count = 0;
        if (this.weekDayCheck[1] == 1) {
            repeatDescBuilder.append("周一 ");
            count++;
        }
        if (this.weekDayCheck[2] == 1) {
            repeatDescBuilder.append("周二 ");
            count++;
        }
        if (this.weekDayCheck[3] == 1) {
            repeatDescBuilder.append("周三 ");
            count++;
        }
        if (this.weekDayCheck[4] == 1) {
            repeatDescBuilder.append("周四 ");
            count++;
        }
        if (this.weekDayCheck[5] == 1) {
            repeatDescBuilder.append("周五 ");
            count++;
        }
        if (this.weekDayCheck[6] == 1) {
            repeatDescBuilder.append("周六 ");
            count++;
        }
        if (this.weekDayCheck[0] == 1) {
            repeatDescBuilder.append("周日 ");
            count++;
        }
        if (count == 7) {
            repeatDescBuilder = new StringBuilder("每天");
        }
        if (Boolean.TRUE.equals(getSkipHoliday())) {
            repeatDescBuilder.append("(除法定节假日)");
        } else if (Boolean.TRUE.equals(getSkipWorkingDay())) {
            repeatDescBuilder.append("(除法定工作日)");
        }
        return repeatDescBuilder.toString();
    }
}
