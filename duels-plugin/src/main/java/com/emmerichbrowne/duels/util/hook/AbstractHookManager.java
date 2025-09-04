package com.emmerichbrowne.duels.util.hook;

import com.emmerichbrowne.duels.DuelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class AbstractHookManager<P extends JavaPlugin> {

    protected final P plugin;
    private final Map<Class<? extends PluginHook<P>>, PluginHook<P>> hooks = new HashMap<>();

    public AbstractHookManager(final P plugin) {
        this.plugin = plugin;
    }

    protected void register(final String name, final Class<? extends PluginHook<P>> clazz) {
        final Plugin target = Bukkit.getPluginManager().getPlugin(name);

        if (target == null || !target.isEnabled()) {
            return;
        }

        try {
            if (hooks.putIfAbsent(clazz, clazz.getConstructor(plugin.getClass()).newInstance(plugin)) != null) {
                plugin.getLogger().warning("Failed to hook into " + name + ": There was already a hook registered with same name");
                return;
            }

            DuelsPlugin.sendMessage(((DuelsPlugin)plugin).getLang().getMessage("SYSTEM.hooks.successful", "name", name));
        } catch (Throwable throwable) {
            Throwable throwable1 = throwable;
            if (throwable1.getCause() != null) {
                throwable1 = throwable1.getCause();
            }

            final String message = String.format("Failed to hook into %s: %s (%s)", 
                    name, throwable1.getMessage(), throwable1.getClass().getName());
            plugin.getLogger().log(Level.SEVERE, message, throwable1);
        }
    }

    public <T extends PluginHook<P>> T getHook(Class<T> clazz) {
        return clazz != null ? clazz.cast(hooks.get(clazz)) : null;
    }
}
