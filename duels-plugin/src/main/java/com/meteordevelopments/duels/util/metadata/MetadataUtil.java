package com.meteordevelopments.duels.util.metadata;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public final class MetadataUtil {

    private MetadataUtil() {
    }

    public static Object get(final Plugin plugin, final Entity entity, final String key) {
        java.util.Objects.requireNonNull(plugin, "plugin must not be null");
        return entity.getMetadata(key).stream().filter(value -> value.getOwningPlugin() == plugin).findFirst().map(MetadataValue::value).orElse(null);
    }

    public static void put(final Plugin plugin, final Entity entity, final String key, final Object data) {
        entity.setMetadata(key, new FixedMetadataValue(plugin, data));
    }

    public static void remove(final Plugin plugin, final Entity entity, final String key) {
        java.util.Objects.requireNonNull(plugin, "plugin must not be null");
        entity.removeMetadata(key, plugin);
    }

    public static Object removeAndGet(final Plugin plugin, final Entity entity, final String key) {
        final Object value = get(plugin, entity, key);
        remove(plugin, entity, key);
        return value;
    }
}
