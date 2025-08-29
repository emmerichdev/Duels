package com.meteordevelopments.duels.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

    private DateUtil() {
    }

    public static String formatDate(final Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatDatetime(final long millis) {
        return TIMESTAMP_FORMAT.format(millis);
    }

    public static String format(long seconds) {
        if (seconds <= 0) {
            return "updating...";
        }

        TimeCalculator.TimeComponents time = new TimeCalculator.TimeComponents(seconds);
        StringBuilder sb = new StringBuilder();

        if (time.years > 0) {
            sb.append(time.years).append("yr");
        }

        if (time.months > 0) {
            sb.append(time.months).append("mo");
        }

        if (time.weeks > 0) {
            sb.append(time.weeks).append("w");
        }

        if (time.days > 0) {
            sb.append(time.days).append("d");
        }

        if (time.hours > 0) {
            sb.append(time.hours).append("h");
        }

        if (time.minutes > 0) {
            sb.append(time.minutes).append("m");
        }

        if (time.seconds > 0) {
            sb.append(time.seconds).append("s");
        }

        return sb.toString();
    }

    public static String formatMilliseconds(long ms) {
        if (ms < 1000) {
            return "0 second";
        }

        long totalSeconds = TimeCalculator.millisecondsToSeconds(ms);
        TimeCalculator.TimeComponents time = new TimeCalculator.TimeComponents(totalSeconds);
        StringBuilder builder = new StringBuilder();

        boolean hasContent = false;
        hasContent |= TimeCalculator.appendTimeUnit(builder, hasContent, time.years, "year");
        hasContent |= TimeCalculator.appendTimeUnit(builder, hasContent, time.months, "month");
        hasContent |= TimeCalculator.appendTimeUnit(builder, hasContent, time.weeks, "week");
        hasContent |= TimeCalculator.appendTimeUnit(builder, hasContent, time.days, "day");
        hasContent |= TimeCalculator.appendTimeUnit(builder, hasContent, time.hours, "hour");
        hasContent |= TimeCalculator.appendTimeUnit(builder, hasContent, time.minutes, "minute");
        TimeCalculator.appendTimeUnit(builder, hasContent, time.seconds, "second");

        return builder.toString();
    }
}
