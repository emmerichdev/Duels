package com.emmerichbrowne.duels.gui.settings.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;

import com.emmerichbrowne.duels.gui.BaseButton;
import com.emmerichbrowne.duels.setting.Settings;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KitSelectButton extends BaseButton {

    public KitSelectButton(final DuelsPlugin plugin) {
        super(plugin, ItemBuilder.of(Material.DIAMOND_SWORD).name(plugin.getLang().getMessage("GUI.settings.buttons.kit-selector.name")).build());
    }

    @Override
    public void update(final Player player) {
        if (config.isKitSelectingUsePermission() && !player.hasPermission("duels.use.kit-select") && !player.hasPermission("duels.use.*")) {
            setLore(lang.getMessage("GUI.settings.buttons.kit-selector.lore-no-permission").split("\n"));
            return;
        }

        final Settings settings = settingManager.getSafely(player);
        final String kit = settings.getKit() != null ? settings.getKit().getName() : lang.getMessage("GENERAL.not-selected");
        final String lore = lang.getMessage("GUI.settings.buttons.kit-selector.lore", "kit", kit);
        setLore(lore.split("\n"));
    }

    @Override
    public void onClick(final Player player) {
        if (config.isKitSelectingUsePermission() && !player.hasPermission("duels.use.kit-select") && !player.hasPermission("duels.use.*")) {
            playErrorSound(player);
            lang.sendMessage(player, "ERROR.no-permission", "permission", "duels.use.kit-select");
            return;
        }

        playClickSound(player);
        kitManager.getGui().open(player);
    }
}