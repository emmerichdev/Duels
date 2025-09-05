package com.emmerichbrowne.duels.commands;

import co.aikar.commands.annotation.*;
import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaImpl;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import com.emmerichbrowne.duels.command.AutoRegister;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AutoRegister
@CommandAlias("testarena")
@CommandPermission("duels.testarena")
public class TestArenaCommand extends BaseCommand implements Listener {

    private final Map<UUID, World> testingArenas = new ConcurrentHashMap<>();
    private final Map<UUID, Location> originalLocations = new ConcurrentHashMap<>();

    public TestArenaCommand(DuelsPlugin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Subcommand("join")
    @CommandCompletion("@arenas")
    public void onJoin(Player player, ArenaImpl arena) {
        if (testingArenas.containsKey(player.getUniqueId())) {
            lang.sendMessage(player, "&cYou are already in a test arena. Use /testarena leave first.");
            return;
        }

        lang.sendMessage(player, "&aCreating test arena '" + arena.getName() + "'...");

        plugin.getSlimeManager().createMatchWorld(arena.getName()).thenAccept(world -> {
            if (world == null) {
                lang.sendMessage(player, "&cFailed to create the arena world.");
                return;
            }

            Location spawnPoint = arena.getPosition(1);
            if (spawnPoint == null) {
                lang.sendMessage(player, "&cArena is missing spawn point 1.");
                plugin.getSlimeManager().unloadMatchWorld(world);
                return;
            }

            Location teleportLocation = spawnPoint.clone();
            teleportLocation.setWorld(world);

            plugin.doSync(() -> {
                originalLocations.put(player.getUniqueId(), player.getLocation());
                testingArenas.put(player.getUniqueId(), world);
                player.teleport(teleportLocation);
                lang.sendMessage(player, "&aTeleported to test arena. Use /testarena leave to exit.");
            });
        });
    }

    @Subcommand("leave")
    public void onLeave(Player player) {
        if (!testingArenas.containsKey(player.getUniqueId())) {
            lang.sendMessage(player, "&cYou are not in a test arena.");
            return;
        }
        leaveArena(player);
    }

    private void leaveArena(Player player) {
        World world = testingArenas.remove(player.getUniqueId());
        Location originalLocation = originalLocations.remove(player.getUniqueId());

        if (originalLocation != null) {
            player.teleport(originalLocation);
        }

        if (world != null) {
            plugin.getSlimeManager().unloadMatchWorld(world);
        }

        lang.sendMessage(player, "&aYou have left the test arena.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (testingArenas.containsKey(event.getPlayer().getUniqueId())) {
            leaveArena(event.getPlayer());
        }
    }
}
