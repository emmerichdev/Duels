package com.emmerichbrowne.duels.betting;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.menus.betting.BettingMenu;
import com.emmerichbrowne.duels.hook.hooks.VaultHook;
import com.emmerichbrowne.duels.setting.Settings;
import com.emmerichbrowne.duels.util.Loadable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BettingManager implements Loadable, Listener {

	private final DuelsPlugin plugin;

	public BettingManager(final DuelsPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void handleLoad() {
		final VaultHook vaultHook = plugin.getHookManager().getHook(VaultHook.class);

		if (vaultHook == null) {
			DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.hooks.vault-not-found"));
		} else if (vaultHook.getEconomy() == null) {
			DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.hooks.economy-not-found"));
		}
	}

	@Override
	public void handleUnload() {
	}

	public void open(final Settings settings, final Player sender, final Player target) {
		final BettingMenu gui = new BettingMenu(plugin, settings, sender, target);
		gui.open(sender, target);
	}
}
