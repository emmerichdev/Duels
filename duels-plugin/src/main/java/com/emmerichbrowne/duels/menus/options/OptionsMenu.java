package com.emmerichbrowne.duels.menus.options;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.menus.options.buttons.OptionButton;
import com.emmerichbrowne.duels.kit.KitImpl;
import com.emmerichbrowne.duels.kit.KitImpl.Characteristic;
import com.emmerichbrowne.duels.util.CommonItems;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class OptionsMenu {

    private final Gui gui;

	public OptionsMenu(final DuelsPlugin plugin, final Player player, final KitImpl kit) {
        this.gui = Gui.gui()
				.title(LegacyComponentSerializer.legacySection().deserialize(plugin.getLang().getMessage("GUI.options.title", "kit", kit.getName())))
				.rows(2)
				.create();

		this.gui.setDefaultClickAction(event -> event.setCancelled(true));

		int i = 0;
		for (final Option option : Option.values()) {
			final OptionButton btn = new OptionButton(plugin, this, kit, option);
			btn.update(player);
			gui.setItem(i++, btn.toGuiItem(player));
		}

		final ItemStack spacing = CommonItems.WHITE_PANE.clone();
		for (int slot = 9; slot < 18; slot++) {
			gui.setItem(slot, new GuiItem(spacing.clone(), e -> e.setCancelled(true)));
		}
	}

	public void open(final Player player) {
		gui.open(player);
	}

	public void update(final Player player, final OptionButton button) {
		// Find index of this option in enum order and replace that slot
		int index = button.getOption().ordinal();
		gui.setItem(index, button.toGuiItem(player));
	}

	public enum Option {

		USEPERMISSION(Material.BARRIER, KitImpl::isUsePermission, kit -> kit.setUsePermission(!kit.isUsePermission()), "When enabled, players must", "have the permission duels.kits.%kit%", "to select this kit for duel."),
		ARENASPECIFIC(CommonItems.EMPTY_MAP, KitImpl::isArenaSpecific, kit -> kit.setArenaSpecific(!kit.isArenaSpecific()), "When enabled, kit %kit%", "can only be used in", "arenas it is bound to."),
		SOUP(CommonItems.MUSHROOM_SOUP, Characteristic.SOUP, "When enabled, players will", "receive the amount of health", "defined in config when", "right-clicking a soup."),
		SUMO(Material.SLIME_BALL, Characteristic.SUMO, "When enabled, players will ", "lose health only when", "interacting with water or lava."),
		UHC(Material.GOLDEN_APPLE, Characteristic.UHC, "When enabled, player's health", "will not naturally regenerate."),
		COMBO(Material.IRON_SWORD, Characteristic.COMBO, "When enabled, players will", "have no delay between hits."),
		LOKA(Material.DIAMOND_SWORD, Characteristic.LOKA, "When enabled, player,s damage", "will get increased by 33%"),
		HUNGER(Material.COOKED_BEEF, Characteristic.HUNGER, "When enabled, players will", "not hungry."),
		ROUNDS3(Material.GOLD_INGOT, Characteristic.ROUNDS3, "When enabled, duels will", "have 3 rounds.");
/*      PLACE(Material.STONE, Characteristic.PLACE, "When enabled, players can", "be placed blocks in arena."),
		BREAK(Material.STONE, Characteristic.BREAK, "When enabled, players can", "be break blocks in arena.");*/
		@Getter
		private final Material displayed;
		@Getter
		private final String[] description;

		private final Function<KitImpl, Boolean> getter;
		private final Consumer<KitImpl> setter;

		Option(final Material displayed, final Function<KitImpl, Boolean> getter, final Consumer<KitImpl> setter, final String... description) {
			this.displayed = displayed;
			this.description = description;
			this.getter = getter;
			this.setter = setter;
		}

		Option(final Material displayed, final Characteristic characteristic, final String... description) {
			this.displayed = displayed;
			this.description = description;
			this.getter = kit -> kit.hasCharacteristic(characteristic);
			this.setter = kit -> kit.toggleCharacteristic(characteristic);
		}

		public boolean get(final KitImpl kit) {
			return getter.apply(kit);
		}

		public void set(final KitImpl kit) {
			setter.accept(kit);
		}
	}
}