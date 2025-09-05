package com.emmerichbrowne.duels.gui.settings;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.gui.BaseButton;
import com.emmerichbrowne.duels.gui.settings.buttons.*;
import com.emmerichbrowne.duels.util.CommonItems;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SettingsGui {

	private static final int[][] PATTERNS = {
				{13},
				{12, 14},
				{12, 13, 14},
				{12, 13, 14, 22}
	};

	private final DuelsPlugin plugin;
	private final Gui gui;

	public SettingsGui(final DuelsPlugin plugin) {
		this.plugin = plugin;
		this.gui = Gui.gui()
				.title(LegacyComponentSerializer.legacySection().deserialize(plugin.getLang().getMessage("GUI.settings.title")))
				.rows(3)
				.create();

		// Cancel all clicks by default
		this.gui.setDefaultClickAction(event -> event.setCancelled(true));
	}

	public void open(final Player player) {
		populate(player);
		gui.open(player);
	}

	public void update(final Player player) {
		populate(player);
	}

	private void populate(final Player player) {
		final Config config = plugin.getConfiguration();
		final ItemStack spacing = CommonItems.from(config.getSettingsFillerType());

		// Clear and fill background spacers in the same slots as before
		gui.getInventory().clear();
		fillRange(2, 7, 0, spacing);
		fillRange(11, 16, 0, spacing);
		fillRange(20, 25, 0, spacing);

		// Center head/details button
		setButton(4, new RequestDetailsButton(plugin), player);

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
				setButton(pattern[i], buttons.get(i), player);
			}
		}

		// Action bars (left: send; right: cancel) across 3 rows like before
		setButtonRange(0, 2, 3, new RequestSendButton(plugin), player);
		setButtonRange(7, 9, 3, new CancelButton(plugin), player);
	}

	private void setButton(final int slot, final BaseButton button, final Player player) {
		button.update(player);
		final ItemStack displayed = button.getDisplayed().clone();
		final GuiItem item = new GuiItem(displayed, event -> button.onClick(player));
		gui.setItem(slot, item);
	}

	private void setButtonRange(final int from, final int to, final int height, final BaseButton button, final Player player) {
		button.update(player);
		final ItemStack displayed = button.getDisplayed().clone();
		final GuiItem item = new GuiItem(displayed, event -> button.onClick(player));
		for (int h = 0; h < height; h++) {
			for (int s = from; s < to; s++) {
				gui.setItem(s + h * 9, item);
			}
		}
	}

	private void fillRange(final int from, final int to, final int height, final ItemStack item) {
		for (int h = 0; h < height + 1; h++) {
			for (int s = from; s < to; s++) {
				gui.setItem(s + h * 9, new GuiItem(item.clone(), e -> e.setCancelled(true)));
			}
		}
	}
}
