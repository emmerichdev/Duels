package com.meteordevelopments.duels.gui.inventory;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.gui.inventory.buttons.*;
import com.meteordevelopments.duels.util.CommonItems;
import com.meteordevelopments.duels.util.gui.SinglePageGui;
import com.meteordevelopments.duels.util.inventory.Slots;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryGui extends SinglePageGui<DuelsPlugin> {

    public InventoryGui(final DuelsPlugin plugin, final Player player, final boolean dead) {
        super(plugin, plugin.getLang().getMessage("GUI.inventory-view.title", "name", player.getName()), 6);

        final ItemStack spacing = CommonItems.GRAY_PANE.clone();
        Slots.run(0, 9, slot -> inventory.setItem(slot, spacing));
        set(4, new HeadButton(plugin, player));

        int potions = 0;
        int slot = 9;

        for (final ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                if (CommonItems.isHealSplash(item)) {
                    potions++;
                }

                inventory.setItem(slot, item.clone());
            }

            slot++;
        }

        slot = 48;

        for (final ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR) {
                inventory.setItem(slot, item.clone());
            }

            slot--;
        }

        inventory.setItem(49, spacing);
        set(50, new PotionCounterButton(plugin, potions));
        set(51, new EffectsButton(plugin, player));
        set(52, new HungerButton(plugin, player));
        set(53, new HealthButton(plugin, player, dead));
    }
}
