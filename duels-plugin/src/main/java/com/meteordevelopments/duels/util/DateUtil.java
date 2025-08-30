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

        TimeComponents time = calculateTimeComponents(seconds);
        StringBuilder sb = new StringBuilder();

        if (time.years() > 0) {
            sb.append(time.years()).append("yr");
        }

        if (time.months() > 0) {
            sb.append(time.months()).append("mo");
        }

        if (time.weeks() > 0) {
            sb.append(time.weeks()).append("w");
        }

        if (time.days() > 0) {
            sb.append(time.days()).append("d");
        }

        if (time.hours() > 0) {
            sb.append(time.hours()).append("h");
        }

        if (time.minutes() > 0) {
            sb.append(time.minutes()).append("m");
        }

        if (time.seconds() > 0) {
            sb.append(time.seconds()).append("s");
        }

        return sb.toString();
    }

    public static String formatMilliseconds(long ms) {
        if (ms < 1000) {
            return "0 second";
        }

        long totalSeconds = ms / 1000;
        TimeComponents time = calculateTimeComponents(totalSeconds);

        StringBuilder builder = new StringBuilder();
        boolean hasContent = false;
        
        hasContent = appendTimeUnit(builder, hasContent, time.years(), "year");
        hasContent = appendTimeUnit(builder, hasContent, time.months(), "month");
        hasContent = appendTimeUnit(builder, hasContent, time.weeks(), "week");
        hasContent = appendTimeUnit(builder, hasContent, time.days(), "day");
        hasContent = appendTimeUnit(builder, hasContent, time.hours(), "hour");
        hasContent = appendTimeUnit(builder, hasContent, time.minutes(), "minute");
        appendTimeUnit(builder, hasContent, time.seconds(), "second");

        return builder.toString();
    }
    
    private static boolean appendTimeUnit(StringBuilder builder, boolean hasContent, long value, String unit) {
        if (value > 0) {
            if (hasContent) {
                builder.append(", ");
            }
            builder.append(value).append(" ").append(unit);
            if (value > 1) {
                builder.append("s");
            }
            return true;
        }
        return hasContent;
    }
    
    private static TimeComponents calculateTimeComponents(long totalSeconds) {
        final long secondsPerMonth = 2592000;
        final long totalMonths = totalSeconds / secondsPerMonth;
        
        long years = totalMonths / 12;
        long months = totalMonths % 12;
        
        long remaining = totalSeconds - (totalMonths * secondsPerMonth);
        
        long weeks = remaining / 604800;
        remaining -= weeks * 604800;
        
        long days = remaining / 86400;
        remaining -= days * 86400;
        
        long hours = remaining / 3600;
        remaining -= hours * 3600;
        
        long minutes = remaining / 60;
        long seconds = remaining - (minutes * 60);
        
        return new TimeComponents(years, months, weeks, days, hours, minutes, seconds);
    }
    
}
