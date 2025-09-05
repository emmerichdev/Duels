package com.emmerichbrowne.duels.gui.bind;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.gui.bind.buttons.BindButton;
import com.emmerichbrowne.duels.kit.KitImpl;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BindGui {

	private final PaginatedGui gui;
	private final DuelsPlugin plugin;
	private final KitImpl kit;

	public BindGui(final DuelsPlugin plugin, final KitImpl kit) {
		this.plugin = plugin;
		this.kit = kit;
		final int rows = plugin.getConfiguration().getArenaSelectorRows();
		this.gui = new PaginatedGui(rows, 9, LegacyComponentSerializer.legacySection().deserialize(plugin.getLang().getMessage("GUI.options.bind.title", "kit", kit.getName())));

		this.gui.setDefaultClickAction(event -> event.setCancelled(true));

		final Config config = plugin.getConfiguration();
		final Lang lang = plugin.getLang();

		// Fill background with filler
		final org.bukkit.inventory.ItemStack filler = CommonItems.from(config.getArenaSelectorFillerType());
		this.gui.getFiller().fill(ItemBuilder.from(filler).asGuiItem());

		// Prev/next buttons
		final org.bukkit.inventory.ItemStack prev = ItemBuilder.from(ItemBuilder.of(Material.PAPER).name(lang.getMessage("GUI.arena-selector.buttons.previous-page.name")).build()).build();
		final org.bukkit.inventory.ItemStack next = ItemBuilder.from(ItemBuilder.of(Material.PAPER).name(lang.getMessage("GUI.arena-selector.buttons.next-page.name")).build()).build();
		this.gui.setItem(rows, 3, ItemBuilder.from(prev).asGuiItem(e -> gui.previous()))
				.setItem(rows, 7, ItemBuilder.from(next).asGuiItem(e -> gui.next()));

		rebuildItems();
	}

	private void rebuildItems() {
		gui.clearPageItems();
		plugin.getArenaManager().getArenasImpl().stream()
				.map(arena -> new BindButton(plugin, kit, arena))
				.peek(button -> button.setGui(this))
				.map(button -> ItemBuilder.from(button.getDisplayed().clone()).asGuiItem(e -> button.onClick((Player) e.getWhoClicked())))
				.forEach(gui::addItem);
	}

	public void open(Player player) {
		gui.open(player);
	}

	public void calculatePages() {
		rebuildItems();
	}
}
