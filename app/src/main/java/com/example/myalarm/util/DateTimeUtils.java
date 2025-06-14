package com.example.myalarm.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtils {
    public static LocalDateTime long2LocalDateTime(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    public static long localDateTime2long(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    }

    public static String getDateStr(LocalDate localDate) {
        String dateStr;
        LocalDate today = LocalDate.now();
        if (today.equals(localDate)) {
            dateStr = "今天";
        } else if (today.plusDays(1L).equals(localDate)) {
            dateStr = "明天";
        } else {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M月d日", Locale.CHINA);
            dateStr = localDate.format(dateFormatter);
        }
        return dateStr;
    }
}
