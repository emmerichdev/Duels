package com.emmerichbrowne.duels.gui.betting.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.gui.BaseButton;
import com.emmerichbrowne.duels.setting.Settings;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
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
