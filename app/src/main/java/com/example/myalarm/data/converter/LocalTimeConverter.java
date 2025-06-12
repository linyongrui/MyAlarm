package com.example.myalarm.data.converter;

import androidx.room.TypeConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeConverter {

    @TypeConverter
    public static String fromLocalTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time == null ? null : time.format(formatter);
    }

    @TypeConverter
    public static LocalTime toLocalTime(String timeString) {
        return timeString == null ? null : LocalTime.parse(timeString);
    }
}