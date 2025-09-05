package com.emmerichbrowne.duels.setting;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.util.Loadable;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SettingsManager implements Loadable {

    private final DuelsPlugin plugin;
    private final Map<UUID, Settings> cache = new ConcurrentHashMap<>();

    public SettingsManager(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleLoad() {
        // No initialization required
    }

    @Override
    public void handleUnload() {
        cache.clear();
    }

    public Settings getSafely(final Player player) {
        return cache.computeIfAbsent(player.getUniqueId(), result -> new Settings(plugin, player));
    }

    public void remove(final Player player) {
        cache.remove(player.getUniqueId());
    }
}
