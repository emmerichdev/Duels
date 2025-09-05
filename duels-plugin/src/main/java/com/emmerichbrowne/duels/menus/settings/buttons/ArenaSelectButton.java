package com.emmerichbrowne.duels.menus.settings.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;

import com.emmerichbrowne.duels.menus.BaseButton;
import com.emmerichbrowne.duels.setting.Settings;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.entity.Player;

public class ArenaSelectButton extends BaseButton {

    public ArenaSelectButton(final DuelsPlugin plugin) {
        super(plugin, ItemBuilder.of(CommonItems.EMPTY_MAP).name(plugin.getLang().getMessage("GUI.settings.buttons.arena-selector.name")).build());
    }

    @Override
    public void update(final Player player) {
        if (config.isArenaSelectingUsePermission() && !player.hasPermission("duels.use.arena-select") && !player.hasPermission("duels.use.*\n")) {
            setLore(lang.getMessage("GUI.settings.buttons.arena-selector.lore-no-permission").split("\n"));
            return;
        }

        final Settings settings = settingManager.getSafely(player);
        final String arena = settings.getArena() != null ? settings.getArena().getName() : lang.getMessage("GENERAL.random");
        final String lore = lang.getMessage("GUI.settings.buttons.arena-selector.lore", "arena", arena);
        setLore(lore.split("\n"));
    }

    @Override
    public void onClick(final Player player) {
        if (config.isArenaSelectingUsePermission() && !player.hasPermission("duels.use.arena-select") && !player.hasPermission("duels.use.*\n")) {
            lang.sendMessage(player, "ERROR.no-permission", "permission", "duels.use.arena-select");
            return;
        }

        arenaManager.getGui().open(player);
    }
}