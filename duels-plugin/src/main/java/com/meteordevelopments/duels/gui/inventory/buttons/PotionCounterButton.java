package com.meteordevelopments.duels.gui.inventory.buttons;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.gui.BaseButton;
import com.meteordevelopments.duels.util.CommonItems;
import com.meteordevelopments.duels.util.inventory.ItemBuilder;
import org.bukkit.inventory.ItemFlag;

public class PotionCounterButton extends BaseButton {

    public PotionCounterButton(final DuelsPlugin plugin, final int count) {
        super(plugin, ItemBuilder
                .of(CommonItems.HEAL_SPLASH_POTION.clone())
                .name(plugin.getLang().getMessage("GUI.inventory-view.buttons.potion-counter.name", "potions", count))
                .build()
        );
        editMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES));
    }
}
