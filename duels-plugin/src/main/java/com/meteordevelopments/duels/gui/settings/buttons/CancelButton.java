package com.meteordevelopments.duels.gui.settings.buttons;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.gui.BaseButton;
import com.meteordevelopments.duels.util.CommonItems;
import com.meteordevelopments.duels.util.inventory.ItemBuilder;
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
