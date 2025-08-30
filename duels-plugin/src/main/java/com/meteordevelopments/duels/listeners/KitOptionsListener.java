package com.meteordevelopments.duels.listeners;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.arena.ArenaImpl;
import com.meteordevelopments.duels.arena.ArenaManagerImpl;
import com.meteordevelopments.duels.config.Config;
import com.meteordevelopments.duels.countdown.DuelCountdown;
import com.meteordevelopments.duels.duel.DuelManager;
import com.meteordevelopments.duels.kit.KitImpl.Characteristic;
import com.meteordevelopments.duels.match.DuelMatch;
import com.meteordevelopments.duels.util.PlayerUtil;
import com.meteordevelopments.duels.util.compat.Items;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class KitOptionsListener implements Listener {

    private final DuelsPlugin plugin;
    private final Config config;
    private final ArenaManagerImpl arenaManager;
    private final DuelManager duelManager;

    public KitOptionsListener(final DuelsPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.arenaManager = plugin.getArenaManager();
        this.duelManager = plugin.getDuelManager();

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().registerEvents(new ComboPost1_14Listener(), plugin);
    }

    private boolean isEnabled(final ArenaImpl arena, final Characteristic characteristic) {
        final DuelMatch match = arena.getMatch();
        return match != null && match.getKit() != null && match.getKit().hasCharacteristic(characteristic);
    }

    @EventHandler
    public void on(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        final ArenaImpl arena = arenaManager.get(player);

        if (arena == null) {
            return;
        }

        final DuelMatch match = arena.getMatch();
        if (match == null) {
            return;
        }

        // For ROUNDS3, if damage would kill the player, handle round end
        if (isEnabled(arena, Characteristic.ROUNDS3)) {
            double finalHealth = player.getHealth() - event.getFinalDamage();
            if (finalHealth <= 0) {
                // Cancel the damage event immediately to prevent any delayed damage
                event.setCancelled(true);

                // Find the winner (the other player)
                Player winner = match.getAlivePlayers().stream()
                        .filter(p -> !p.equals(player))
                        .findFirst()
                        .orElse(null);

                if (winner != null) {
                    // Add round win
                    match.addRoundWin(winner);

                    if (match.hasWonMatch(winner)) {
                        // On final round, let the damage go through to kill the player
                        match.markAsDead(player);
                        // Let DuelManager handle the match end with all effects
                        // Pass the winner's current health for the death message
                        double winnerHealth = Math.ceil(winner.getHealth()) * 0.5;
                        arena.broadcast(plugin.getLang().getMessage("DUEL.on-death.with-killer",
                                "name", player.getName(),
                                "killer", winner.getName(),
                                "health", winnerHealth));
                        duelManager.handleMatchEnd(match, arena, player, player.getLocation(), winner);
                        return;
                    }

                    // Cancel the damage event for non-final rounds
                    event.setDamage(0);
                    final AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
                    if (maxHealthAttribute != null) {
                        player.setHealth(maxHealthAttribute.getValue());
                    }

                    // Start next round
                    match.nextRound();

                    // Reset both players' health and equipment
                    for (Player p : match.getAllPlayers()) {
                        PlayerUtil.reset(p);
                        final AttributeInstance playerMaxHealth = p.getAttribute(Attribute.MAX_HEALTH);
                        if (playerMaxHealth != null) {
                            p.setHealth(playerMaxHealth.getValue());
                        }
                        p.setNoDamageTicks(40); // Give 2 seconds immunity to prevent damage carry-over
                        if (match.getKit() != null) {
                            match.getKit().equip(p);
                        }
                    }

                    // Use the plugin's teleport system for both players
                    Player[] players = match.getAllPlayers().toArray(new Player[0]);
                    if (players.length >= 2) {
                        plugin.getTeleport().tryTeleport(players[0], arena.getPosition(1));
                        plugin.getTeleport().tryTeleport(players[1], arena.getPosition(2));
                    }

                    // Broadcast round end and status
                    arena.broadcast(plugin.getLang().getMessage("DUEL.rounds.round-end",
                            "round", match.getCurrentRound() - 1,
                            "winner", winner.getName()));

                    arena.broadcast(plugin.getLang().getMessage("DUEL.rounds.round-status",
                            "player1", players[0].getName(),
                            "wins1", match.getRoundWins(players[0]),
                            "player2", players[1].getName(),
                            "wins2", match.getRoundWins(players[1])));

                    // Check for match point
                    if (match.getRoundWins(winner) == 1) {
                        arena.broadcast(plugin.getLang().getMessage("DUEL.rounds.match-point",
                                "player", winner.getName()));
                    }

                    // Start countdown and announce new round
                    arena.broadcast(plugin.getLang().getMessage("DUEL.rounds.round-start",
                            "round", match.getCurrentRound()));

                    DuelCountdown countdown = new DuelCountdown(plugin, arena, match);
                    arena.setCountdown(countdown);
                    countdown.startCountdown(0L, 20L);
                }
                return;
            }
        }

        if (!isEnabled(arena, Characteristic.SUMO) && !isEnabled(arena, Characteristic.BOXING)) {
            return;
        }

        event.setDamage(0);
    }

    @EventHandler
    public void on(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        final ArenaImpl arena = arenaManager.get(player);

        if (arena == null || !isEnabled(arena, Characteristic.LOKA)) {
            return;
        }
        double originalDamage = event.getDamage();
        double increaseDamage = originalDamage * 1.33;
        event.setDamage(increaseDamage);
    }

    @EventHandler
    public void on(final FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        final ArenaImpl arena = arenaManager.get(player);

        if (arena == null || !isEnabled(arena, Characteristic.HUNGER)) {
            return;
        }

        event.setCancelled(true);
    }



    @EventHandler
    public void on(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final ArenaImpl arena = arenaManager.get(player);

        if (player.isDead() || arena == null || !isEnabled(arena, Characteristic.SUMO) || arena.isEndGame()) {
            return;
        }

        final Location to = event.getTo(), from = event.getFrom();

        if ((from.getBlockX() !=
                to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ())
                && arena.getCountdown() != null) {
            from.setPitch(player.getLocation().getPitch());
            from.setYaw(player.getLocation().getYaw());
            event.setTo(from);
            return;
        }

        final Block block = event.getFrom().getBlock();

        if (!(block.getType().name().contains("WATER") || block.getType().name().contains("LAVA"))) {
            return;
        }

        player.setHealth(0);
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();
        final ArenaImpl arena = arenaManager.get(player);

        if (arena == null || !isEnabled(arena, Characteristic.SOUP)) {
            return;
        }

        final ItemStack item = event.getItem();

        if (item == null || item.getType() != Items.MUSHROOM_SOUP) {
            return;
        }

        event.setUseItemInHand(Result.DENY);

        if (config.isSoupCancelIfAlreadyFull() && player.getHealth() == PlayerUtil.getMaxHealth(player)) {
            return;
        }

        final ItemStack bowl = config.isSoupRemoveEmptyBowl() ? null : new ItemStack(Material.BOWL);


        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            player.getInventory().setItemInOffHand(bowl);
        } else {
            player.getInventory().setItemInMainHand(bowl);
        }

        final double regen = config.getSoupHeartsToRegen() * 2.0;
        final double oldHealth = player.getHealth();
        final double maxHealth = PlayerUtil.getMaxHealth(player);
        player.setHealth(Math.min(oldHealth + regen, maxHealth));
    }

    @EventHandler(ignoreCancelled = true)
    public void on(final EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN)) {
            return;
        }

        final ArenaImpl arena = arenaManager.get(player);

        if (arena == null || !isEnabled(arena, Characteristic.UHC)) {
            return;
        }

        event.setCancelled(true);
    }



    private class ComboPost1_14Listener implements Listener {

        @EventHandler
        public void on(final EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof Player player)) {
                return;
            }

            final ArenaImpl arena = arenaManager.get(player);

            if (arena == null || !isEnabled(arena, Characteristic.COMBO)) {
                return;
            }

            Location belowLocation = player.getLocation().add(0, -1, 0);
            boolean isOnGround = belowLocation.getBlock().getType().isSolid();
            
            boolean isCritical = !isOnGround &&
                    !player.isSneaking() &&
                    player.getFallDistance() > 0;
            if (isCritical) {
                // Cancel the extra critical damage by setting it to normal attack damage
                event.setDamage(event.getDamage() / 1.5); // Critical hits are 150%, reverse it
            }

            plugin.doSyncAfter(() -> player.setNoDamageTicks(0), 1);
        }
    }
}