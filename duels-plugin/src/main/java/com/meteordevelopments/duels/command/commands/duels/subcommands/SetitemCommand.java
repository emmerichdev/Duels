package com.meteordevelopments.duels.command.commands.duels.subcommands;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.command.BaseCommand;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.util.StringUtil;
import com.meteordevelopments.duels.util.command.CommandUtil;
import com.meteordevelopments.duels.util.inventory.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetitemCommand extends BaseCommand {

    public SetitemCommand(final DuelsPlugin plugin) {
        super(plugin, "setitem", "setitem [name]", "Sets the displayed item for selected kit.", 2, true);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final Player player = (Player) sender;
        final ItemStack held = InventoryUtil.getItemInHand(player);

        if (held == null || held.getType() == Material.AIR) {
            lang.sendMessage(sender, "ERROR.kit.empty-hand");
            return;
        }

        final KitImpl kit = CommandUtil.parseAndValidateRequiredKit(args, 1, kitManager, lang, sender);
        if (kit == null) {
            return;
        }

        kit.setDisplayed(held.clone());
        kitManager.getGui().calculatePages();
        lang.sendMessage(sender, "COMMAND.duels.set-item", "name", kit.getName());
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, final @NotNull Command command, final @NotNull String alias, final String[] args) {
        if (args.length == 2) {
            return handleTabCompletion(args[1], kitManager.getNames(false));
        }

        return null;
    }
}
