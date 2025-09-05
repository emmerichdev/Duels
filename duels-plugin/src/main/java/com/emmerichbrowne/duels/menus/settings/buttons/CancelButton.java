package com.emmerichbrowne.duels.menus.settings.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.menus.BaseButton;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.entity.Player;

public class CancelButton extends BaseButton {

    public CancelButton(final DuelsPlugin plugin) {
        super(plugin, ItemBuilder.of(CommonItems.RED_PANE.clone()).name(plugin.getLang().getMessage("GUI.settings.buttons.cancel.name")).build());
    }

    @Override
    public void onClick(final Player player) {
        playClickSound(player);
        player.closeInventory();
    }
}
