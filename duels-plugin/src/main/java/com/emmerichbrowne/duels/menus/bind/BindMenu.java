package com.emmerichbrowne.duels.menus.bind;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.menus.bind.buttons.BindButton;
import com.emmerichbrowne.duels.kit.KitImpl;
import com.emmerichbrowne.duels.util.CommonItems;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BindMenu {

	private final DuelsPlugin plugin;
	private final KitImpl kit;
	private final PaginatedGui gui;

	public BindMenu(final DuelsPlugin plugin, final KitImpl kit) {
		this.plugin = plugin;
		this.kit = kit;
		this.gui = Gui.paginated()
				.title(LegacyComponentSerializer.legacySection().deserialize(plugin.getLang().getMessage("GUI.options.bind.title", "kit", kit.getName())))
				.rows(6)
				.pageSize(45)
				.create();
		this.gui.setDefaultClickAction(e -> e.setCancelled(true));
		calculatePages();
	}

	public void open(Player player) {
		gui.open(player);
	}

	public void refresh() {
		calculatePages();
		gui.update();
	}

	public void calculatePages() {
		gui.clearPageItems();
		for (final var arena : plugin.getArenaManager().getArenasImpl()) {
			final BindButton button = new BindButton(plugin, kit, arena);
			button.setGui(this);
			gui.addItem(button.toGuiItem(null));
		}
		final var filler = CommonItems.WHITE_PANE.clone();
		gui.getFiller().fillBottom(ItemBuilder.from(filler).asGuiItem());

		// Build localized prev/next items without using ItemBuilder.name(Component)
		final Component prevName = LegacyComponentSerializer.legacySection().deserialize(plugin.getLang().getMessage("GUI.kit-selector.buttons.previous-page.name"));
		final Component nextName = LegacyComponentSerializer.legacySection().deserialize(plugin.getLang().getMessage("GUI.kit-selector.buttons.next-page.name"));

		final ItemStack prevItem = new ItemStack(Material.PAPER);
		final ItemMeta prevMeta = prevItem.getItemMeta();
		prevMeta.displayName(prevName);
		prevItem.setItemMeta(prevMeta);
		final GuiItem prev = new GuiItem(prevItem, e -> gui.previous());

		final ItemStack nextItem = new ItemStack(Material.PAPER);
		final ItemMeta nextMeta = nextItem.getItemMeta();
		nextMeta.displayName(nextName);
		nextItem.setItemMeta(nextMeta);
		final GuiItem next = new GuiItem(nextItem, e -> gui.next());

		gui.setItem(6, 3, prev);
		gui.setItem(6, 7, next);
	}
}
