package com.emmerichbrowne.duels.startup;

import com.google.common.collect.Lists;
import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaManagerImpl;
import com.emmerichbrowne.duels.betting.BettingManager;
import com.emmerichbrowne.duels.data.UserManagerImpl;
import com.emmerichbrowne.duels.duel.DuelManager;
import com.emmerichbrowne.duels.hook.HookManager;
import com.emmerichbrowne.duels.inventories.InventoryManager;
import com.emmerichbrowne.duels.kit.KitManagerImpl;
import com.emmerichbrowne.duels.leaderboard.manager.LeaderboardManager;
import com.emmerichbrowne.duels.logging.LogManager;
import com.emmerichbrowne.duels.party.PartyManagerImpl;
import com.emmerichbrowne.duels.player.PlayerInfoManager;
import com.emmerichbrowne.duels.queue.QueueManager;
import com.emmerichbrowne.duels.queue.sign.QueueSignManagerImpl;
import com.emmerichbrowne.duels.rank.manager.RankManager;
import com.emmerichbrowne.duels.request.RequestManager;
import com.emmerichbrowne.duels.setting.SettingsManager;
import com.emmerichbrowne.duels.spectate.SpectateManagerImpl;
import com.emmerichbrowne.duels.teleport.Teleport;
import com.emmerichbrowne.duels.util.CC;
import com.emmerichbrowne.duels.util.Loadable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LoadableManager {

    private final DuelsPlugin plugin;
    private final List<Loadable> loadables = new ArrayList<>();
    private int lastLoad = -1;
    
    public LoadableManager(DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean initializeLoadables() {
        long start = System.currentTimeMillis();
        if (plugin.getLang() != null) {
            DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.startup.initializing-components"));
        } else {
            plugin.getLogger().info("Initializing components...");
        }
        
        // Initialize remaining loadables (Config and Lang are already initialized)
        // Skip config and lang as they are initialized earlier in the startup process
        addLoadable("user manager", () -> {
            UserManagerImpl userManager = new UserManagerImpl(plugin);
            plugin.setUserManager(userManager);
            return userManager;
        });
        addLoadable("party manager", () -> {
            PartyManagerImpl partyManager = new PartyManagerImpl(plugin);
            plugin.setPartyManager(partyManager);
            return partyManager;
        });
        addLoadable("kit manager", () -> {
            KitManagerImpl kitManager = new KitManagerImpl(plugin);
            plugin.setKitManager(kitManager);
            return kitManager;
        });
        addLoadable("arena manager", () -> {
            ArenaManagerImpl arenaManager = new ArenaManagerImpl(plugin);
            plugin.setArenaManager(arenaManager);
            return arenaManager;
        });
        addLoadable("settings manager", () -> {
            SettingsManager settingsManager = new SettingsManager(plugin);
            plugin.setSettingManager(settingsManager);
            return settingsManager;
        });
        addLoadable("player manager", () -> {
            PlayerInfoManager playerManager = new PlayerInfoManager(plugin);
            plugin.setPlayerManager(playerManager);
            return playerManager;
        });
        addLoadable("spectate manager", () -> {
            SpectateManagerImpl spectateManager = new SpectateManagerImpl(plugin);
            plugin.setSpectateManager(spectateManager);
            return spectateManager;
        });
        addLoadable("betting manager", () -> {
            BettingManager bettingManager = new BettingManager(plugin);
            plugin.setBettingManager(bettingManager);
            return bettingManager;
        });
        addLoadable("inventory manager", () -> {
            InventoryManager inventoryManager = new InventoryManager(plugin);
            plugin.setInventoryManager(inventoryManager);
            return inventoryManager;
        });
        addLoadable("duel manager", () -> {
            DuelManager duelManager = new DuelManager(plugin);
            plugin.setDuelManager(duelManager);
            return duelManager;
        });
        addLoadable("queue manager", () -> {
            QueueManager queueManager = new QueueManager(plugin);
            plugin.setQueueManager(queueManager);
            return queueManager;
        });
        addLoadable("queue signs", () -> {
            QueueSignManagerImpl queueSignManager = new QueueSignManagerImpl(plugin);
            plugin.setQueueSignManager(queueSignManager);
            return queueSignManager;
        });
        addLoadable("request manager", () -> {
            RequestManager requestManager = new RequestManager(plugin);
            plugin.setRequestManager(requestManager);
            return requestManager;
        });
        addLoadable("leaderboard manager", () -> {
            LeaderboardManager leaderboardManager = new LeaderboardManager(plugin);
            plugin.setLeaderboardManager(leaderboardManager);
            return leaderboardManager;
        });
        addLoadable("rank manager", () -> {
            RankManager rankManager = new RankManager(plugin);
            plugin.setRankManager(rankManager);
            return rankManager;
        });
        addLoadable("teleport manager", () -> {
            Teleport teleport = new Teleport(plugin);
            plugin.setTeleport(teleport);
            return teleport;
        });
        
        // Hook manager is not a Loadable, so handle it separately
        try {
            HookManager hookManager = new HookManager(plugin);
            plugin.setHookManager(hookManager);
        } catch (Exception e) {
            if (plugin.getLang() != null) {
                DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.errors.hook-manager-failed", "error", e.getMessage()));
            } else {
                plugin.getLogger().severe("Failed to initialize hook manager: " + e.getMessage());
            }
            throw new RuntimeException("Failed to initialize hook manager", e);
        }

        if (!loadAll()) {
            return false;
        }

        String timeString = CC.getTimeDifferenceAndColor(start, System.currentTimeMillis());
        if (plugin.getLang() != null) {
            DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.startup.components-success", "time", timeString));
        } else {
            plugin.getLogger().info("Components initialized successfully in " + timeString);
        }
        return true;
    }

    public boolean loadAll() {
        for (final Loadable loadable : loadables) {
            final String name = loadable.getClass().getSimpleName();

            try {
                final long now = System.currentTimeMillis();
                plugin.getLogManager().debug("Starting load of " + name + " at " + now);
                loadable.handleLoad();
                plugin.getLogManager().debug(name + " has been loaded. (took " + (System.currentTimeMillis() - now) + "ms)");
                lastLoad = loadables.indexOf(loadable);
            } catch (Exception ex) {
                Logger.getLogger(LoadableManager.class.getName()).log(Level.SEVERE, null, ex);

                if (loadable instanceof LogManager) {
                    Logger.getLogger(LoadableManager.class.getName()).log(Level.SEVERE, "Error loading LogManager", ex);
                }

                if (plugin.getLang() != null) {
                    DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.errors.load-failure", "name", name));
                } else {
                    plugin.getLogger().severe("Failed to load " + name + ": " + ex.getMessage());
                }
                return false;
            }
        }
        return true;
    }

    public void unloadAll() {
        for (final Loadable loadable : Lists.reverse(loadables)) {
            assert loadable != null;
            final String name = loadable.getClass().getSimpleName();

            try {
                if (loadables.indexOf(loadable) > lastLoad) {
                    continue;
                }

                final long now = System.currentTimeMillis();
                plugin.getLogManager().debug("Starting unload of " + name + " at " + now);
                loadable.handleUnload();
                plugin.getLogManager().debug(name + " has been unloaded. (took " + (System.currentTimeMillis() - now) + "ms)");
            } catch (Exception ex) {
                if (plugin.getLang() != null) {
                    DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.errors.unload-failure", "name", name));
                } else {
                    plugin.getLogger().severe("Failed to unload " + name + ": " + ex.getMessage());
                }
                // Log the error but continue with shutdown - don't halt the process
            }
        }
    }

    public Loadable find(final String name) {
        return loadables.stream()
                .filter(loadable -> loadable.getClass().getSimpleName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }


    private void addLoadable(String name, Supplier<Loadable> supplier) {
        try {
            Loadable loadable = supplier.get();
            loadables.add(loadable);
        } catch (Exception e) {
            if (plugin.getLang() != null) {
                DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.errors.initialization-failed", "name", name, "error", e.getMessage()));
            } else {
                plugin.getLogger().severe("Failed to initialize " + name + ": " + e.getMessage());
            }
            throw new RuntimeException("Failed to initialize " + name, e);
        }
    }
}
