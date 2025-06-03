package com.example.myalarm.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.myalarm.alarmtype.BaseAlarmType;
import com.example.myalarm.alarmtype.DateAlarmType;
import com.example.myalarm.alarmtype.EveryDayAlarmType;
import com.example.myalarm.alarmtype.HolidayAlarmType;
import com.example.myalarm.alarmtype.OnceAlarmType;
import com.example.myalarm.alarmtype.WeekAlarmType;
import com.example.myalarm.alarmtype.WorkingDayAlarmType;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.entity.HolidayEntity;
import com.example.myalarm.receiver.AlarmReceiver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AlarmUtils {
    public static LocalDate getNextTriggerDate(AlarmEntity alarmEntity) {
        LocalDate today = LocalDate.now();
        LocalTime timeNow = LocalTime.now().withSecond(0).withNano(0);
        boolean isBeforeNow = !alarmEntity.getTime().isAfter(timeNow);

        List<HolidayEntity> holidayEntities = HolidayUtils.getHolidays();

        LocalDate nextTriggerDate = null;
        BaseAlarmType baseAlarmType = alarmEntity.getBaseAlarmType();
        switch (baseAlarmType.getType()) {
            case DateAlarmType.ALARM_TYPE:
                break;
            case EveryDayAlarmType.ALARM_TYPE:
            case OnceAlarmType.ALARM_TYPE:
                nextTriggerDate = isBeforeNow ? today.plusDays(1L) : today;
                break;
            case WeekAlarmType.ALARM_TYPE:
                int todayOfWeekNumber = today.getDayOfWeek().getValue();
                todayOfWeekNumber = todayOfWeekNumber == 7 ? 0 : todayOfWeekNumber;
                int[] weekCheck = ((WeekAlarmType) baseAlarmType).getWeekDayCheck();
                LocalDate nextTriggerDateCursor = LocalDate.now();
                LocalDate nextTriggerDateT1 = null;
                LocalDate nextTriggerDateT2 = null;
                for (int i = 0, index = todayOfWeekNumber; i < 7; i++) {
                    if (weekCheck[index] == 1) {
                        if (nextTriggerDateT1 == null) {
                            nextTriggerDateT1 = isBeforeNow && today.equals(nextTriggerDateCursor) ? nextTriggerDateCursor.plusDays(7L) : nextTriggerDateCursor;
                        } else if (nextTriggerDateT2 == null) {
                            nextTriggerDateT2 = nextTriggerDateCursor;
                            break;
                        }
                    }
                    index = index == 6 ? 0 : index + 1;
                    nextTriggerDateCursor = nextTriggerDateCursor.plusDays(1L);
                }
                if (nextTriggerDateT1 != null && nextTriggerDateT2 != null) {
                    nextTriggerDate = nextTriggerDateT1.isBefore(nextTriggerDateT2) ? nextTriggerDateT1 : nextTriggerDateT2;
                } else if (nextTriggerDateT1 != null) {
                    nextTriggerDate = nextTriggerDateT1;
                }
                break;
            case HolidayAlarmType.ALARM_TYPE:
                break;
            case WorkingDayAlarmType.ALARM_TYPE:
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
        System.out.println("nextTriggerTime:" + nextTriggerTime);
        return nextTriggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @SuppressLint("ScheduleExactAlarm")
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
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