package com.meteordevelopments.duels.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CC {

    // Adventure serializers
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final ANSIComponentSerializer ANSI_SERIALIZER = ANSIComponentSerializer.ansi();
    private static final Pattern HEX_PATTERN = Pattern.compile("#([0-9A-Fa-f]{6})");

    public static String translate(String input) {
        if (input == null) return "";
        
        String processedInput = expandHexColors(input);
        
        Component component = LEGACY_SERIALIZER.deserialize(processedInput);
        
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
    
    public static String translateConsole(String input) {
        if (input == null) return "";
        
        String processedInput = expandHexColors(input);
        
        Component component = LEGACY_SERIALIZER.deserialize(processedInput);
        
        return ANSI_SERIALIZER.serialize(component);
    }
    
    public static String translateConsoleFromSection(String input) {
        if (input == null) return "";
        
        // Convert section symbols back to ampersand for processing
        String ampersandInput = input.replace('§', '&');
        String processedInput = expandHexColors(ampersandInput);
        
        Component component = LEGACY_SERIALIZER.deserialize(processedInput);
        
        return ANSI_SERIALIZER.serialize(component);
    }
    
    private static String expandHexColors(String input) {
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder result = new StringBuilder();
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("&x");
            for (char c : hex.toCharArray()) {
                replacement.append("&").append(c);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement.toString()));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }

    public static String getTimeDifferenceAndColor(long start, long end) {
        NamedTextColor color = getColorBasedOnSize((end - start), 20, 5000, 10000);
        Component component = Component.text((end - start) + "ms").color(color);
        return LEGACY_SERIALIZER.serialize(component);
    }
    
    public static NamedTextColor getColorBasedOnSize(long num, int low, int med, int high) {
        if (num <= low) {
            return NamedTextColor.GREEN;
        } else if (num <= med) {
            return NamedTextColor.YELLOW;
        } else if (num <= high) {
            return NamedTextColor.RED;
        } else {
            return NamedTextColor.DARK_RED;
        }
    }
}