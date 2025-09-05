package com.emmerichbrowne.duels.commands;

import co.aikar.commands.annotation.*;
import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaImpl;
import com.emmerichbrowne.duels.kit.KitImpl;
import com.emmerichbrowne.duels.util.StringUtil;
import com.emmerichbrowne.duels.command.AutoRegister;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@AutoRegister
@CommandAlias("duels")
@CommandPermission("duels.admin")
public class ArenaCommand extends BaseCommand {

    public ArenaCommand(DuelsPlugin plugin) {
        super(plugin);
    }

    @Subcommand("create")
    @Description("Creates an arena with given name.")
    public void onCreateArena(CommandSender sender, String name) {
        if (!StringUtil.isAlphanumeric(name)) {
            lang.sendMessage(sender, "ERROR.command.name-not-alphanumeric", "name", name);
            return;
        }

        if (!arenaManager.create(sender, name)) {
            lang.sendMessage(sender, "ERROR.arena.already-exists", "name", name);
            return;
        }

        lang.sendMessage(sender, "COMMAND.duels.create", "name", name);
    }

    @Subcommand("set")
    @CommandCompletion("@arenas 1|2")
    @Description("Sets the teleport position of an arena.")
    public void onSetArena(Player player, ArenaImpl arena, int position) {
        if (position <= 0 || position > 2) {
            lang.sendMessage(player, "ERROR.arena.invalid-position");
            return;
        }

        final Location location = player.getLocation().clone();
        arena.setPosition(player, position, location);
        lang.sendMessage(player, "COMMAND.duels.set", "position", position, "name", arena.getName(), "location", StringUtil.parse(location));
    }

    @Subcommand("delete")
    @CommandCompletion("@arenas")
    @Description("Deletes an arena.")
    public void onDeleteArena(CommandSender sender, ArenaImpl arena) {
        if (arena.isUsed()) {
            lang.sendMessage(sender, "ERROR.arena.delete-failure", "name", arena.getName());
            return;
        }

        arenaManager.remove(sender, arena);
        lang.sendMessage(sender, "COMMAND.duels.delete", "name", arena.getName());
    }

    @Subcommand("info")
    @CommandCompletion("@arenas")
    @Description("Displays information about the selected arena.")
    public void onInfoArena(CommandSender sender, ArenaImpl arena) {
        final String inUse = arena.isUsed() ? lang.getMessage("GENERAL.true") : lang.getMessage("GENERAL.false");
        final String disabled = arena.isDisabled() ? lang.getMessage("GENERAL.true") : lang.getMessage("GENERAL.false");
        final String kits = StringUtil.join(arena.getKits().stream().map(KitImpl::getName).collect(Collectors.toList()), ", ");
        final String positions = StringUtil.join(arena.getPositions().values().stream().map(StringUtil::parse).collect(Collectors.toList()), ", ");
        final String players = StringUtil.join(arena.getPlayers().stream().map(Player::getName).collect(Collectors.toList()), ", ");
        lang.sendMessage(sender, "COMMAND.duels.info", "name", arena.getName(), "in_use", inUse, "disabled", disabled, "kits",
                !kits.isEmpty() ? kits : lang.getMessage("GENERAL.none"), "positions", !positions.isEmpty() ? positions : lang.getMessage("GENERAL.none"), "players",
                !players.isEmpty() ? players : lang.getMessage("GENERAL.none"));
    }

    @Subcommand("toggle")
    @CommandCompletion("@arenas")
    @Description("Enables or disables an arena.")
    public void onToggleArena(CommandSender sender, ArenaImpl arena) {
        arena.setDisabled(sender, !arena.isDisabled());
        lang.sendMessage(sender, "COMMAND.duels.toggle", "name", arena.getName(), "state", arena.isDisabled() ? lang.getMessage("GENERAL.disabled") : lang.getMessage("GENERAL.enabled"));
    }

    @Subcommand("teleport|tp|goto")
    @CommandCompletion("@arenas 1|2")
    @Description("Teleports to an arena.")
    public void onTeleportArena(Player player, ArenaImpl arena, @Default("1") int position) {
        if (arena.getPositions().isEmpty()) {
            lang.sendMessage(player, "ERROR.arena.no-position-set", "name", arena.getName());
            return;
        }

        final Location location = arena.getPosition(position);

        if (location == null) {
            lang.sendMessage(player, "ERROR.arena.invalid-position");
            return;
        }
        
        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                lang.sendMessage(player, "COMMAND.duels.teleport", "name", arena.getName(), "position", position);
            } else {
                lang.sendMessage(player, "ERROR.teleport.failed", "name", arena.getName(), "position", position);
            }
        }).exceptionally(throwable -> {
            lang.sendMessage(player, "ERROR.teleport.failed", "name", arena.getName(), "position", position);
            return null;
        });
    }

    @Subcommand("disable")
    @CommandCompletion("@arenas")
    @Description("Disables an arena.")
    public void onDisableArena(CommandSender sender, ArenaImpl arena) {
        if (arena.isDisabled()) {
            lang.sendMessage(sender, "COMMAND.duels.already-disabled", "name", arena.getName());
            return;
        }

        arena.setDisabled(sender, true);
        lang.sendMessage(sender, "COMMAND.duels.disable", "name", arena.getName());
    }

    @Subcommand("enable")
    @CommandCompletion("@arenas")
    @Description("Enables an arena.")
    public void onEnableArena(CommandSender sender, ArenaImpl arena) {
        if (!arena.isDisabled()) {
            lang.sendMessage(sender, "COMMAND.duels.already-enabled", "name", arena.getName());
            return;
        }

        arena.setDisabled(sender, false);
        lang.sendMessage(sender, "COMMAND.duels.enable", "name", arena.getName());
    }
}
