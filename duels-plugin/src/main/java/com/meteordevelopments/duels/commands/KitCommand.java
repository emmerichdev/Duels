package com.meteordevelopments.duels.commands;

import co.aikar.commands.annotation.*;
import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.gui.bind.BindGui;
import com.meteordevelopments.duels.gui.options.OptionsGui;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.util.StringUtil;
import com.meteordevelopments.duels.util.inventory.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("duels")
@CommandPermission("duels.admin")
public class KitCommand extends BaseCommand {

    public KitCommand(DuelsPlugin plugin) {
        super(plugin);
    }

    @Subcommand("savekit")
    @CommandCompletion("@nothing")
    @Description("Saves a kit with given name.")
    public void onSaveKit(Player player, String name, @Optional String override) {
        if (!StringUtil.isAlphanumeric(name)) {
            lang.sendMessage(player, "ERROR.command.name-not-alphanumeric", "name", name);
            return;
        }

        if (kitManager.create(player, name, override != null && override.equals("-o")) == null) {
            lang.sendMessage(player, "ERROR.kit.already-exists", "name", name);
            return;
        }

        lang.sendMessage(player, "COMMAND.duels.save-kit", "name", name);
    }

    @Subcommand("loadkit")
    @CommandCompletion("@kits")
    @Description("Loads the selected kit to your inventory.")
    public void onLoadKit(Player player, KitImpl kit) {
        player.getInventory().clear();
        kit.equip(player);
        lang.sendMessage(player, "COMMAND.duels.load-kit", "name", kit.getName());
    }

    @Subcommand("deletekit")
    @CommandCompletion("@kits")
    @Description("Deletes a kit.")
    public void onDeleteKit(CommandSender sender, KitImpl kit) {
        if (kitManager.remove(sender, kit.getName()) == null) {
            lang.sendMessage(sender, "ERROR.kit.not-found", "name", kit.getName());
            return;
        }

        lang.sendMessage(sender, "COMMAND.duels.delete-kit", "name", kit.getName());
    }

    @Subcommand("setitem")
    @CommandCompletion("@kits")
    @Description("Sets the displayed item for selected kit.")
    public void onSetItem(Player player, KitImpl kit) {
        final ItemStack held = InventoryUtil.getItemInHand(player);

        if (held == null || held.getType() == Material.AIR) {
            lang.sendMessage(player, "ERROR.kit.empty-hand");
            return;
        }

        kit.setDisplayed(held.clone());
        kitManager.getGui().calculatePages();
        lang.sendMessage(player, "COMMAND.duels.set-item", "name", kit.getName());
    }

    @Subcommand("options")
    @CommandCompletion("@kits")
    @Description("Opens the options gui for kit.")
    public void onOptions(Player player, KitImpl kit) {
        plugin.getGuiListener().addGui(player, new OptionsGui(plugin, player, kit), true).open(player);
    }

    @Subcommand("bind")
    @CommandCompletion("@kits")
    @Description("Opens the arena bind gui for kit.")
    public void onBind(Player player, KitImpl kit) {
        plugin.getGuiListener().addGui(player, new BindGui(plugin, kit), true).open(player);
    }
}
