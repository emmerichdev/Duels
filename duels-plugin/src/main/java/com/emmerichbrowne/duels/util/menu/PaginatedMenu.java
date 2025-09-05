package com.emmerichbrowne.duels.util.menu;

import com.emmerichbrowne.duels.menus.BaseButton;
import com.emmerichbrowne.duels.util.CommonItems;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.triumphteam.gui.guis.Gui;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class PaginatedMenu {

	private final int rows;
	@Getter
	private final Collection<? extends BaseButton> buttons;
	private final PaginatedGui gui;

	private ItemStack emptyIndicator;

	public PaginatedMenu(final String title, final int rows, final Collection<? extends BaseButton> buttons) {
		this.rows = rows;
		this.buttons = buttons;
		this.gui = Gui.paginated()
				.title(LegacyComponentSerializer.legacySection().deserialize(title))
				.rows(rows)
				.create();
		this.gui.setDefaultClickAction(e -> e.setCancelled(true));
	}

	public void setSpaceFiller(final ItemStack filler) {
		ItemStack spaceFiller = filler != null ? filler.clone() : CommonItems.WHITE_PANE.clone();
		// PaginatedGui does not support full fill; use border fill for background framing
		this.gui.getFiller().fillBorder(ItemBuilder.from(spaceFiller).asGuiItem());
	}

	public void setPrevButton(final ItemStack prev) {
		ItemStack prevButton = prev != null ? prev.clone() : null;
		if (prevButton != null) {
			this.gui.setItem(rows, 3, ItemBuilder.from(prevButton).asGuiItem(e -> gui.previous()));
		}
	}

	public void setNextButton(final ItemStack next) {
		ItemStack nextButton = next != null ? next.clone() : null;
		if (nextButton != null) {
			this.gui.setItem(rows, 7, ItemBuilder.from(nextButton).asGuiItem(e -> gui.next()));
		}
	}

	public void setEmptyIndicator(final ItemStack empty) {
		this.emptyIndicator = empty != null ? empty.clone() : null;
	}

	public void calculatePages() {
		this.gui.clearPageItems();
		if (buttons == null || buttons.isEmpty()) {
			if (emptyIndicator != null) {
				this.gui.setItem(1, 5, ItemBuilder.from(emptyIndicator).asGuiItem());
			}
			return;
		}
		for (final BaseButton button : buttons) {
			this.gui.addItem(button.toGuiItem(null));
		}
	}

	public void open(final Player player) {
		this.gui.open(player);
	}
}
