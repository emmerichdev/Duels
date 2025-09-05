package com.emmerichbrowne.duels.commands;

import co.aikar.commands.annotation.*;
import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.api.kit.Kit;
import com.emmerichbrowne.duels.api.queue.DQueue;
import com.emmerichbrowne.duels.arena.ArenaImpl;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.queue.sign.QueueSignImpl;
import com.emmerichbrowne.duels.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.emmerichbrowne.duels.command.AutoRegister;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AutoRegister
@CommandAlias("duels")
@CommandPermission("duels.admin")
public class DuelsCommand extends BaseCommand {

    public DuelsCommand(DuelsPlugin plugin) {
        super(plugin);
    }

    @Default
    @HelpCommand
    public void onDefault(CommandSender sender) {
        lang.sendMessage(sender, "COMMAND.duels.usage", "command", "duels");
    }

    @Subcommand("help")
    @CommandCompletion("arena|kit|queue|sign|user|extra")
    public void onHelp(CommandSender sender, @Default("arena") String category) {
        lang.sendMessage(sender, "COMMAND.duels.help." + category.toLowerCase(), "command", "duels");
    }

    @Subcommand("setlobby|setspawn")
    @Description("Sets duel lobby location.")
    public void onSetLobby(Player player) {
        if (!playerManager.setLobby(player)) {
            lang.sendMessage(player, "ERROR.command.lobby-save-failure");
            return;
        }

        lang.sendMessage(player, "COMMAND.duels.set-lobby");
    }

    @Subcommand("lobby")
    @Description("Teleports to duel lobby.")
    public void onLobby(Player player) {
        final Location lobbyLocation = playerManager.getLobby();
        
        player.teleportAsync(lobbyLocation).thenAccept(success -> {
            if (success) {
                lang.sendMessage(player, "COMMAND.duels.lobby");
            } else {
                lang.sendMessage(player, "ERROR.teleport.failed");
            }
        }).exceptionally(throwable -> {
            lang.sendMessage(player, "ERROR.teleport.failed");
            return null;
        });
    }

    @Subcommand("playsound")
    @CommandCompletion("@sounds")
    @Description("Plays the selected sound if defined.")
    public void onPlaySound(Player player, String soundName) {
        final Config.MessageSound sound = config.getSound(soundName);

        if (sound == null) {
            lang.sendMessage(player, "ERROR.sound.not-found", "name", soundName);
            return;
        }

        player.playSound(player.getLocation(), sound.getType(), sound.getVolume(), sound.getPitch());
    }

    @Subcommand("list|ls")
    @Description("Displays the list of all arenas, kits, queues, etc.")
    public void onList(CommandSender sender) {
        final List<String> arenas = new ArrayList<>();
        arenaManager.getArenasImpl().forEach(arena -> arenas.add("&" + getColor(arena) + arena.getName()));
        final String kits = StringUtil.join(kitManager.getKits().stream().map(Kit::getName).collect(Collectors.toList()), ", ");
        final String queues = StringUtil.join(queueManager.getQueues().stream().map(DQueue::toString).collect(Collectors.toList()), ", ");
        final String signs = StringUtil.join(queueSignManager.getSigns().stream().map(QueueSignImpl::toString).collect(Collectors.toList()), ", ");
        lang.sendMessage(sender, "COMMAND.duels.list",
                "arenas", !arenas.isEmpty() ? StringUtil.join(arenas, "&r, &r") : lang.getMessage("GENERAL.none"),
                "kits", !kits.isEmpty() ? kits : lang.getMessage("GENERAL.none"),
                "queues", !queues.isEmpty() ? queues : lang.getMessage("GENERAL.none"),
                "queue_signs", !signs.isEmpty() ? signs : lang.getMessage("GENERAL.none"),
                "lobby", StringUtil.parse(playerManager.getLobby()));
    }

    private String getColor(final ArenaImpl arena) {
        return arena.isDisabled() ? "4" : (arena.getPositions().size() < 2 ? "9" : arena.isUsed() ? "c" : "a");
    }
}