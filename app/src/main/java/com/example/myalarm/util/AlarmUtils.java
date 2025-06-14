package com.example.myalarm.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.myalarm.alarmtype.BaseAlarmType;
import com.example.myalarm.alarmtype.DateAlarmType;
import com.example.myalarm.alarmtype.EveryDayAlarmType;
import com.example.myalarm.alarmtype.HolidayAlarmType;
import com.example.myalarm.alarmtype.OnceAlarmType;
import com.example.myalarm.alarmtype.WeekAlarmType;
import com.example.myalarm.alarmtype.WorkingDayAlarmType;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.receiver.AlarmReceiver;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public class AlarmUtils {

    public static boolean isWorkingDay(LocalDate date) {

        Set<String> holidatSet = HolidayUtils.getHolidaySet();
        Set<String> transferWorkdaySet = HolidayUtils.getTransferWorkdaySet();
//        Log.i("terry", "holidatSet.size()：" + holidatSet.size());
//        Log.i("terry", "transferWorkdaySet.size()：" + transferWorkdaySet.size());

        boolean isWorkingDate;
        String dateStr = date.toString();
        if (holidatSet.contains(dateStr)) {
            isWorkingDate = false;
        } else if (transferWorkdaySet.contains(dateStr)) {
            isWorkingDate = true;
        } else {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                isWorkingDate = true;
            } else {
                isWorkingDate = false;
            }
        }
        return isWorkingDate;
    }

    public static LocalDate getNextTriggerDateForDate(AlarmEntity alarmEntity) {
        return null;
    }

    public static LocalDate getNextTriggerDateForOnce(LocalTime alarmTime) {
        LocalDate today = LocalDate.now();
        LocalTime timeNow = LocalTime.now().withSecond(0).withNano(0);
        boolean isBeforeNow = !alarmTime.isAfter(timeNow);
        return isBeforeNow ? today.plusDays(1L) : today;
    }

    public static LocalDate getNextTriggerDateForWeek(AlarmEntity alarmEntity) {
        LocalDate nextTriggerDate = null;

        LocalDate today = LocalDate.now();
        LocalTime timeNow = LocalTime.now().withSecond(0).withNano(0);
        boolean isBeforeNow = !alarmEntity.getTime().isAfter(timeNow);

        WeekAlarmType weekAlarmType = (WeekAlarmType) alarmEntity.getBaseAlarmType();
        int[] weekCheck = weekAlarmType.getWeekDayCheck();
        boolean isSkipHoliday = weekAlarmType.getSkipHoliday();
        boolean isSkipWorkingDay = weekAlarmType.getSkipWorkingDay();
        if (weekCheck[0] == 0 && weekCheck[1] == 0 && weekCheck[2] == 0 && weekCheck[3] == 0 && weekCheck[4] == 0 && weekCheck[5] == 0 && weekCheck[6] == 0) {
            return null;
        }

        int todayOfWeekNumber = today.getDayOfWeek().getValue();
        todayOfWeekNumber = todayOfWeekNumber == 7 ? 0 : todayOfWeekNumber;
        LocalDate nextTriggerDateCursor = LocalDate.now();
        LocalDate nextTriggerDateT1 = null;
        LocalDate nextTriggerDateT2 = null;

        int count = 0;
        int index = todayOfWeekNumber;
        boolean isNextTriggerDateT1Get = false;
        do {
            if (weekCheck[index] == 1) {
                boolean isWorkingDay = isWorkingDay(nextTriggerDateCursor);
                boolean isSkipCheckPass = (!isSkipHoliday && !isSkipWorkingDay) || (isSkipHoliday && isWorkingDay) || (isSkipWorkingDay && !isWorkingDay);
                if (isSkipCheckPass) {
                    if (nextTriggerDateT1 == null) {
                        isNextTriggerDateT1Get = true;
                        nextTriggerDateT1 = isBeforeNow && today.equals(nextTriggerDateCursor) ? nextTriggerDateCursor.plusDays(7L) : nextTriggerDateCursor;
                    } else if (nextTriggerDateT2 == null) {
                        nextTriggerDateT2 = nextTriggerDateCursor;
                        break;
                    }
                }
            }
            index = index == 6 ? 0 : index + 1;
            nextTriggerDateCursor = nextTriggerDateCursor.plusDays(1L);

            count++;
        } while ((isNextTriggerDateT1Get && count < 7) || (!isNextTriggerDateT1Get && count < 365));
        Log.i("terry", "count：" + count);

        if (nextTriggerDateT1 != null && nextTriggerDateT2 != null) {
            nextTriggerDate = nextTriggerDateT1.isBefore(nextTriggerDateT2) ? nextTriggerDateT1 : nextTriggerDateT2;
        } else if (nextTriggerDateT1 != null) {
            nextTriggerDate = nextTriggerDateT1;
        }
        return nextTriggerDate;
    }

    public static LocalDate getNextTriggerDateForHoliday(LocalTime alarmTime) {
        LocalDate today = LocalDate.now();
        LocalTime timeNow = LocalTime.now().withSecond(0).withNano(0);
        boolean isBeforeNow = !alarmTime.isAfter(timeNow);

        LocalDate nextTriggerDate = null;
        LocalDate nextTriggerDateCursor = isBeforeNow ? today.plusDays(1L) : today;
        int count = 0;
        do {
            count++;
            boolean isWorkingDay = isWorkingDay(nextTriggerDateCursor);
            if (isWorkingDay) {
                nextTriggerDateCursor = nextTriggerDateCursor.plusDays(1L);
            } else {
                nextTriggerDate = nextTriggerDateCursor;
                break;
            }
        } while (count <= 31);
        return nextTriggerDate;
    }

    public static LocalDate getNextTriggerDateForWorkingDay(LocalTime alarmTime) {
        LocalDate today = LocalDate.now();
        LocalTime timeNow = LocalTime.now().withSecond(0).withNano(0);
        boolean isBeforeNow = !alarmTime.isAfter(timeNow);

        LocalDate nextTriggerDate = null;
        LocalDate nextTriggerDateCursor = isBeforeNow ? today.plusDays(1L) : today;
        int count = 0;
        do {
            count++;
            boolean isWorkingDay = isWorkingDay(nextTriggerDateCursor);
            if (isWorkingDay) {
                nextTriggerDate = nextTriggerDateCursor;
                break;
            } else {
                nextTriggerDateCursor = nextTriggerDateCursor.plusDays(1L);
            }
        } while (count <= 31);
        return nextTriggerDate;
    }

    public static LocalDate getNextTriggerDate(AlarmEntity alarmEntity) {
        LocalDate nextTriggerDate;
        BaseAlarmType baseAlarmType = alarmEntity.getBaseAlarmType();
        switch (baseAlarmType.getType()) {
            case DateAlarmType.ALARM_TYPE:
                nextTriggerDate = getNextTriggerDateForDate(alarmEntity);
                break;
            case EveryDayAlarmType.ALARM_TYPE:
            case OnceAlarmType.ALARM_TYPE:
                nextTriggerDate = getNextTriggerDateForOnce(alarmEntity.getTime());
                break;
            case WeekAlarmType.ALARM_TYPE:
                nextTriggerDate = getNextTriggerDateForWeek(alarmEntity);
                break;
            case HolidayAlarmType.ALARM_TYPE:
                nextTriggerDate = getNextTriggerDateForHoliday(alarmEntity.getTime());
                break;
            case WorkingDayAlarmType.ALARM_TYPE:
                nextTriggerDate = getNextTriggerDateForWorkingDay(alarmEntity.getTime());
                break;
            default:
                throw new IllegalArgumentException("Unknown alarm type: " + baseAlarmType.getType());
        }
        return nextTriggerDate;
    }

    public static int[] getNextTriggerTimeLeft(AlarmEntity alarmEntity) {
        int[] NextTriggerTimeLeft = new int[]{0, 0, 0};
        LocalDate nextTriggerDate = getNextTriggerDate(alarmEntity);
        if (nextTriggerDate != null) {
            LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
            LocalDateTime nextTriggerTime = LocalDateTime.of(nextTriggerDate, alarmEntity.getTime());
            int days = (int) ChronoUnit.DAYS.between(now, nextTriggerTime);
            LocalDateTime adjustedDateTime1 = now.plusDays(days);
            int hours = (int) ChronoUnit.HOURS.between(adjustedDateTime1, nextTriggerTime);
            LocalDateTime adjustedDateTime2 = adjustedDateTime1.plusHours(hours);
            int minutes = (int) ChronoUnit.MINUTES.between(adjustedDateTime2, nextTriggerTime);
            NextTriggerTimeLeft = new int[]{days, hours, minutes};
        }
        return NextTriggerTimeLeft;
    }

    public static long getNextTriggerTime(AlarmEntity alarmEntity) {
        LocalDate nextTriggerDate = getNextTriggerDate(alarmEntity);
        if (nextTriggerDate == null) {
            throw new IllegalArgumentException("Unknown NextTriggerDate");
        }
        LocalDateTime nextTriggerTime = LocalDateTime.of(nextTriggerDate, alarmEntity.getTime());
        Log.i("terry", "nextTriggerTime:" + nextTriggerTime);
        return nextTriggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static void setAlarm(Context context, AlarmEntity alarm) {
        long triggerTime = getNextTriggerTime(alarm);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmId", alarm.getId());
        intent.putExtra("name", alarm.getName());
        intent.putExtra("triggerTime", triggerTime);
        intent.putExtra("alarmType", alarm.getBaseAlarmType().getType());
        intent.putExtra("repeatDesc", alarm.getBaseAlarmType().getRepeatDesc());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) alarm.getId(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle( AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

    public static void cancelAlarm(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        alarmManager.cancel(pendingIntent);
    }
}