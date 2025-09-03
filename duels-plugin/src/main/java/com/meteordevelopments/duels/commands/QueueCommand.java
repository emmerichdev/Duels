package com.meteordevelopments.duels.commands;

import co.aikar.commands.annotation.*;
import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.Permissions;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.queue.Queue;
import org.bukkit.entity.Player;

@CommandAlias("queue")
@CommandPermission("duels.queue")
public class QueueCommand extends BaseCommand {

    public QueueCommand(DuelsPlugin plugin) {
        super(plugin);
    }

    @Default
    public void onDefault(Player player) {
        if (userManager.get(player) == null) {
            lang.sendMessage(player, "ERROR.data.load-failure");
            return;
        }

        queueManager.getGui().open(player);
    }

    @Subcommand("join|j")
    @CommandCompletion("@kits")
    public void onJoin(Player player, @Optional String kitName, @Optional Integer bet) {
        KitImpl kit = null;
        if (kitName != null && !kitName.equals("-") && !kitName.equals("-r")) {
            kit = kitManager.get(kitName);
            if (kit == null) {
                lang.sendMessage(player, "ERROR.kit.not-found", "name", kitName);
                return;
            }
        }

        final String finalKitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");
        final int finalBet = bet != null ? bet : 0;
        final Queue queue = (kitName != null && kitName.equals("-r")) ? queueManager.randomQueue() : queueManager.get(kit, finalBet);

        if (queue == null) {
            lang.sendMessage(player, "ERROR.queue.not-found", "bet_amount", finalBet, "kit", finalKitName);
            return;
        }

        queueManager.queue(player, queue);
    }

    @Subcommand("leave|l")
    public void onLeave(Player player) {
        if (queueManager.remove(player) == null) {
            lang.sendMessage(player, "ERROR.queue.not-in-queue");
        }
    }
}
