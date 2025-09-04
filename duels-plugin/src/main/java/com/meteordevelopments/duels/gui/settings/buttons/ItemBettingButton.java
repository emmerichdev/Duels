package com.meteordevelopments.duels.gui.settings.buttons;

import com.meteordevelopments.duels.DuelsPlugin;

import com.meteordevelopments.duels.gui.BaseButton;
import com.meteordevelopments.duels.setting.Settings;
import com.meteordevelopments.duels.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ItemBettingButton extends BaseButton {

    public ItemBettingButton(final DuelsPlugin plugin) {
        super(plugin, ItemBuilder.of(Material.DIAMOND).name(plugin.getLang().getMessage("GUI.settings.buttons.item-betting.name")).build());
    }

    @Override
    public void update(final Player player) {
        if (config.isItemBettingUsePermission() && !player.hasPermission("duels.use.item-betting") && !player.hasPermission("duels.use.*")) {
            setLore(lang.getMessage("GUI.settings.buttons.item-betting.lore-no-permission").split("\n"));
            return;
        }

        final Settings settings = settingManager.getSafely(player);
        final String itemBetting = settings.isItemBetting() ? lang.getMessage("GENERAL.enabled") : lang.getMessage("GENERAL.disabled");
        final String lore = plugin.getLang().getMessage("GUI.settings.buttons.item-betting.lore", "item_betting", itemBetting);
        setLore(lore.split("\n"));
    }

    @Override
    public void onClick(final Player player) {
        if (config.isItemBettingUsePermission() && !player.hasPermission("duels.use.item-betting") && !player.hasPermission("duels.use.*")) {
            lang.sendMessage(player, "ERROR.no-permission", "permission", "duels.use.item-betting");
            return;
        }

        final Settings settings = settingManager.getSafely(player);

        if (settings.isPartyDuel()) {
            lang.sendMessage(player, "ERROR.party-duel.option-unavailable");
            return;
        }

        settings.setItemBetting(!settings.isItemBetting());
        settings.updateGui(player);
    }
}