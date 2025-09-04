package com.emmerichbrowne.duels.gui.inventory.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.gui.BaseButton;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.entity.Player;

public class HeadButton extends BaseButton {

    public HeadButton(final DuelsPlugin plugin, final Player owner) {
        super(plugin, ItemBuilder
                .of(CommonItems.HEAD.clone())
                .name(plugin.getLang().getMessage("GUI.inventory-view.buttons.head.name", "name", owner.getName()))
                .build()
        );
        setOwner(owner);
    }
}
