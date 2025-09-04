package com.emmerichbrowne.duels.gui.inventory.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.gui.BaseButton;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HealthButton extends BaseButton {

    public HealthButton(final DuelsPlugin plugin, final Player player, final boolean dead) {
        super(plugin, ItemBuilder
                .of(dead ? CommonItems.SKELETON_HEAD : Material.GOLDEN_APPLE)
                .name(plugin.getLang().getMessage("GUI.inventory-view.buttons.health.name", "health", dead ? 0 : Math.ceil(player.getHealth()) * 0.5))
                .build());
    }
}
