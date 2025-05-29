package com.example.myalarm.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.receiver.AlarmReceiver;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class AlarmUtils {
    public static long getTriggerTime(AlarmEntity alarmEntity) {
        LocalDateTime now = LocalDateTime.now();
        String[] timeArray = alarmEntity.getTime().split(":");
        LocalDateTime triggerTime = now.withHour(Integer.parseInt(timeArray[0])).withMinute(Integer.parseInt(timeArray[1])).withSecond(0).withNano(0);
        Log.i("terry", triggerTime.toString());
        return triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void setAlarm(Context context, AlarmEntity alarm) {
        long triggerTime = getTriggerTime(alarm);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmId", alarm.getId());
        intent.putExtra("name", alarm.getName());
        intent.putExtra("triggerTime", triggerTime);
        intent.putExtra("alarmType", alarm.getBaseAlarmType().getType());
        intent.putExtra("repeatDesc", alarm.getBaseAlarmType().getRepeatDesc());

        PendingIntent pendingIntent = null;
        try {
            pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (int) alarm.getId(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("terry", "setAlarm end");
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