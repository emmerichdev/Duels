package com.meteordevelopments.duels.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CC {

    public static String translate(String input) {
        if (input == null) return "";
        
        // Handle hex colors &#RRGGBB
        if (Pattern.compile("&#[0-9A-Fa-f]{6}").matcher(input).find()) {
            Matcher matcher = Pattern.compile("&(#[0-9A-Fa-f]{6})").matcher(input);
            while (matcher.find()) {
                String hexColor = matcher.group(1);
                TextColor color = TextColor.fromHexString(hexColor);
                if (color != null) {
                    input = input.replaceFirst(
                            Pattern.quote(matcher.group()),
                            LegacyComponentSerializer.legacyAmpersand().serialize(
                                net.kyori.adventure.text.Component.text("").color(color)
                            )
                    );
                }
            }
        }
        
        // Use Adventure's legacy serializer for standard color codes
        return LegacyComponentSerializer.legacyAmpersand().serialize(
                LegacyComponentSerializer.legacyAmpersand().deserialize(input)
        );
    }
    
    public static String getTimeDifferenceAndColor(long start, long end) {
        NamedTextColor color = getColorBasedOnSize((end - start), 20, 5000, 10000);
        return LegacyComponentSerializer.legacyAmpersand().serialize(
                net.kyori.adventure.text.Component.text((end - start) + "ms").color(color)
        );
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