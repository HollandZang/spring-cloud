package com.holland.common.utils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;

public class DateUtil {

    public static String toStr(long timestamp, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(new Date(timestamp));
    }

    public static String toStr(Temporal temporal, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(temporal);
    }

    public static Date toDate(Temporal temporal) {
        LocalDateTime localDateTime = null;
        if (temporal instanceof LocalDateTime) {
            localDateTime = (LocalDateTime) temporal;
        }
        if (temporal instanceof LocalDate) {
            localDateTime = ((LocalDate) temporal).atTime(LocalTime.MIN);
        }
        if (temporal instanceof LocalTime) {
            localDateTime = ((LocalTime) temporal).atDate(LocalDate.of(1970, Month.FEBRUARY, 1));
        }
        return Date.from(localDateTime.toInstant(ZoneOffset.of("+8")));
    }

    public static <T extends Temporal> T toTemporal(String date, String pattern, Class<T> temporal) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        if (temporal != null && "LocalDate".equals(temporal.getSimpleName())) {
            return (T) LocalDate.parse(date, formatter);
        }
        if (temporal != null && "LocalTime".equals(temporal.getSimpleName())) {
            return (T) LocalTime.parse(date, formatter);
        }
        return (T) LocalDateTime.parse(date, formatter);
    }

    public static <T extends Temporal> T toTemporal(String date, String pattern) {
        return toTemporal(date, pattern, null);
    }
}
