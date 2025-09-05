package com.emmerichbrowne.duels.menus.settings;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.menus.BaseButton;
import com.emmerichbrowne.duels.menus.settings.buttons.*;
import com.emmerichbrowne.duels.util.CommonItems;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SettingsMenu {
	private final DuelsPlugin plugin;
	private final Gui gui;

	public SettingsMenu(final DuelsPlugin plugin) {
		this.plugin = plugin;
		this.gui = Gui.gui()
				.title(LegacyComponentSerializer.legacySection().deserialize(plugin.getLang().getMessage("GUI.settings.title")))
				.rows(6)
				.create();
		this.gui.setDefaultClickAction(e -> e.setCancelled(true));
	}

	public void open(final Player player) {
		populate(player);
		gui.open(player);
	}

	public void update(final Player player) {
		populate(player);
	}

	private void populate(final Player player) {
        gui.getFiller().fill(new GuiItem(CommonItems.WHITE_PANE.clone()));

		// Top row controls
		setButton(2, new RequestSendButton(plugin), player);
		setButton(4, new RequestDetailsButton(plugin), player);
		setButton(6, new CancelButton(plugin), player);

		// Arena and kit selectors
		setButtonRange(20, 24, new ArenaSelectButton(plugin), player);
		setButtonRange(22, 26, new KitSelectButton(plugin), player);

		// Toggle options
		setButton(38, new OwnInventoryButton(plugin), player);
		setButton(40, new ItemBettingButton(plugin), player);
	}

	private void setButton(final int slot, final BaseButton button, final Player player) {
		gui.setItem(slot, button.toGuiItem(player));
	}

	private void setButtonRange(final int from, final int to, final BaseButton button, final Player player) {
		for (int row = 0; row < 1; row++) {
			for (int col = from; col <= to; col++) {
                gui.setItem(col, button.toGuiItem(player));
			}
		}
	}

	@SuppressWarnings("unused")
	private void fillRange(final int from, final int to, final int height, final ItemStack item) {
		for (int row = 0; row < height; row++) {
			for (int col = from; col <= to; col++) {
				final int slot = 9 * row + col;
				gui.setItem(slot, new GuiItem(item.clone()));
			}
		}
	}
}
