package com.emmerichbrowne.duels.menus;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaManagerImpl;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.kit.KitManagerImpl;
import com.emmerichbrowne.duels.queue.QueueManager;
import com.emmerichbrowne.duels.queue.sign.QueueSignManagerImpl;
import com.emmerichbrowne.duels.request.RequestManager;
import com.emmerichbrowne.duels.setting.SettingsManager;
import com.emmerichbrowne.duels.spectate.SpectateManagerImpl;
import com.emmerichbrowne.duels.util.CC;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class BaseButton {

	protected final DuelsPlugin plugin;
	@Getter @Setter
	private ItemStack displayed;

	protected final Config config;
	protected final Lang lang;
	protected final KitManagerImpl kitManager;
	protected final ArenaManagerImpl arenaManager;
	protected final SettingsManager settingManager;
	protected final QueueManager queueManager;
	protected final QueueSignManagerImpl queueSignManager;
	protected final SpectateManagerImpl spectateManager;
	protected final RequestManager requestManager;

	protected BaseButton(final DuelsPlugin plugin, final ItemStack displayed) {
		this.plugin = plugin;
		this.displayed = displayed;
		this.config = plugin.getConfiguration();
		this.lang = plugin.getLang();
		this.kitManager = plugin.getKitManager();
		this.arenaManager = plugin.getArenaManager();
		this.settingManager = plugin.getSettingManager();
		this.queueManager = plugin.getServerRole() == com.emmerichbrowne.duels.core.ServerRole.LOBBY ? plugin.getQueueManager() : null;
		this.queueSignManager = plugin.getServerRole() == com.emmerichbrowne.duels.core.ServerRole.LOBBY ? plugin.getQueueSignManager() : null;
		this.spectateManager = plugin.getSpectateManager();
		this.requestManager = plugin.getRequestManager();
	}

	// Triumph adapter
	public GuiItem toGuiItem(final Player player) {
		final ItemStack item = getDisplayed().clone();
		return new GuiItem(item, e -> {
			e.setCancelled(true);
			final Player clicker = player != null ? player : (Player) e.getWhoClicked();
			onClick(clicker);
		});
	}

	protected void editMeta(final Consumer<ItemMeta> consumer) {
		final ItemMeta meta = getDisplayed().getItemMeta();
		consumer.accept(meta);
		getDisplayed().setItemMeta(meta);
	}

	protected void setDisplayName(final String name) {
		editMeta(meta -> meta.displayName(parseComponent(name)));
	}

	protected void setLore(final List<String> lore) {
		editMeta(meta -> meta.lore(lore.stream().map(this::parseComponent).collect(Collectors.toList())));
	}

	protected void setLore(final String... lore) {
		setLore(Arrays.asList(lore));
	}

	protected void setLore(final Component... components) {
		editMeta(meta -> meta.lore(Arrays.asList(components)));
	}

	private Component parseComponent(final String text) {
		return LegacyComponentSerializer.legacySection().deserialize(CC.translate(text));
	}

	protected void playClickSound(final Player player) {
		playClickSound(player, Sound.Source.MASTER);
	}

	protected void playClickSound(final Player player, final Sound.Source source) {
		player.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, source, 0.5f, 1.0f));
	}

	protected void playErrorSound(final Player player) {
		player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_VILLAGER_NO, Sound.Source.MASTER, 0.5f, 1.0f));
	}

    protected void setOwner(final Player player) {
		if (CommonItems.equals(displayed, CommonItems.HEAD)) {
			editMeta(meta -> ((SkullMeta) meta).setOwningPlayer(player));
		}
	}

	protected void setGlow(final boolean glow) {
		// Golden apples special-case for enchant glint
		if (displayed.getType().name().endsWith("GOLDEN_APPLE")) {
			final ItemStack item = glow ? CommonItems.ENCHANTED_GOLDEN_APPLE.clone() : ItemBuilder.of(Material.GOLDEN_APPLE).build();
			item.setItemMeta(getDisplayed().getItemMeta());
			setDisplayed(item);
			return;
		}
		editMeta(meta -> {
			if (glow) {
				meta.addEnchant(Enchantment.UNBREAKING, 1, false);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			} else {
				meta.removeEnchant(Enchantment.UNBREAKING);
				meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
		});
	}

    public void update(final Player player) {}

	public void onClick(final Player player) {}

}
