package com.meteordevelopments.duels.util.compat;

import com.meteordevelopments.duels.util.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

public final class Skulls {

    private Skulls() {
    }

    public static void setProfile(final SkullMeta meta, final Player player) {
        try {
            meta.setOwningPlayer(player);
        } catch (Exception ex) {
            Log.error("Failed to set skull profile for player: " + player.getName(), ex);
        }
    }
}
