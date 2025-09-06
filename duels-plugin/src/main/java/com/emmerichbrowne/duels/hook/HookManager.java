package com.emmerichbrowne.duels.hook;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.hook.hooks.DeluxeCombatHook;
import com.emmerichbrowne.duels.hook.hooks.EssentialsHook;
import com.emmerichbrowne.duels.hook.hooks.PlaceholderHook;
import com.emmerichbrowne.duels.hook.hooks.VaultHook;
import com.emmerichbrowne.duels.hook.hooks.worldguard.WorldGuardHook;
import com.emmerichbrowne.duels.util.hook.AbstractHookManager;
import me.clip.placeholderapi.PlaceholderAPIPlugin;

public class HookManager extends AbstractHookManager<DuelsPlugin> {

    public HookManager(final DuelsPlugin plugin) {
        super(plugin);
        register(DeluxeCombatHook.NAME, DeluxeCombatHook.class);
        register(EssentialsHook.NAME, EssentialsHook.class);
        // PlaceholderAPI is a special case; register expansion if present
        if (plugin.getServer().getPluginManager().getPlugin(PlaceholderHook.NAME) instanceof PlaceholderAPIPlugin) {
            new PlaceholderHook(plugin).register();
        }
        register(VaultHook.NAME, VaultHook.class);
        register(WorldGuardHook.NAME, WorldGuardHook.class);
    }
}
