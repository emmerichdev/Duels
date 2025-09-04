package com.emmerichbrowne.duels.slm;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.api.world.SlimeWorldInstance;
import com.infernalsuite.asp.api.world.properties.SlimePropertyMap;
import com.infernalsuite.asp.loaders.file.FileLoader;
import com.emmerichbrowne.duels.DuelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SlimeManager {
    private final DuelsPlugin plugin;
    private AdvancedSlimePaperAPI asp;
    private FileLoader loader;
    private File arenasDir;

    public SlimeManager(DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        this.asp = AdvancedSlimePaperAPI.instance();
        this.arenasDir = new File(plugin.getDataFolder(), "slime_arenas");
        if (!arenasDir.exists()) {
            if (!arenasDir.mkdirs()) {
                plugin.getLogger().log(Level.SEVERE, "Could not create slime_arenas directory.");
            }
        }
        this.loader = new FileLoader(arenasDir);
    }

    public CompletableFuture<World> createMatchWorld(String templateName) {
        return CompletableFuture.supplyAsync(() -> {
            String matchWorldName = "duel-" + UUID.randomUUID();
            File templateFile = new File(arenasDir, templateName + ".slime");
            File matchFile = new File(arenasDir, matchWorldName + ".slime");

            if (!templateFile.exists()) {
                plugin.getLogger().severe("Arena template not found: " + templateName);
                return null;
            }

            try {
                Files.copy(templateFile.toPath(), matchFile.toPath());
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to copy arena template: " + templateName, e);
                return null;
            }

            try {
                SlimeWorld slimeWorld = asp.readWorld(loader, matchWorldName, false, new SlimePropertyMap());

                CompletableFuture<World> worldFuture = new CompletableFuture<>();
                DuelsPlugin.getMorePaperLib().scheduling().globalRegionalScheduler().run(() -> {
                    try {
                        SlimeWorldInstance worldInstance = asp.loadWorld(slimeWorld, true);
                        worldFuture.complete(worldInstance.getBukkitWorld());
                    } catch (Exception e) {
                        worldFuture.completeExceptionally(e);
                    }
                });
                return worldFuture.join();

            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load slime world " + matchWorldName, e);
                return null;
            }
        });
    }

    public void unloadMatchWorld(World world) {
        if (world == null) return;

        DuelsPlugin.getMorePaperLib().scheduling().globalRegionalScheduler().run(() -> {
            Bukkit.unloadWorld(world, false);

            DuelsPlugin.getMorePaperLib().scheduling().asyncScheduler().run(() -> {
                File matchFile = new File(arenasDir, world.getName() + ".slime");
                if (matchFile.exists()) {
                    try {
                        Files.delete(matchFile.toPath());
                    } catch (IOException e) {
                        plugin.getLogger().log(Level.SEVERE, "Failed to delete slime world file: " + matchFile.getName(), e);
                    }
                }
            });
        });
    }
}
