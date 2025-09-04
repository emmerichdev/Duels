package com.emmerichbrowne.duels.teleport;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.hook.hooks.EssentialsHook;
import com.emmerichbrowne.duels.util.Loadable;
import com.emmerichbrowne.duels.util.Log;
import com.emmerichbrowne.duels.util.metadata.MetadataUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class Teleport implements Loadable, Listener {

    public static final String METADATA_KEY = "Duels-Teleport";

    private final DuelsPlugin plugin;

    private EssentialsHook essentials;

    public Teleport(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleLoad() {
        this.essentials = plugin.getHookManager().getHook(EssentialsHook.class);

        // Late-register the listener to override previously registered listeners
        plugin.doSyncAfter(() -> plugin.registerListener(this), 1L);
    }

    @Override
    public void handleUnload() {
    }

    public void tryTeleport(final Player player, final Location location) {
        if (location == null || location.getWorld() == null) {
            Log.warn(this, "Could not teleport " + player.getName() + "! Location is null");
            return;
        }

        for (Entity entity : player.getPassengers()) {
            player.removePassenger(entity);
        }

        player.closeInventory();

        if (essentials != null) {
            essentials.setBackLocation(player, location);
        }

        MetadataUtil.put(plugin, player, METADATA_KEY, location.clone());

        boolean isFolia = DuelsPlugin.getMorePaperLib().scheduling().isUsingFolia();

        if (isFolia) {
            player.teleportAsync(location).thenAccept(success -> {
                if (!success) {
                    Log.warn(this, "Could not teleport " + player.getName() + "! TeleportAsync failed.");
                }
            });
        } else {
            if (!player.teleport(location)) {
                Log.warn(this, "Could not teleport " + player.getName() + "! Player is dead or is vehicle");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        final Object value = MetadataUtil.removeAndGet(plugin, player, METADATA_KEY);

        // Only handle the case where teleport is cancelled and player has force teleport metadata value
        if (!event.isCancelled() || value == null) {
            return;
        }

        event.setCancelled(false);
        event.setTo((Location) value);
    }
}
