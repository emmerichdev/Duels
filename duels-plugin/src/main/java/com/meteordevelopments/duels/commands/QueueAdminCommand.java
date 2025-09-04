package com.meteordevelopments.duels.commands;

import co.aikar.commands.annotation.*;
import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.queue.Queue;
import com.meteordevelopments.duels.queue.sign.QueueSignImpl;
import com.meteordevelopments.duels.util.BlockUtil;
import com.meteordevelopments.duels.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("duels")
@CommandPermission("duels.admin")
public class QueueAdminCommand extends BaseCommand {

    public QueueAdminCommand(DuelsPlugin plugin) {
        super(plugin);
    }

    @Subcommand("createqueue|createq")
    @CommandCompletion("@nothing @kits")
    @Description("Creates a queue with given bet and kit.")
    public void onCreateQueue(CommandSender sender, int bet, @Optional String kitName) {
        KitImpl kit = getKit(sender, kitName);
        if (kitName != null && !kitName.equals("-") && kit == null) {
            return;
        }

        final String finalKitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");

        if (queueManager.create(sender, kit, bet) == null) {
            lang.sendMessage(sender, "ERROR.queue.already-exists", "kit", finalKitName, "bet_amount", bet);
            return;
        }

        lang.sendMessage(sender, "COMMAND.duels.create-queue", "kit", finalKitName, "bet_amount", bet);
    }

    @Subcommand("deletequeue|delqueue|delq")
    @CommandCompletion("@nothing @kits")
    @Description("Deletes a queue.")
    public void onDeleteQueue(CommandSender sender, int bet, @Optional String kitName) {
        KitImpl kit = getKit(sender, kitName);
        if (kitName != null && !kitName.equals("-") && kit == null) {
            return;
        }

        final String finalKitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");

        if (queueManager.remove(sender, kit, bet) == null) {
            lang.sendMessage(sender, "ERROR.queue.not-found", "bet_amount", bet, "kit", finalKitName);
            return;
        }

        lang.sendMessage(sender, "COMMAND.duels.delete-queue", "kit", finalKitName, "bet_amount", bet);
    }

    @Subcommand("addsign")
    @CommandCompletion("@nothing @kits")
    @Description("Creates a queue sign with given bet and kit.")
    public void onAddSign(Player player, int bet, @Optional String kitName) {
        final Sign sign = BlockUtil.getTargetBlock(player, Sign.class, 6);

        if (sign == null) {
            lang.sendMessage(player, "ERROR.sign.not-a-sign");
            return;
        }

        KitImpl kit = getKit(player, kitName);
        if (kitName != null && !kitName.equals("-") && kit == null) {
            return;
        }

        final String finalKitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");
        final Queue queue = queueManager.get(kit, bet);

        if (queue == null) {
            lang.sendMessage(player, "ERROR.queue.not-found", "bet_amount", bet, "kit", finalKitName);
            return;
        }

        if (!queueSignManager.create(player, sign.getLocation(), queue)) {
            lang.sendMessage(player, "ERROR.sign.already-exists");
            return;
        }

        final Location location = sign.getLocation();
        lang.sendMessage(player, "COMMAND.duels.add-sign", "location", StringUtil.parse(location), "kit", finalKitName, "bet_amount", bet);
    }

    @Subcommand("deletesign|delsign")
    @Description("Deletes the queue sign you are looking at.")
    public void onDeleteSign(Player player) {
        final Sign sign = BlockUtil.getTargetBlock(player, Sign.class, 6);

        if (sign == null) {
            lang.sendMessage(player, "ERROR.sign.not-a-sign");
            return;
        }

        final QueueSignImpl queueSign = queueSignManager.remove(player, sign.getLocation());

        if (queueSign == null) {
            lang.sendMessage(player, "ERROR.sign.not-found");
            return;
        }

        sign.setType(Material.AIR);
        sign.update(true);

        final Location location = sign.getLocation();
        final Queue queue = queueSign.getQueue();
        final com.meteordevelopments.duels.api.kit.Kit kit = queue.getKit();
        final String kitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");
        lang.sendMessage(player, "COMMAND.duels.del-sign", "location", StringUtil.parse(location), "kit", kitName, "bet_amount", queue.getBet());
    }

    private KitImpl getKit(CommandSender sender, String kitName) {
        if (kitName != null && !kitName.equals("-")) {
            KitImpl kit = kitManager.get(kitName);
            if (kit == null) {
                lang.sendMessage(sender, "ERROR.kit.not-found", "name", kitName);
                return null;
            }
            return kit;
        }
        return null;
    }
}