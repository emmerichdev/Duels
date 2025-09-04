package com.emmerichbrowne.duels.gui.settings;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.gui.BaseButton;
import com.emmerichbrowne.duels.gui.settings.buttons.*;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.gui.SinglePageGui;
import com.emmerichbrowne.duels.util.inventory.Slots;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettingsGui extends SinglePageGui<DuelsPlugin> {

    private static final int[][] PATTERNS = {
            {13},
            {12, 14},
            {12, 13, 14},
            {12, 13, 14, 22}
    };

    public SettingsGui(final DuelsPlugin plugin) {
        super(plugin, plugin.getLang().getMessage("GUI.settings.title"), 3);
        final Config config = plugin.getConfiguration();
        final ItemStack spacing = CommonItems.from(config.getSettingsFillerType());
        Slots.run(2, 7, slot -> inventory.setItem(slot, spacing));
        Slots.run(11, 16, slot -> inventory.setItem(slot, spacing));
        Slots.run(20, 25, slot -> inventory.setItem(slot, spacing));
        set(4, new RequestDetailsButton(plugin));

        final List<BaseButton> buttons = new ArrayList<>();

        if (config.isKitSelectingEnabled()) {
            buttons.add(new KitSelectButton(plugin));
        }

        if (config.isOwnInventoryEnabled()) {
            buttons.add(new OwnInventoryButton(plugin));
        }

        if (config.isArenaSelectingEnabled()) {
            buttons.add(new ArenaSelectButton(plugin));
        }

        if (config.isItemBettingEnabled()) {
            buttons.add(new ItemBettingButton(plugin));
        }

        if (!buttons.isEmpty()) {
            final int[] pattern = PATTERNS[buttons.size() - 1];

            for (int i = 0; i < buttons.size(); i++) {
                set(pattern[i], buttons.get(i));
            }
        }

        set(0, 2, 3, new RequestSendButton(plugin));
        set(7, 9, 3, new CancelButton(plugin));
    }
}
