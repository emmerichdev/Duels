package com.emmerichbrowne.duels.gui.inventory.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.gui.BaseButton;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
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
