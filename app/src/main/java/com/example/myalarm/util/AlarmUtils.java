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
import com.example.myalarm.dao.AlarmDao;
import com.example.myalarm.data.DatabaseClient;
import com.example.myalarm.entity.AlarmEntity;
import com.example.myalarm.receiver.AlarmReceiver;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AlarmUtils {
    public static AlarmDao alarmDao = DatabaseClient.getInstance()
            .getAlarmDatabase()
            .alarmDao();

    public static boolean isWorkingDay(LocalDate date) {

        Set<String> holidatSet = HolidayUtils.getHolidaySet();
        Set<String> transferWorkdaySet = HolidayUtils.getTransferWorkdaySet();

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

    public static LocalDate getNextTriggerDateForEveryDay(AlarmEntity alarmEntity) {
        LocalDate dateFrom = LocalDate.now();
        LocalTime timeFrom = LocalTime.now().withSecond(0).withNano(0);
        if (alarmEntity.isTempDisabled()) {
            LocalDateTime localDateTime = DateTimeUtils.long2LocalDateTime(alarmEntity.getNextTriggerTime());
            dateFrom = localDateTime.toLocalDate();
            timeFrom = localDateTime.toLocalTime();
        }

        LocalTime alarmTime = alarmEntity.getTime();
        boolean isBeforeNow = !alarmTime.isAfter(timeFrom);
        return isBeforeNow ? dateFrom.plusDays(1L) : dateFrom;
    }

    public static LocalDate getNextTriggerDateForOnce(LocalTime alarmTime) {
        LocalDate today = LocalDate.now();
        LocalTime timeNow = LocalTime.now().withSecond(0).withNano(0);
        boolean isBeforeNow = !alarmTime.isAfter(timeNow);
        return isBeforeNow ? today.plusDays(1L) : today;
    }

    public static LocalDate getNextTriggerDateForWeek(AlarmEntity alarmEntity) {
        LocalDate dateFrom = LocalDate.now();
        LocalTime timeFrom = LocalTime.now().withSecond(0).withNano(0);
        LocalDate nextTriggerDateCursor = LocalDate.now();
        if (alarmEntity.isTempDisabled()) {
            LocalDateTime localDateTime = DateTimeUtils.long2LocalDateTime(alarmEntity.getNextTriggerTime());
            dateFrom = localDateTime.toLocalDate();
            timeFrom = localDateTime.toLocalTime();
            nextTriggerDateCursor = localDateTime.toLocalDate();
        }

        boolean isBeforeNow = !alarmEntity.getTime().isAfter(timeFrom);

        WeekAlarmType weekAlarmType = (WeekAlarmType) alarmEntity.getBaseAlarmType();
        int[] weekCheck = weekAlarmType.getWeekDayCheck();
        boolean isSkipHoliday = weekAlarmType.getSkipHoliday();
        boolean isSkipWorkingDay = weekAlarmType.getSkipWorkingDay();
        if (weekCheck[0] == 0 && weekCheck[1] == 0 && weekCheck[2] == 0 && weekCheck[3] == 0 && weekCheck[4] == 0 && weekCheck[5] == 0 && weekCheck[6] == 0) {
            return null;
        }

        int todayOfWeekNumber = dateFrom.getDayOfWeek().getValue();
        todayOfWeekNumber = todayOfWeekNumber == 7 ? 0 : todayOfWeekNumber;
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
                        nextTriggerDateT1 = isBeforeNow && dateFrom.equals(nextTriggerDateCursor) ? nextTriggerDateCursor.plusDays(7L) : nextTriggerDateCursor;
                    } else if (nextTriggerDateT2 == null) {
                        nextTriggerDateT2 = nextTriggerDateCursor;
                        break;
                    }
                }
            }
            index = index == 6 ? 0 : index + 1;
            nextTriggerDateCursor = nextTriggerDateCursor.plusDays(1L);

            count++;
        } while ((isNextTriggerDateT1Get && count < 7) || (!isNextTriggerDateT1Get && count < 366));

        LocalDate nextTriggerDate = null;
        if (nextTriggerDateT1 != null && nextTriggerDateT2 != null) {
            nextTriggerDate = nextTriggerDateT1.isBefore(nextTriggerDateT2) ? nextTriggerDateT1 : nextTriggerDateT2;
        } else if (nextTriggerDateT1 != null) {
            nextTriggerDate = nextTriggerDateT1;
        }
        return nextTriggerDate;
    }

    public static LocalDate getNextTriggerDateForHoliday(AlarmEntity alarmEntity) {
        LocalTime alarmTime = alarmEntity.getTime();
        LocalDate dateFrom = LocalDate.now();
        LocalTime timeFrom = LocalTime.now().withSecond(0).withNano(0);
        if (alarmEntity.isTempDisabled()) {
            LocalDateTime localDateTime = DateTimeUtils.long2LocalDateTime(alarmEntity.getNextTriggerTime());
            dateFrom = localDateTime.toLocalDate();
            timeFrom = localDateTime.toLocalTime();
        }
        boolean isBeforeNow = !alarmTime.isAfter(timeFrom);

        LocalDate nextTriggerDate = null;
        LocalDate nextTriggerDateCursor = isBeforeNow ? dateFrom.plusDays(1L) : dateFrom;
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

    public static LocalDate getNextTriggerDateForWorkingDay(AlarmEntity alarmEntity) {
        LocalTime alarmTime = alarmEntity.getTime();
        LocalDate dateFrom = LocalDate.now();
        LocalTime timeFrom = LocalTime.now().withSecond(0).withNano(0);
        if (alarmEntity.isTempDisabled()) {
            LocalDateTime localDateTime = DateTimeUtils.long2LocalDateTime(alarmEntity.getNextTriggerTime());
            dateFrom = localDateTime.toLocalDate();
            timeFrom = localDateTime.toLocalTime();
        }
        boolean isBeforeNow = !alarmTime.isAfter(timeFrom);

        LocalDate nextTriggerDate = null;
        LocalDate nextTriggerDateCursor = isBeforeNow ? dateFrom.plusDays(1L) : dateFrom;
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
                nextTriggerDate = getNextTriggerDateForEveryDay(alarmEntity);
                break;
            case OnceAlarmType.ALARM_TYPE:
                nextTriggerDate = getNextTriggerDateForOnce(alarmEntity.getTime());
                break;
            case WeekAlarmType.ALARM_TYPE:
                nextTriggerDate = getNextTriggerDateForWeek(alarmEntity);
                break;
            case HolidayAlarmType.ALARM_TYPE:
                nextTriggerDate = getNextTriggerDateForHoliday(alarmEntity);
                break;
            case WorkingDayAlarmType.ALARM_TYPE:
                nextTriggerDate = getNextTriggerDateForWorkingDay(alarmEntity);
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

    public static String getNextTriggerTimeLeft(List<AlarmEntity> alarmEntities) {
        if (alarmEntities == null || alarmEntities.isEmpty()) {
            return "还没有闹钟，请点击下方 + 新建闹钟";
        }

        Optional<AlarmEntity> earlyAlarmEntity = alarmEntities.stream()
                .filter(alarmEntity -> !alarmEntity.isDisabled())
                .min(Comparator.comparingLong(AlarmEntity::getNextTriggerTime));
        if (earlyAlarmEntity.isPresent()) {
            long minNextTriggerTimeLong = earlyAlarmEntity.get().getNextTriggerTime();
            LocalDateTime minNextTriggerTime = DateTimeUtils.long2LocalDateTime(minNextTriggerTimeLong);
            LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

            int days = (int) ChronoUnit.DAYS.between(now, minNextTriggerTime);
            LocalDateTime adjustedDateTime1 = now.plusDays(days);
            int hours = (int) ChronoUnit.HOURS.between(adjustedDateTime1, minNextTriggerTime);
            LocalDateTime adjustedDateTime2 = adjustedDateTime1.plusHours(hours);
            int minutes = (int) ChronoUnit.MINUTES.between(adjustedDateTime2, minNextTriggerTime);
            StringBuilder timeLeftBuilder = new StringBuilder();
            timeLeftBuilder.append("距离下次响铃还有");
            if (days > 0) {
                timeLeftBuilder.append(days + "天");
            }
            if (hours > 0) {
                timeLeftBuilder.append(hours + "小时");
            }
            if (minutes > 0) {
                timeLeftBuilder.append(minutes + "分钟");
            }
            return timeLeftBuilder.toString();
        } else {
            return "请开启已有的闹钟，或者点击下方 + 新建闹钟";
        }
    }

    public static long getNextTriggerTime(AlarmEntity alarmEntity) {
        LocalDate nextTriggerDate = getNextTriggerDate(alarmEntity);
        if (nextTriggerDate == null) {
            throw new IllegalArgumentException("Unknown NextTriggerDate");
        }
        LocalDateTime nextTriggerTime = LocalDateTime.of(nextTriggerDate, alarmEntity.getTime());
        Log.i("terry", "nextTriggerTime:" + nextTriggerTime);
        return DateTimeUtils.localDateTime2long(nextTriggerTime);
    }

    public static void setAlarm(Context context, AlarmEntity alarm, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmId", alarm.getId());
        intent.putExtra("alarmName", alarm.getName() == null || alarm.getName().isBlank() ? "闹钟" : alarm.getName());
        intent.putExtra("ringtoneProgress", alarm.getRingtoneProgress());
        intent.putExtra("isVibrator", alarm.isVibrator());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getNextTriggerTime(), pendingIntent);
    }

    public static void cancelAlarm(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }

    public static void cancelAllAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancelAll();
    }

    public static void saveAlarm(Context context, AlarmEntity newAlarmEntity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                newAlarmEntity.setNextTriggerTime(getNextTriggerTime(newAlarmEntity));
                long id = alarmDao.insertAlarmEntity(newAlarmEntity);
                newAlarmEntity.setId(id);
                setAlarm(context, newAlarmEntity, (int) id * 10);
            }
        }).start();
    }

    public static void updateAlarmEnabled(Context context, AlarmEntity alarmEntity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long alarmId = alarmEntity.getId();
                boolean disabled = alarmEntity.isDisabled();
                boolean tempDisabled = alarmEntity.isTempDisabled();

                AlarmEntity originalAlarmEntity = alarmDao.getActiveAlarmById(alarmId);
                originalAlarmEntity.setDisabled(disabled);
                originalAlarmEntity.setTempDisabled(tempDisabled);

                int originalRequestCode = getRequestCode(alarmId, originalAlarmEntity.getRequestCodeSeq(), false);
                int newRequestCode = getRequestCode(alarmId, originalAlarmEntity.getRequestCodeSeq(), true);
                if (disabled) {
                    originalAlarmEntity.setTempDisableTriggerTime(0);
                    originalAlarmEntity.setNextTriggerTime(0);
                    cancelAlarm(context, originalRequestCode);

                } else if (tempDisabled) {
                    cancelAlarm(context, originalRequestCode);
                    originalAlarmEntity.setTempDisableTriggerTime(originalAlarmEntity.getNextTriggerTime());
                    originalAlarmEntity.setNextTriggerTime(getNextTriggerTime(originalAlarmEntity));
                    originalAlarmEntity.setRequestCodeSeq(alarmEntity.getRequestCodeSeq() + 1);
                    setAlarm(context, originalAlarmEntity, newRequestCode);

                } else {
                    cancelAlarm(context, originalRequestCode);
                    originalAlarmEntity.setTempDisableTriggerTime(0);
                    originalAlarmEntity.setNextTriggerTime(getNextTriggerTime(originalAlarmEntity));
                    originalAlarmEntity.setRequestCodeSeq(alarmEntity.getRequestCodeSeq() + 1);
                    setAlarm(context, originalAlarmEntity, newRequestCode);
                }
                alarmDao.updateAlarmEntity(originalAlarmEntity);
            }
        }).start();
    }

    public static void updateAlarm(Context context, AlarmEntity alarmEntity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int originalRequestCode = getRequestCode(alarmEntity.getId(), alarmEntity.getRequestCodeSeq(), false);
                int newRequestCode = getRequestCode(alarmEntity.getId(), alarmEntity.getRequestCodeSeq(), true);

                alarmEntity.setNextTriggerTime(getNextTriggerTime(alarmEntity));
                alarmEntity.setRequestCodeSeq(alarmEntity.getRequestCodeSeq() + 1);
                alarmDao.updateAlarmEntity(alarmEntity);
                cancelAlarm(context, originalRequestCode);
                setAlarm(context, alarmEntity, newRequestCode);
            }
        }).start();
    }

    public static void deleteAlarmsByIds(Context context, Set<Long> ids) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AlarmEntity> alarmEntities = alarmDao.getAlarmByIds(ids);
                alarmDao.deleteAlarmByIds(ids);
                for (AlarmEntity alarmEntity : alarmEntities) {
                    int originalRequestCode = getRequestCode(alarmEntity.getId(), alarmEntity.getRequestCodeSeq(), false);
                    cancelAlarm(context, originalRequestCode);
                }
            }
        }).start();
    }

    public static void deleteAllAlarms(Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                alarmDao.deleteAllAlarm();
                cancelAllAlarm(context);
            }
        }).start();
    }

    public static void setNextAlarm(Context context, long alarmId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AlarmEntity alarmEntity = alarmDao.getActiveAlarmById(alarmId);
                if (alarmEntity == null) {
                    return;
                }
                alarmEntity.setAlreadyRingTimes(alarmEntity.getAlreadyRingTimes() + 1);
                if (alarmEntity.getAlreadyRingTimes() < alarmEntity.getRingTimes()) {
                    int newRequestCode = getRequestCode(alarmId, alarmEntity.getRequestCodeSeq(), true);
                    alarmEntity.setRequestCodeSeq(alarmEntity.getRequestCodeSeq() + 1);
                    alarmEntity.setNextTriggerTime(alarmEntity.getNextTriggerTime() + alarmEntity.getRingInterval() * 60000L);
                    alarmDao.updateAlarmEntity(alarmEntity);
                    setAlarm(context, alarmEntity, newRequestCode);

                } else {
                    String alarmType = alarmEntity.getBaseAlarmType().getType();
                    alarmEntity.setAlreadyRingTimes(0);
                    if (OnceAlarmType.ALARM_TYPE.equals(alarmType)) {
                        alarmEntity.setDisabled(true);
                        alarmEntity.setTempDisabled(false);
                        alarmEntity.setNextTriggerTime(0);
                        alarmDao.updateAlarmEntity(alarmEntity);
                    } else {
                        int newRequestCode = getRequestCode(alarmId, alarmEntity.getRequestCodeSeq(), true);
                        alarmEntity.setRequestCodeSeq(alarmEntity.getRequestCodeSeq() + 1);
                        alarmEntity.setNextTriggerTime(getNextTriggerTime(alarmEntity));
                        alarmDao.updateAlarmEntity(alarmEntity);
                        setAlarm(context, alarmEntity, newRequestCode);
                    }

                }
            }
        }).start();
    }

    public static int getRequestCode(long alarmId, int originalRequestCodeSeq, boolean isGenerateNew) {
        int tempRequestCodeSeq = isGenerateNew ? originalRequestCodeSeq + 1 : originalRequestCodeSeq;
        return (int) (alarmId * 10 + tempRequestCodeSeq % 10);
    }
}