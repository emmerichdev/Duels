package com.emmerichbrowne.duels.menus.betting.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.menus.BaseButton;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.entity.Player;

public class HeadButton extends BaseButton {

    public HeadButton(final DuelsPlugin plugin, final Player owner) {
        super(plugin, ItemBuilder
                .of(CommonItems.HEAD.clone())
                .name(plugin.getLang().getMessage("GUI.item-betting.buttons.head.name", "name", owner.getName()))
                .build()
        );
        setOwner(owner);
    }
}
