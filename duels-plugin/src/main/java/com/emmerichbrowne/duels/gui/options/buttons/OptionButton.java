package com.emmerichbrowne.duels.gui.options.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.gui.BaseButton;
import com.emmerichbrowne.duels.gui.options.OptionsGui;
import com.emmerichbrowne.duels.gui.options.OptionsGui.Option;
import com.emmerichbrowne.duels.kit.KitImpl;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionButton extends BaseButton {

	private final OptionsGui gui;
	private final KitImpl kit;
	private final Option option;

	public OptionButton(final DuelsPlugin plugin, final OptionsGui gui, final KitImpl kit, final Option option) {
		super(plugin, ItemBuilder.of(option.getDisplayed()).build());
		this.gui = gui;
		this.kit = kit;
		this.option = option;
		setDisplayName(plugin.getLang().getMessage("GUI.options.buttons.option.name", "name", option.name().toLowerCase()));
		update();
	}

	public Option getOption() {
		return option;
	}

	private void update() {
		final boolean state = option.get(kit);
		setGlow(state);

		final List<String> lore = new ArrayList<>();

		for (final String line : option.getDescription()) {
			lore.add("&f" + line.replace("%kit%", kit.getName()));
		}

		Collections.addAll(lore,
				lang.getMessage("GUI.options.buttons.option.lore", "state", state ? lang.getMessage("GENERAL.enabled") : lang.getMessage("GENERAL.disabled")).split("\n"));
		setLore(lore);
	}

	@Override
	public void onClick(final Player player) {
		option.set(kit);
		update();
		gui.update(player, this);
	}
}
