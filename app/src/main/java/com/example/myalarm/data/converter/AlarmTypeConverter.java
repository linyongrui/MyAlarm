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
    public static String alarmTypeToString(BaseAlarmType alarmType) {
        if (alarmType == null) return null;
        return gson.toJson(new AlarmTypeWrapper(alarmType.getType(), alarmType));
    }

    @TypeConverter
    public static BaseAlarmType stringToAlarmType(String json) {
        if (json == null) return null;

        Type wrapperType = new TypeToken<AlarmTypeWrapper>() {
        }.getType();
        AlarmTypeWrapper wrapper = gson.fromJson(json, wrapperType);

        switch (wrapper.type) {
            case DateAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarmType), DateAlarmType.class);
            case EveryDayAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarmType), EveryDayAlarmType.class);
            case HolidayAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarmType), HolidayAlarmType.class);
            case OnceAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarmType), OnceAlarmType.class);
            case WeekAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarmType), WeekAlarmType.class);
            case WorkingDayAlarmType.ALARM_TYPE:
                return gson.fromJson(gson.toJson(wrapper.alarmType), WorkingDayAlarmType.class);
            default:
                throw new IllegalArgumentException("Unknown alarm type: " + wrapper.type);
        }
    }

    private static class AlarmTypeWrapper {
        String type;
        Object alarmType;

        public AlarmTypeWrapper(String type, Object alarmType) {
            this.type = type;
            this.alarmType = alarmType;
        }
    }
}