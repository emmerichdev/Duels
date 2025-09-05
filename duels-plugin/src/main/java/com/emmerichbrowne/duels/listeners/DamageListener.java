package com.emmerichbrowne.duels.listeners;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaImpl;
import com.emmerichbrowne.duels.arena.ArenaManagerImpl;
import com.emmerichbrowne.duels.party.PartyManagerImpl;
import com.emmerichbrowne.duels.util.EventUtil;
import com.emmerichbrowne.duels.command.AutoRegister;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@AutoRegister
public class DamageListener implements Listener {

    private final ArenaManagerImpl arenaManager;
    private final PartyManagerImpl partyManager;

    public DamageListener(final DuelsPlugin plugin) {
        this.arenaManager = plugin.getArenaManager();
        this.partyManager = plugin.getPartyManager();

        if (plugin.getConfiguration().isForceAllowCombat()) {
            plugin.doSyncAfter(() -> Bukkit.getPluginManager().registerEvents(this, plugin), 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(final EntityDamageByEntityEvent event) {
        if (!event.isCancelled() || !(event.getEntity() instanceof Player damaged)) {
            return;
        }

        final Player damager = EventUtil.getDamager(event);

        if (damager == null) {
            return;
        }

        final ArenaImpl arena = arenaManager.get(damaged);

        if (arena == null || !arenaManager.isInMatch(damager) || arena.isEndGame() || !partyManager.canDamage(damager, damaged)) {
            return;
        }

        event.setCancelled(false);
    }
}