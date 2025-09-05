package com.emmerichbrowne.duels.menus.betting.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.menus.BaseButton;
import com.emmerichbrowne.duels.menus.betting.BettingMenu;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StateButton extends BaseButton {

    private final BettingMenu gui;
    private final UUID owner;

    public StateButton(final DuelsPlugin plugin, final BettingMenu gui, final Player owner) {
        super(plugin, ItemBuilder
                .of(CommonItems.OFF.clone())
                .name(plugin.getLang().getMessage("GUI.item-betting.buttons.state.name-not-ready"))
                .build()
        );
        this.gui = gui;
        this.owner = owner.getUniqueId();
    }

    @Override
    public void onClick(final Player player) {
        if (!gui.isReady(player) && player.getUniqueId().equals(owner)) {
            setDisplayed(CommonItems.ON.clone());
            setDisplayName(lang.getMessage("GUI.item-betting.buttons.state.name-ready"));
            gui.setReady(player);
        }
    }
}
