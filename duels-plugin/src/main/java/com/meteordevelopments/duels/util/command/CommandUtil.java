package com.meteordevelopments.duels.util.command;

import com.meteordevelopments.duels.config.Lang;

public final class CommandUtil {

    private CommandUtil() {
    }

    public static SettingsDisplay formatSettingsDisplay(com.meteordevelopments.duels.setting.Settings settings, Lang lang) {
        final String kit = settings.getKit() != null ? settings.getKit().getName() : lang.getMessage("GENERAL.not-selected");
        final String ownInventory = settings.isOwnInventory() ? lang.getMessage("GENERAL.enabled") : lang.getMessage("GENERAL.disabled");
        final String arena = settings.getArena() != null ? settings.getArena().getName() : lang.getMessage("GENERAL.random");
        return new SettingsDisplay(kit, ownInventory, arena);
    }

}
