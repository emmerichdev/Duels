package com.emmerichbrowne.duels.menus.inventory;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.menus.inventory.buttons.EffectsButton;
import com.emmerichbrowne.duels.menus.inventory.buttons.HeadButton;
import com.emmerichbrowne.duels.menus.inventory.buttons.HealthButton;
import com.emmerichbrowne.duels.menus.inventory.buttons.HungerButton;
import com.emmerichbrowne.duels.menus.inventory.buttons.PotionCounterButton;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryMenu {

	private final DuelsPlugin plugin;
	private final Gui gui;
	private final long creation;

	public InventoryMenu(final DuelsPlugin plugin, final Player player, final boolean dead) {
		this.plugin = plugin;
		this.gui = Gui.gui()
				.title(LegacyComponentSerializer.legacySection().deserialize(plugin.getLang().getMessage("GUI.inventory.title", "name", player.getName())))
				.rows(6)
				.create();
		this.creation = System.currentTimeMillis();
		final ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		for (int i = 0; i < 9; i++) gui.setItem(i, new GuiItem(black.clone()));
		for (int i = 45; i < 54; i++) gui.setItem(i, new GuiItem(black.clone()));

		gui.setItem(4, new HeadButton(plugin, player).toGuiItem(player));

		gui.setItem(47, new GuiItem(new ItemStack(Material.ARROW)));
		gui.setItem(49, new GuiItem(new ItemStack(Material.EXPERIENCE_BOTTLE)));

		int potionCount = 0;
		final ItemStack[] inv = player.getInventory().getContents();
		for (int i = 0; i < 36; i++) {
			final ItemStack item = inv[i] != null ? inv[i].clone() : null;
			if (item != null && item.getType() != Material.AIR) {
				if (item.getType() == Material.POTION) potionCount++;
				gui.setItem(9 + i, new GuiItem(item));
			}
		}

		gui.setItem(50, new PotionCounterButton(plugin, potionCount).toGuiItem(player));
		gui.setItem(51, new EffectsButton(plugin, player).toGuiItem(player));
		gui.setItem(52, new HungerButton(plugin, player).toGuiItem(player));
		gui.setItem(53, new HealthButton(plugin, player, dead).toGuiItem(player));
	}

	public void open(final Player player) {
		gui.open(player);
	}

	public Gui getGui() {
		return gui;
	}

	public long getCreation() {
		return creation;
	}
}
