package com.emmerichbrowne.duels.scanner;

import co.aikar.commands.PaperCommandManager;
import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.commands.BaseCommand;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Set;

public class AutoRegistrationScanner {
    
    private final DuelsPlugin plugin;
    private final PaperCommandManager commandManager;
    
    public AutoRegistrationScanner(DuelsPlugin plugin, PaperCommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }
    
    public void scanAndRegisterAll() {
        scanAndRegisterCommands();
        scanAndRegisterListeners();
    }
    
    public void scanAndRegisterCommands() {
        Reflections reflections = new Reflections("com.emmerichbrowne.duels.commands");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoRegister.class);
        
        plugin.getLogger().info("Found " + annotatedClasses.size() + " commands with @AutoRegister annotation");
        
        for (Class<?> clazz : annotatedClasses) {
            try {
                registerCommand(clazz);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to register command " + clazz.getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public void scanAndRegisterListeners() {
        Reflections reflections = new Reflections("com.emmerichbrowne.duels.listeners");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoRegister.class);
        
        plugin.getLogger().info("Found " + annotatedClasses.size() + " listeners with @AutoRegister annotation");
        
        for (Class<?> clazz : annotatedClasses) {
            try {
                registerListener(clazz);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to register listener " + clazz.getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void registerCommand(Class<?> clazz) throws Exception {
        if (!BaseCommand.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Command " + clazz.getSimpleName() + " must extend BaseCommand");
        }
        
        Constructor<?> constructor = clazz.getConstructor(DuelsPlugin.class);
        BaseCommand command = (BaseCommand) constructor.newInstance(plugin);
        
        commandManager.registerCommand(command);
        plugin.getLogger().info("Auto-registered command: " + clazz.getSimpleName());
    }
    
    private void registerListener(Class<?> clazz) throws Exception {
        if (!Listener.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Listener " + clazz.getSimpleName() + " must implement Listener");
        }
        
        Constructor<?> constructor = clazz.getConstructor(DuelsPlugin.class);
        Listener listener = (Listener) constructor.newInstance(plugin);
        
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        plugin.getLogger().info("Auto-registered listener: " + clazz.getSimpleName());
    }
}