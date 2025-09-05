package com.emmerichbrowne.duels.listeners;

import com.emmerichbrowne.duels.DuelsPlugin;

import com.emmerichbrowne.duels.arena.ArenaManagerImpl;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.spectate.SpectateManagerImpl;
import com.emmerichbrowne.duels.teleport.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;
import com.emmerichbrowne.duels.command.AutoRegister;

@AutoRegister
public class TeleportListener implements Listener {

    private final Lang lang;
    private final ArenaManagerImpl arenaManager;
    private final SpectateManagerImpl spectateManager;

    public TeleportListener(final DuelsPlugin plugin) {
        this.lang = plugin.getLang();
        this.arenaManager = plugin.getArenaManager();
        this.spectateManager = plugin.getSpectateManager();

        if (plugin.getConfiguration().isPreventTpToMatchPlayers()) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
    }

    private boolean isSimilar(final Location first, final Location second) {
        return Math.abs(first.getX() - second.getX()) + Math.abs(first.getY() - second.getY()) + Math.abs(first.getZ() - second.getZ()) < 5;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        if (player.isOp() || player.isDead() || player.hasPermission("duels.admin") || player.hasPermission("duels.teleport.bypass") ||
                player.hasMetadata(Teleport.METADATA_KEY) || arenaManager.isInMatch(player) || spectateManager.isSpectating(player)) {
            return;
        }

        final Location to = event.getTo();
        final Set<Player> players = arenaManager.getPlayers();
        players.addAll(spectateManager.getAllSpectators());

        for (final Player target : players) {
            if (target == null || player.equals(target) || !target.isOnline() || !isSimilar(target.getLocation(), to)) {
                continue;
            }

            event.setCancelled(true);
            lang.sendMessage(player, "ERROR.duel.prevent-teleportation");
            return;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onTP(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if (!arenaManager.isInMatch(player)) {
            return;
        }
        player.closeInventory();
    }
}
