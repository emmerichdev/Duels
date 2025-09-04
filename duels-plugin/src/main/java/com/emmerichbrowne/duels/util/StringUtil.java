package com.emmerichbrowne.duels.util;


import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

public final class StringUtil {

    private static final Pattern ALPHANUMERIC = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final TreeMap<Integer, String> ROMAN_NUMERALS = new TreeMap<>();
    static {
        ROMAN_NUMERALS.put(1000, "M");
        ROMAN_NUMERALS.put(900, "CM");
        ROMAN_NUMERALS.put(500, "D");
        ROMAN_NUMERALS.put(400, "CD");
        ROMAN_NUMERALS.put(100, "C");
        ROMAN_NUMERALS.put(90, "XC");
        ROMAN_NUMERALS.put(50, "L");
        ROMAN_NUMERALS.put(40, "XL");
        ROMAN_NUMERALS.put(10, "X");
        ROMAN_NUMERALS.put(9, "IX");
        ROMAN_NUMERALS.put(5, "V");
        ROMAN_NUMERALS.put(4, "IV");
        ROMAN_NUMERALS.put(1, "I");
    }

    private StringUtil() {
    }

    // Source: https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
    public static String toRoman(final int number) {
        if (number <= 0) {
            return String.valueOf(number);
        }

        int key = ROMAN_NUMERALS.floorKey(number);

        if (number == key) {
            return ROMAN_NUMERALS.get(number);
        }

        return ROMAN_NUMERALS.get(key) + toRoman(number - key);
    }

    public static String fromList(final List<?> list) {
        StringBuilder builder = new StringBuilder();

        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i).toString()).append(i + 1 != list.size() ? "\n" : "");
            }
        }

        return builder.toString();
    }

    public static String parse(final Location location) {
        return "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")";
    }

    public static boolean isAlphanumeric(final String input) {
        return ALPHANUMERIC.matcher(input.replace(" ", "")).matches();
    }

    // Direct delegation to Apache Commons Lang - these are just thin wrappers
    public static String join(final Object[] array, final String separator, final int startIndex, final int endIndex) {
        return StringUtils.join(array, separator, startIndex, endIndex);
    }

    public static String join(final Collection<?> collection, final String separator) {
        return StringUtils.join(collection, separator);
    }

    public static String capitalize(final String s) {
        return StringUtils.capitalize(s);
    }

    public static boolean containsIgnoreCase(final String str, final String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }
}