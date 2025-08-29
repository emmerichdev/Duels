package com.meteordevelopments.duels.command.commands.duels.subcommands;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.arena.ArenaImpl;
import com.meteordevelopments.duels.command.BaseCommand;
import com.meteordevelopments.duels.util.StringUtil;
import com.meteordevelopments.duels.util.command.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DisableCommand extends BaseCommand {


    public DisableCommand(final DuelsPlugin plugin) {
        super(plugin, "disable", "disable [name]", "Disables an arena.", 2, false);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final ArenaImpl arena = CommandUtil.parseAndValidateArena(args, 1, arenaManager, lang, sender);
        if (arena == null) {
            return;
        }

        if (arena.isDisabled()) {
            lang.sendMessage(sender, "COMMAND.duels.already-disabled", "name", arena.getName());
            return;
        }

        arena.setDisabled(sender, true);
        lang.sendMessage(sender, "COMMAND.duels.disable", "name", arena.getName());
    }

    public List<String> onTabComplete(@NotNull final CommandSender sender, final @NotNull Command command, final @NotNull String alias, final String[] args) {
        if (args.length == 2) {
            return handleTabCompletion(args[1], arenaManager.getNames());
        }

        return null;
    }
}