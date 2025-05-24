package com.example.myalarm.alarmtype;

public class WeekAlarm extends CustomTypeAlarm {
    public final static String ALARM_TYPE = "W";
    private int[] weekDayCheck;

    public WeekAlarm(int[] weekDayCheck) {
        this.weekDayCheck = weekDayCheck;
    }

    public int[] getWeekDayCheck() {
        return weekDayCheck;
    }

    public void setWeekDayCheck(int[] weekDayCheck) {
        this.weekDayCheck = weekDayCheck;
    }

    @Override
    public String getRepeatDesc() {
        StringBuilder repeatDescBuilder = new StringBuilder();
        if (this.weekDayCheck[1] == 1) {
            repeatDescBuilder.append("周一 ");
        }
        if (this.weekDayCheck[2] == 1) {
            repeatDescBuilder.append("周二 ");
        }
        if (this.weekDayCheck[3] == 1) {
            repeatDescBuilder.append("周三 ");
        }
        if (this.weekDayCheck[4] == 1) {
            repeatDescBuilder.append("周四 ");
        }
        if (this.weekDayCheck[5] == 1) {
            repeatDescBuilder.append("周五 ");
        }
        if (this.weekDayCheck[6] == 1) {
            repeatDescBuilder.append("周六 ");
        }
        if (this.weekDayCheck[0] == 1) {
            repeatDescBuilder.append("周日 ");
        }
        return repeatDescBuilder.toString();
    }
}
