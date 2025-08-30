package com.meteordevelopments.duels.listeners;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.arena.ArenaManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PotionListener implements Listener {

    private final ArenaManagerImpl arenaManager;

    public PotionListener(final DuelsPlugin plugin) {
        this.arenaManager = plugin.getArenaManager();

        if (plugin.getConfiguration().isRemoveEmptyBottle()) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void on(final PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();

        if (!arenaManager.isInMatch(player)) {
            return;
        }

        final ItemStack item = event.getItem();

        if (!item.getType().name().endsWith("POTION")) {
            return;
        }

        DuelsPlugin.getMorePaperLib().scheduling().entitySpecificScheduler(player).run(() -> {
            if (item.getAmount() <= 1) {
                    final ItemStack held = player.getInventory().getItemInMainHand();

                    if (held.getType() == Material.GLASS_BOTTLE) {
                        player.getInventory().setItemInMainHand(null);
                    } else {
                        player.getInventory().setItemInOffHand(null);
                    }
            } else {
                player.getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE, 1));
            }
        }, null);
    }
}
