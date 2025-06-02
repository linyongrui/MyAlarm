package com.example.myalarm.data.converter;

import androidx.room.TypeConverter;

import java.time.LocalTime;

public class LocalTimeConverter {

    @TypeConverter
    public static String fromLocalTime(LocalTime time) {
        return time == null ? null : time.toString();
    }

    @TypeConverter
    public static LocalTime toLocalTime(String timeString) {
        return timeString == null ? null : LocalTime.parse(timeString);
    }
}