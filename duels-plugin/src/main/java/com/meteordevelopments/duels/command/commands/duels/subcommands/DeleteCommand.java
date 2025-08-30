package com.meteordevelopments.duels.command.commands.duels.subcommands;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.arena.ArenaImpl;
import com.meteordevelopments.duels.command.BaseCommand;
import com.meteordevelopments.duels.util.command.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeleteCommand extends BaseCommand {

    public DeleteCommand(final DuelsPlugin plugin) {
        super(plugin, "delete", "delete [name]", "Deletes an arena.", 2, false);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final ArenaImpl arena = CommandUtil.parseAndValidateArena(args, 1, arenaManager, lang, sender);
        if (arena == null) {
            return;
        }

        if (arena.isUsed()) {
            lang.sendMessage(sender, "ERROR.arena.delete-failure", "name", arena.getName());
            return;
        }

        arenaManager.remove(sender, arena);
        lang.sendMessage(sender, "COMMAND.duels.delete", "name", arena.getName());
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, final @NotNull Command command, final @NotNull String alias, final String[] args) {
        if (args.length == 2) {
            return handleTabCompletion(args[1], arenaManager.getNames());
        }

        return null;
    }
}
