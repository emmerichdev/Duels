package com.emmerichbrowne.duels.hook;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.hook.hooks.DeluxeCombatHook;
import com.emmerichbrowne.duels.hook.hooks.EssentialsHook;
import com.emmerichbrowne.duels.hook.hooks.PlaceholderHook;
import com.emmerichbrowne.duels.hook.hooks.VaultHook;
import com.emmerichbrowne.duels.hook.hooks.worldguard.WorldGuardHook;
import com.emmerichbrowne.duels.util.hook.AbstractHookManager;

public class HookManager extends AbstractHookManager<DuelsPlugin> {

    public HookManager(final DuelsPlugin plugin) {
        super(plugin);
        register(DeluxeCombatHook.NAME, DeluxeCombatHook.class);
        register(EssentialsHook.NAME, EssentialsHook.class);
        register(PlaceholderHook.NAME, PlaceholderHook.class);
        register(VaultHook.NAME, VaultHook.class);
        register(WorldGuardHook.NAME, WorldGuardHook.class);
    }
}
