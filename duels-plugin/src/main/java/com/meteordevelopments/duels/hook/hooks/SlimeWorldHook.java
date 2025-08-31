package com.meteordevelopments.duels.hook.hooks;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.util.hook.PluginHook;
import com.meteordevelopments.duels.world.ArenaWorldProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Optional integration with InfernalSuite's Slime World Manager (ASWM/ASP).
 * If present, provides per-match world instances; otherwise falls back to vanilla provider.
 */
public class SlimeWorldHook extends PluginHook<DuelsPlugin> implements ArenaWorldProvider {

	public static final String NAME = "SlimeWorldManager";

	private final boolean available;
	// Use reflection to avoid hard dependency when not present at runtime
	private final Object aspApi; // AdvancedSlimePaperAPI

	public SlimeWorldHook(final DuelsPlugin plugin) {
		super(plugin, NAME);
		Object api = null;
		boolean ok;
		try {
			// Try both old/new class names
			Class<?> apiClass = tryClass(
					"com.infernalsuite.aswm.api.AdvancedSlimePaperAPI",
					"com.infernalsuite.aswm.api.SlimePaperAPI"
			);
			api = Bukkit.getServicesManager().load(apiClass);
			ok = api != null;
		} catch (Throwable ignored) {
			ok = false;
		}
		this.available = ok;
		this.aspApi = api;
	}

	private static @Nullable Class<?> tryClass(String... names) throws ClassNotFoundException {
		for (String n : names) {
			try { return Class.forName(n); } catch (ClassNotFoundException ignored) {}
		}
		throw new ClassNotFoundException(names[0]);
	}

	public boolean isAvailable() { return available; }

	@Override
	public @Nullable World acquireWorld(@NotNull String templateWorldName, @NotNull String instanceId) {
		if (!available) return null;
		try {
			// Resolve loader via services; prefer in-memory or default
			Class<?> loaderClass = Class.forName("com.infernalsuite.aswm.api.loaders.SlimeLoader");
			Object loader = Bukkit.getServicesManager().load(loaderClass);
			if (loader == null) {
				return null;
			}

			Class<?> propertyMapClass = Class.forName("com.infernalsuite.aswm.api.world.properties.SlimePropertyMap");
			Object props = propertyMapClass.getConstructor().newInstance();

			// readWorld(loader, name, readOnly, properties)
			Object slimeWorld = aspApi.getClass()
					.getMethod("readWorld", loaderClass, String.class, boolean.class, propertyMapClass)
					.invoke(aspApi, loader, templateWorldName, false, props);

			// loadWorld(slimeWorld, copy)
			Object worldInstance = aspApi.getClass()
					.getMethod("loadWorld", Class.forName("com.infernalsuite.aswm.api.world.SlimeWorld"), boolean.class)
					.invoke(aspApi, slimeWorld, true);
			World bukkitWorld = (World) worldInstance.getClass().getMethod("getBukkitWorld").invoke(worldInstance);
			return bukkitWorld;
		} catch (Throwable ignored) {
			return null;
		}
	}

	@Override
	public @Nullable Location toInstanceLocation(@Nullable Location templateLocation, @NotNull World instanceWorld) {
		if (templateLocation == null) return null;
		return new Location(
				instanceWorld,
				templateLocation.getX(),
				templateLocation.getY(),
				templateLocation.getZ(),
				templateLocation.getYaw(),
				templateLocation.getPitch()
		);
	}

	@Override
	public void releaseWorld(@Nullable World instanceWorld) {
		if (!available || instanceWorld == null) return;
		try {
			// Attempt to unload via Bukkit; ASP may clean up resources accordingly
			Bukkit.unloadWorld(instanceWorld, false);
		} catch (Throwable ignored) {
		}
	}
}

