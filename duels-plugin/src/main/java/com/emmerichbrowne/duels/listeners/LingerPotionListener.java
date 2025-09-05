package com.emmerichbrowne.duels.listeners;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaManagerImpl;
import com.emmerichbrowne.duels.spectate.SpectateManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import com.emmerichbrowne.duels.command.AutoRegister;

@AutoRegister
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
