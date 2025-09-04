package com.emmerichbrowne.duels.gui.inventory.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.gui.BaseButton;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HungerButton extends BaseButton {

    public HungerButton(final DuelsPlugin plugin, final Player player) {
        super(plugin, ItemBuilder
                .of(Material.COOKED_BEEF)
                .name(plugin.getLang().getMessage("GUI.inventory-view.buttons.hunger.name", "hunger", player.getFoodLevel()))
                .build()
        );
    }
}
