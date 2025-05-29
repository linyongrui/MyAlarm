package com.example.myalarm.data.converter;

import androidx.room.TypeConverter;

import com.example.myalarm.alarmtype.BaseAlarmType;
import com.example.myalarm.alarmtype.DateAlarmType;
import com.example.myalarm.alarmtype.EveryDayAlarmType;
import com.example.myalarm.alarmtype.HolidayAlarmType;
import com.example.myalarm.alarmtype.OnceAlarmType;
import com.example.myalarm.alarmtype.WeekAlarmType;
import com.example.myalarm.alarmtype.WorkingDayAlarmType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AlarmTypeConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String alarmToString(BaseAlarmType alarm) {
        if (alarm == null) return null;
        return gson.toJson(new AlarmWrapper(alarm.getType(), alarm));
    }

    @TypeConverter
    public static BaseAlarmType stringToAlarm(String json) {
        if (json == null) return null;

        Type wrapperType = new TypeToken<AlarmWrapper>() {
        }.getType();
        AlarmWrapper wrapper = gson.fromJson(json, wrapperType);

        switch (wrapper.type) {
            case DateAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarm), DateAlarmType.class);
            case EveryDayAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarm), EveryDayAlarmType.class);
            case HolidayAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarm), HolidayAlarmType.class);
            case OnceAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarm), OnceAlarmType.class);
            case WeekAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarm), WeekAlarmType.class);
            case WorkingDayAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarm), WorkingDayAlarmType.class);
            default:
                throw new IllegalArgumentException("Unknown alarm type: " + wrapper.type);
        }
    }

    private static class AlarmWrapper {
        String type;
        Object alarm;

        public AlarmWrapper(String type, Object alarm) {
            this.type = type;
            this.alarm = alarm;
        }
    }
}