package com.meteordevelopments.duels.startup;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.listeners.*;
import com.meteordevelopments.duels.util.CC;
import org.bukkit.Bukkit;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListenerManager {
    
    private final DuelsPlugin plugin;
    private final List<Listener> registeredListeners = new ArrayList<>();
    
    public ListenerManager(DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerPreListeners() {
        long start = System.currentTimeMillis();
        DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.startup.registering-listeners"));
        
        registerListener(new KitItemListener(plugin));
        registerListener(new DamageListener(plugin));
        registerListener(new PotionListener(plugin));
        registerListener(new TeleportListener(plugin));
        registerListener(new ProjectileHitListener(plugin));
        registerListener(new EnderpearlListener(plugin));
        registerListener(new KitOptionsListener(plugin));
        registerListener(new LingerPotionListener(plugin));
        
        String timeString = CC.getTimeDifferenceAndColor(start, System.currentTimeMillis());
        DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.startup.listeners-success", "time", timeString)));
    }

    public void registerListener(Listener listener) {
        Objects.requireNonNull(listener, "listener");
        registeredListeners.add(listener);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public void registerListenerWithTiming(Listener listener) {
        long start = System.currentTimeMillis();
        DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.startup.registering-post-listeners"));
        
        registerListener(listener);
        
        String timeString = CC.getTimeDifferenceAndColor(start, System.currentTimeMillis());
        DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.startup.post-listeners-success", "time", timeString)));
    }

    public void unregisterAllListeners() {
        // Unregister managed listeners
        registeredListeners.forEach(HandlerList::unregisterAll);
        registeredListeners.clear();
        
        // Also unregister any listeners registered directly against the plugin
        if (plugin != null) {
            HandlerList.unregisterAll(plugin);
        }
    }
}