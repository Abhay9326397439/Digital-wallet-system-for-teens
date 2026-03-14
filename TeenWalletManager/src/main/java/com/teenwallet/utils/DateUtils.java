package com.teenwallet.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String formatForFile(LocalDateTime dateTime) {
        return dateTime.format(FILE_DATE_FORMATTER);
    }

    public static LocalDateTime parseFromFile(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, FILE_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return LocalDateTime.now();
        }
    }

    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }

    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static boolean isToday(LocalDateTime dateTime) {
        return dateTime.toLocalDate().equals(LocalDate.now());
    }

    public static boolean isThisWeek(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalDate now = LocalDate.now();
        LocalDate weekAgo = now.minusDays(7);
        return !date.isBefore(weekAgo) && !date.isAfter(now);
    }

    public static boolean isThisMonth(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalDate now = LocalDate.now();
        return date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
    }

    public static String getMonthName(int month) {
        return java.time.Month.of(month).toString();
    }

    public static String getDayName(LocalDate date) {
        return date.getDayOfWeek().toString();
    }

    public static LocalDate getStartOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1);
    }

    public static LocalDate getEndOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
    }
}