package com.meteordevelopments.duels.listeners;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.arena.ArenaManagerImpl;
import com.meteordevelopments.duels.spectate.SpectateManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;

public class LingerPotionListener implements Listener {

    private final ArenaManagerImpl arenaManager;
    private final SpectateManagerImpl spectateManager;

    public LingerPotionListener(final DuelsPlugin plugin) {
        this.arenaManager = plugin.getArenaManager();
        this.spectateManager = plugin.getSpectateManager();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(final AreaEffectCloudApplyEvent event) {
        if (!(event.getEntity().getSource() instanceof Player player)) {
            return;
        }

        if (!arenaManager.isInMatch(player)) {
            return;
        }

        event.getAffectedEntities().removeIf(entity -> entity instanceof Player && spectateManager.isSpectating((Player) entity));
    }
}
