package com.meteordevelopments.duels.gui.settings.buttons;

import com.meteordevelopments.duels.DuelsPlugin;

import com.meteordevelopments.duels.gui.BaseButton;
import com.meteordevelopments.duels.setting.Settings;
import com.meteordevelopments.duels.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class OwnInventoryButton extends BaseButton {

    public OwnInventoryButton(final DuelsPlugin plugin) {
        super(plugin, ItemBuilder.of(Material.getMaterial(plugin.getLang().getMessage("GUI.settings.buttons.use-own-inventory.material").toUpperCase())).name(plugin.getLang().getMessage("GUI.settings.buttons.use-own-inventory.name")).build());
    }

    @Override
    public void update(final Player player) {
        if (config.isOwnInventoryUsePermission() && !player.hasPermission("duels.use.own-inventory") && !player.hasPermission("duels.use.*")) {
            setLore(lang.getMessage("GUI.settings.buttons.use-own-inventory.lore-no-permission").split("\n"));
            return;
        }

        final Settings settings = settingManager.getSafely(player);
        final String ownInventory = settings.isOwnInventory() ? lang.getMessage("GENERAL.enabled") : lang.getMessage("GENERAL.disabled");
        final String lore = plugin.getLang().getMessage("GUI.settings.buttons.use-own-inventory.lore", "own_inventory", ownInventory);
        setLore(lore.split("\n"));
    }

    @Override
    public void onClick(final Player player) {
        if (config.isOwnInventoryUsePermission() && !player.hasPermission("duels.use.own-inventory") && !player.hasPermission("duels.use.*")) {
            lang.sendMessage(player, "ERROR.no-permission", "permission", "duels.use.own-inventory");
            return;
        }

        if (!config.isKitSelectingEnabled()) {
            lang.sendMessage(player, "ERROR.duel.mode-fixed");
            return;
        }

        final Settings settings = settingManager.getSafely(player);
        settings.setOwnInventory(!settings.isOwnInventory());
        settings.updateGui(player);
    }
}
