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
        DuelsPlugin.sendMessage("&eRegistering listeners...");
        
        registerListener(new KitItemListener(plugin));
        registerListener(new DamageListener(plugin));
        registerListener(new PotionListener(plugin));
        registerListener(new TeleportListener(plugin));
        registerListener(new ProjectileHitListener(plugin));
        registerListener(new EnderpearlListener(plugin));
        registerListener(new KitOptionsListener(plugin));
        registerListener(new LingerPotionListener(plugin));
        
        DuelsPlugin.sendMessage("&dSuccessfully registered listeners in &f[" + CC.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + "&f]");
    }

    public void registerListener(Listener listener) {
        Objects.requireNonNull(listener, "listener");
        registeredListeners.add(listener);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public void registerListenerWithTiming(Listener listener) {
        long start = System.currentTimeMillis();
        DuelsPlugin.sendMessage("&eRegistering post listeners...");
        
        registerListener(listener);
        
        DuelsPlugin.sendMessage("&dSuccessfully registered listeners after plugin startup in [" + CC.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + "&f]");
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