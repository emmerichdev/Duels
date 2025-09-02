package com.meteordevelopments.duels.command;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.arena.ArenaImpl;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TestArenaCommand extends BaseCommand implements Listener {

    private final Map<UUID, World> testingArenas = new ConcurrentHashMap<>();
    private final Map<UUID, Location> originalLocations = new ConcurrentHashMap<>();

    public TestArenaCommand(DuelsPlugin plugin) {
        super(plugin, "testarena", "duels.admin.testarena", true);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            // This check is technically redundant due to playerOnly=true in the constructor, but it's good practice.
            plugin.getLang().sendMessage(sender, "ERROR.must-be-player");
            return;
        }

        if (args.length == 0) {
            plugin.getLang().sendMessage(player, "&cUsage: /testarena <join|leave>");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "join" -> handleJoin(player, args);
            case "leave" -> handleLeave(player);
            default -> plugin.getLang().sendMessage(player, "&cUsage: /testarena <join|leave>");
        }
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            plugin.getLang().sendMessage(player, "&cUsage: /testarena join <arena_name>");
            return;
        }

        if (testingArenas.containsKey(player.getUniqueId())) {
            plugin.getLang().sendMessage(player, "&cYou are already in a test arena. Use /testarena leave first.");
            return;
        }

        String arenaName = args[1];
        ArenaImpl arena = plugin.getArenaManager().get(arenaName);

        if (arena == null) {
            plugin.getLang().sendMessage(player, "&cCould not find an arena with that name.");
            return;
        }

        plugin.getLang().sendMessage(player, "&aCreating test arena '" + arenaName + "'...");

        plugin.getSlimeManager().createMatchWorld(arena.getName()).thenAccept(world -> {
            if (world == null) {
                plugin.getLang().sendMessage(player, "&cFailed to create the arena world.");
                return;
            }

            Location spawnPoint = arena.getPosition(1);
            if (spawnPoint == null) {
                plugin.getLang().sendMessage(player, "&cArena is missing spawn point 1.");
                plugin.getSlimeManager().unloadMatchWorld(world);
                return;
            }

            Location teleportLocation = spawnPoint.clone();
            teleportLocation.setWorld(world);

            plugin.doSync(() -> {
                originalLocations.put(player.getUniqueId(), player.getLocation());
                testingArenas.put(player.getUniqueId(), world);
                player.teleport(teleportLocation);
                plugin.getLang().sendMessage(player, "&aTeleported to test arena. Use /testarena leave to exit.");
            });
        });
    }

    private void handleLeave(Player player) {
        if (!testingArenas.containsKey(player.getUniqueId())) {
            plugin.getLang().sendMessage(player, "&cYou are not in a test arena.");
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

        plugin.getLang().sendMessage(player, "&aYou have left the test arena.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (testingArenas.containsKey(event.getPlayer().getUniqueId())) {
            leaveArena(event.getPlayer());
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return List.of("join", "leave");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            return plugin.getArenaManager().getNames().stream()
                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        }
        return List.of();
    }
}
