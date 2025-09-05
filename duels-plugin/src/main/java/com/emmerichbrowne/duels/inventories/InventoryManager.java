package com.emmerichbrowne.duels.inventories;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.menus.inventory.InventoryMenu;
import com.emmerichbrowne.duels.match.DuelMatch;
import com.emmerichbrowne.duels.util.CC;
import com.emmerichbrowne.duels.util.Loadable;
import com.emmerichbrowne.duels.util.TextBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.util.*;

public class InventoryManager implements Loadable {

	private final DuelsPlugin plugin;
	private final Map<UUID, InventoryMenu> inventories = new HashMap<>();
	private final Map<UUID, Long> createdAt = new HashMap<>();
	private final Config config;
	private final Lang lang;

	private ScheduledTask expireTask;

	public InventoryManager(final DuelsPlugin plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfiguration();
		this.lang = plugin.getLang();
	}

	@Override
	public void handleLoad() {
		this.expireTask = plugin.doSyncRepeat(() -> {
			final long now = System.currentTimeMillis();
			final Iterator<Map.Entry<UUID, Long>> it = createdAt.entrySet().iterator();
			while (it.hasNext()) {
				final Map.Entry<UUID, Long> e = it.next();
				if (now - e.getValue() >= 1000L * 60 * 5) {
					inventories.remove(e.getKey());
					it.remove();
				}
			}
		}, 20L, 20L * 5);
	}

	@Override
	public void handleUnload() {
		plugin.cancelTask(expireTask);
		inventories.clear();
		createdAt.clear();
	}

	public InventoryMenu get(final UUID uuid) {
		return inventories.get(uuid);
	}

	public void create(final Player player, final boolean dead) {
		final InventoryMenu gui = new InventoryMenu(plugin, player, dead);
		inventories.put(player.getUniqueId(), gui);
		createdAt.put(player.getUniqueId(), System.currentTimeMillis());
	}

	public void open(final Player player) {
		final InventoryMenu gui = inventories.get(player.getUniqueId());
		if (gui != null) gui.open(player);
	}

	public void handleMatchEnd(final DuelMatch match) {
		if (!config.isDisplayInventories()) {
			return;
		}

		String color = lang.getMessage("DUEL.inventories.name-color");
		final TextBuilder builder = TextBuilder.of(lang.getMessage("DUEL.inventories.message"));
		final Set<Player> players = match.getAllPlayers();
		final Iterator<Player> iterator = players.iterator();

		while (iterator.hasNext()) {
			final Player player = iterator.next();
			builder.add(CC.translate(color + player.getName()), ClickEvent.Action.RUN_COMMAND, "/duel _ " + player.getUniqueId());

			if (iterator.hasNext()) {
				builder.add(CC.translate(color + ", "));
			}
		}

		builder.send(players);
	}
}
