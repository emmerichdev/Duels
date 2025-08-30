package com.meteordevelopments.duels.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CC {

    // Adventure serializers
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final ANSIComponentSerializer ANSI_SERIALIZER = ANSIComponentSerializer.ansi();

    public static String translate(String input) {
        if (input == null) return "";
        
        // For in-game text: Convert & codes to § codes that Minecraft clients understand
        Component component = LEGACY_SERIALIZER.deserialize(input);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
    
    public static String translateConsole(String input) {
        if (input == null) return "";
        
        // For console: Convert to ANSI escape codes for terminal colors
        Component component = LEGACY_SERIALIZER.deserialize(input);
        return ANSI_SERIALIZER.serialize(component);
    }
    
    public static String getTimeDifferenceAndColor(long start, long end) {
        NamedTextColor color = getColorBasedOnSize((end - start), 20, 5000, 10000);
        Component component = Component.text((end - start) + "ms").color(color);
        return ANSI_SERIALIZER.serialize(component);
    }
    
    public static String getTimeDifferenceAndColorConsole(long start, long end) {
        return getTimeDifferenceAndColor(start, end);
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