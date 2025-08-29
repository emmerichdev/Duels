package com.meteordevelopments.duels.util;

public final class TimeCalculator {
    
    private TimeCalculator() {
    }

    public static class TimeComponents {
        public final long years;
        public final long months;
        public final long weeks;
        public final long days;
        public final long hours;
        public final long minutes;
        public final long seconds;

        public TimeComponents(long totalSeconds) {
            this.years = totalSeconds / 31556952;
            long remaining = totalSeconds - (this.years * 31556952);
            
            this.months = remaining / 2592000;
            remaining -= this.months * 2592000;
            
            this.weeks = remaining / 604800;
            remaining -= this.weeks * 604800;
            
            this.days = remaining / 86400;
            remaining -= this.days * 86400;
            
            this.hours = remaining / 3600;
            remaining -= this.hours * 3600;
            
            this.minutes = remaining / 60;
            this.seconds = remaining - (this.minutes * 60);
        }
    }

    public static long millisecondsToSeconds(long ms) {
        return ms / 1000 + (ms % 1000 > 0 ? 1 : 0);
    }

    public static boolean appendTimeUnit(StringBuilder builder, boolean hasExistingContent, 
                                       long value, String unit) {
        if (value > 0) {
            if (hasExistingContent) {
                builder.append(" ");
            }
            builder.append(value).append(value > 1 ? " " + unit + "s" : " " + unit);
            return true;
        }
        return false;
    }
}
