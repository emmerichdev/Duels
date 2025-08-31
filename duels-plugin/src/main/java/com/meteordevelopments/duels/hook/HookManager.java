package com.meteordevelopments.duels.hook;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.hook.hooks.DeluxeCombatHook;
import com.meteordevelopments.duels.hook.hooks.EssentialsHook;
import com.meteordevelopments.duels.hook.hooks.PlaceholderHook;
import com.meteordevelopments.duels.hook.hooks.SlimeWorldHook;
import com.meteordevelopments.duels.hook.hooks.VaultHook;
import com.meteordevelopments.duels.hook.hooks.worldguard.WorldGuardHook;
import com.meteordevelopments.duels.util.hook.AbstractHookManager;

public class HookManager extends AbstractHookManager<DuelsPlugin> {

    public HookManager(final DuelsPlugin plugin) {
        super(plugin);
        register(DeluxeCombatHook.NAME, DeluxeCombatHook.class);
        register(EssentialsHook.NAME, EssentialsHook.class);
        register(PlaceholderHook.NAME, PlaceholderHook.class);
        register(VaultHook.NAME, VaultHook.class);
        register(WorldGuardHook.NAME, WorldGuardHook.class);
        // SlimeWorldManager is required; register unconditionally (plugin.yml depend ensures presence)
        register(SlimeWorldHook.NAME, SlimeWorldHook.class);
    }
}
