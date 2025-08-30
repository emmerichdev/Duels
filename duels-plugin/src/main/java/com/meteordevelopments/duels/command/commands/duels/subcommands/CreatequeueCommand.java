package com.meteordevelopments.duels.command.commands.duels.subcommands;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.command.BaseCommand;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.util.command.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreatequeueCommand extends BaseCommand {

    public CreatequeueCommand(final DuelsPlugin plugin) {
        super(plugin, "createqueue", "createqueue [bet] [-:kit]", "Creates a queue with given bet and kit.", 3, false, "createq");
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final int bet = CommandUtil.parseBetAmount(args, 1);
        final KitImpl kit = CommandUtil.parseAndValidateOptionalKit(args, 2, kitManager, lang, sender);
        
        // If parsing failed (invalid kit name), return early
        if (!args[2].equals("-") && kit == null) {
            return;
        }

        final String kitName = CommandUtil.getKitDisplayName(kit, lang);

        if (queueManager.create(sender, kit, bet) == null) {
            lang.sendMessage(sender, "ERROR.queue.already-exists", "kit", kitName, "bet_amount", bet);
            return;
        }

        lang.sendMessage(sender, "COMMAND.duels.create-queue", "kit", kitName, "bet_amount", bet);
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, final @NotNull Command command, final @NotNull String alias, final String[] args) {
        if (args.length == 2) {
            return CommandUtil.getBetTabCompletion();
        }

        if (args.length > 2) {
            return handleTabCompletion(args[2], kitManager.getNames(true));
        }

        return null;
    }
}
