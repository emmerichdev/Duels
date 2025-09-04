package com.meteordevelopments.duels.gui.betting.buttons;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.gui.BaseButton;
import com.meteordevelopments.duels.setting.Settings;
import com.meteordevelopments.duels.util.CommonItems;
import com.meteordevelopments.duels.util.inventory.ItemBuilder;
import org.bukkit.entity.Player;

public class DetailsButton extends BaseButton {

    private final Settings settings;

    public DetailsButton(final DuelsPlugin plugin, final Settings settings) {
        super(plugin, ItemBuilder
                .of(CommonItems.SIGN)
                .name(plugin.getLang().getMessage("GUI.item-betting.buttons.details.name"))
                .build()
        );
        this.settings = settings;
    }

    @Override
    public void update(final Player player) {
        final String lore = lang.getMessage("GUI.item-betting.buttons.details.lore",
                "kit", settings.getKit() != null ? settings.getKit().getName() : lang.getMessage("GENERAL.not-selected"),
                "arena", settings.getArena() != null ? settings.getArena().getName() : lang.getMessage("GENERAL.random"),
                "bet_amount", settings.getBet()
        );
        setLore(lore.split("\n"));
    }
}
