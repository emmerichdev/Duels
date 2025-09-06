package com.emmerichbrowne.duels.game;

import org.bukkit.plugin.java.JavaPlugin;

public final class DuelsGamePlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().info("Duels Game Plugin enabled");
	}

	@Override
	public void onDisable() {
		getLogger().info("Duels Game Plugin disabled");
	}
}

