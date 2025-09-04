package com.meteordevelopments.duels;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.meteordevelopments.duels.api.Duels;
import com.meteordevelopments.duels.api.command.SubCommand;
import com.meteordevelopments.duels.arena.ArenaImpl;
import com.meteordevelopments.duels.arena.ArenaManagerImpl;
import com.meteordevelopments.duels.betting.BettingManager;
import com.meteordevelopments.duels.commands.*;
import com.meteordevelopments.duels.config.Config;
import com.meteordevelopments.duels.config.DatabaseConfig;
import com.meteordevelopments.duels.config.Lang;
import com.meteordevelopments.duels.data.ItemData;
import com.meteordevelopments.duels.data.ItemData.ItemDataDeserializer;
import com.meteordevelopments.duels.data.UserData;
import com.meteordevelopments.duels.data.UserManagerImpl;
import com.meteordevelopments.duels.duel.DuelManager;
import com.meteordevelopments.duels.hook.HookManager;
import com.meteordevelopments.duels.hook.hooks.DeluxeCombatHook;
import com.meteordevelopments.duels.hook.hooks.worldguard.WorldGuardHook;
import com.meteordevelopments.duels.inventories.InventoryManager;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.kit.KitManagerImpl;
import com.meteordevelopments.duels.leaderboard.manager.LeaderboardManager;
import com.meteordevelopments.duels.logging.LogManager;
import com.meteordevelopments.duels.mongo.MongoService;
import com.meteordevelopments.duels.party.PartyManagerImpl;
import com.meteordevelopments.duels.player.PlayerInfoManager;
import com.meteordevelopments.duels.queue.QueueManager;
import com.meteordevelopments.duels.queue.sign.QueueSignManagerImpl;
import com.meteordevelopments.duels.rank.manager.RankManager;
import com.meteordevelopments.duels.redis.RedisService;
import com.meteordevelopments.duels.request.RequestManager;
import com.meteordevelopments.duels.setting.SettingsManager;
import com.meteordevelopments.duels.slm.SlimeManager;
import com.meteordevelopments.duels.spectate.SpectateManagerImpl;
import com.meteordevelopments.duels.startup.ListenerManager;
import com.meteordevelopments.duels.startup.LoadableManager;
import com.meteordevelopments.duels.startup.StartupManager;
import com.meteordevelopments.duels.teleport.Teleport;
import com.meteordevelopments.duels.util.CC;
import com.meteordevelopments.duels.util.Loadable;
import com.meteordevelopments.duels.util.Log;
import com.meteordevelopments.duels.util.Log.LogSource;
import com.meteordevelopments.duels.util.gui.GuiListener;
import com.meteordevelopments.duels.util.inventory.InventoryUtil;
import com.meteordevelopments.duels.util.json.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.JedisPubSub;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.scheduling.ScheduledTask;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static com.meteordevelopments.duels.redis.RedisService.*;


public class DuelsPlugin extends JavaPlugin implements Duels, LogSource {
    
    @Getter
    private static DuelsPlugin instance;
    @Getter
    private static MorePaperLib morePaperLib;

    private LoadableManager loadableManager;
    private ListenerManager listenerManager;
    private PaperCommandManager commandManager;
    // Plugin components
    @Getter @Setter private LogManager logManager;
    @Getter @Setter private Config configuration;
    @Getter @Setter private Lang lang;
    @Getter @Setter private GuiListener<DuelsPlugin> guiListener;
    @Getter @Setter private UserManagerImpl userManager;
    @Getter @Setter private KitManagerImpl kitManager;
    @Getter @Setter private ArenaManagerImpl arenaManager;
    @Getter @Setter private SettingsManager settingManager;
    @Getter @Setter private PlayerInfoManager playerManager;
    @Getter @Setter private SpectateManagerImpl spectateManager;
    @Getter @Setter private BettingManager bettingManager;
    @Getter @Setter private InventoryManager inventoryManager;
    @Getter @Setter private DuelManager duelManager;
    @Getter @Setter private QueueManager queueManager;
    @Getter @Setter private QueueSignManagerImpl queueSignManager;
    @Getter @Setter private RequestManager requestManager;
    @Getter @Setter private HookManager hookManager;
    @Getter @Setter private Teleport teleport;
    @Getter @Setter private PartyManagerImpl partyManager;
    
    @Getter @Setter private LeaderboardManager leaderboardManager;
    @Getter @Setter private RankManager rankManager;
    @Getter @Setter private MongoService mongoService;
    @Getter @Setter private RedisService redisService;
    @Getter @Setter private DatabaseConfig databaseConfig;
    @Getter @Setter private SlimeManager slimeManager;
    private JedisPubSub redisSubscriber;

    @Override
    public void onEnable() {
        instance = this;
        morePaperLib = new MorePaperLib(this);
        Log.addSource(this);
        JsonUtil.registerDeserializer(ItemData.class, ItemDataDeserializer.class);
        
        // Initialize basic configurations first (Config and Lang are needed for startup messages)
        if (!initializeBasicConfigurations()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize LogManager early - MUST be done before LoadableManager and other startup components
        if (!initializeLogManagerEarly()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize managers (LogManager is now available for use)
        StartupManager startupManager = new StartupManager(this);
        loadableManager = new LoadableManager(this);
        listenerManager = new ListenerManager(this);
        
        slimeManager = new SlimeManager(this);
        slimeManager.init();
        
        // Handle startup
        if (!startupManager.startup()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize and load remaining components
        if (!loadableManager.initializeLoadables()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Register commands and listeners
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("brigadier");

        commandManager.getCommandContexts().registerContext(ArenaImpl.class, c -> {
            String name = c.popFirstArg();
            ArenaImpl arena = arenaManager.get(name);
            if (arena == null) {
                throw new InvalidCommandArgument("Arena '" + name + "' not found.");
            }
            return arena;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("@arenas", c -> arenaManager.getNames());

        commandManager.getCommandContexts().registerContext(KitImpl.class, c -> {
            String name = c.popFirstArg();
            KitImpl kit = kitManager.get(name);
            if (kit == null) {
                throw new InvalidCommandArgument("Kit '" + name + "' not found.");
            }
            return kit;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("@kits", c -> kitManager.getNames(false));

        commandManager.getCommandContexts().registerContext(UserData.class, c -> {
            String name = c.popFirstArg();
            UserData user = userManager.get(name);
            if (user == null) {
                throw new InvalidCommandArgument("User '" + name + "' not found.");
            }
            return user;
        });

        registerCommandConditions();

        commandManager.registerCommand(new DuelsCommand(this));
        commandManager.registerCommand(new DuelCommand(this));
        commandManager.registerCommand(new PartyCommand(this));
        commandManager.registerCommand(new QueueCommand(this));
        commandManager.registerCommand(new SpectateCommand(this));
        commandManager.registerCommand(new RankCommand(this));
        commandManager.registerCommand(new TestArenaCommand(this));
        commandManager.registerCommand(new ArenaCommand(this));
        commandManager.registerCommand(new KitCommand(this));
        commandManager.registerCommand(new QueueAdminCommand(this));
        commandManager.registerCommand(new StatsCommand(this));

        listenerManager.registerPreListeners();
        
        // Setup Redis subscriptions after managers are loaded
        if (redisService != null) {
            setupRedisSubscriptions();
        }
        
        // Update system removed in this fork
    }

    @Override
    public void onDisable() {
        final long start = System.currentTimeMillis();
        long last = start;
        
        if (logManager != null) {
            logManager.debug("onDisable start -> " + start + "\n");
        }
        
        // Unload components
        if (loadableManager != null) {
            loadableManager.unloadAll();
        }
        
        // Unregister listeners
        if (listenerManager != null) {
            listenerManager.unregisterAllListeners();
        }
        
        if (logManager != null) {
            logManager.debug("unload done (took " + Math.abs(last - (last = System.currentTimeMillis())) + "ms)");
        }
        
        Log.clearSources();
        
        if (logManager != null) {
            logManager.debug("Log#clearSources done (took " + Math.abs(last - System.currentTimeMillis()) + "ms)");
            logManager.handleDisable();
        }
        
        // Close database connections
        if (mongoService != null) {
            mongoService.close();
        }
        if (redisService != null) {
            try {
                if (redisSubscriber != null) {
                    redisSubscriber.unsubscribe();
                }
            } catch (Exception ignored) {}
            redisService.close();
            redisSubscriber = null;
        }
        
        instance = null;
        sendMessage("&2Disable process took " + (System.currentTimeMillis() - start) + "ms.");
    }

    @Override
    public boolean registerSubCommand(@NotNull final String command, @NotNull final SubCommand subCommand) {
        return false;
    }

    @Override
    public void registerListener(@NotNull final Listener listener) {
        listenerManager.registerListenerWithTiming(listener);
    }

    @Override
    public String getVersion() {
        return getPluginMeta().getVersion();
    }

    @Override
    public ScheduledTask doSync(@NotNull final Runnable task) {
        Objects.requireNonNull(task, "task");
        return morePaperLib.scheduling().globalRegionalScheduler().run(task);
    }

    @Override
    public ScheduledTask doSyncAfter(@NotNull final Runnable task, final long delay) {
        Objects.requireNonNull(task, "task");
        return morePaperLib.scheduling().globalRegionalScheduler().runDelayed(task, delay);
    }

    @Override
    public ScheduledTask doSyncRepeat(@NotNull final Runnable task, final long delay, final long period) {
        Objects.requireNonNull(task, "task");
        return morePaperLib.scheduling().globalRegionalScheduler().runAtFixedRate(task, delay, period);
    }

    @Override
    public void cancelTask(final @NotNull ScheduledTask task) {
        task.cancel();
    }

    @Override
    public ScheduledTask doAsync(@NotNull final Runnable task) {
        Objects.requireNonNull(task, "task");
        return morePaperLib.scheduling().asyncScheduler().run(task);
    }

    @Override
    public ScheduledTask doAsyncAfter(@NotNull final Runnable task, long delay) {
        Objects.requireNonNull(task, "task");
        return morePaperLib.scheduling().asyncScheduler().runDelayed(task, Duration.ofMillis(delay * 50L));
    }

    @Override
    public ScheduledTask doAsyncRepeat(@NotNull final Runnable task, long delay, long interval) {
        Objects.requireNonNull(task, "task");
        return morePaperLib.scheduling().asyncScheduler().runAtFixedRate(task, Duration.ofMillis(delay * 50L), Duration.ofMillis(interval * 50L));
    }

    @Override
    public void log(final Level level, final String s) {
        getLogger().log(level, s);
    }

    @Override
    public void log(final Level level, final String s, final Throwable thrown) {
        getLogger().log(level, s, thrown);
    }

    @Override
    public void info(@NotNull final String message) {
        getLogger().info(message);
    }

    @Override
    public void warn(@NotNull final String message) {
        getLogger().warning(message);
    }

    @Override
    public void error(@NotNull final String message) {
        getLogger().severe(message);
    }

    @Override
    public void error(@NotNull final String message, @NotNull final Throwable thrown) {
        getLogger().log(java.util.logging.Level.SEVERE, message, thrown);
    }

    public static String getPrefix() {
        return CC.translateConsole("&b&lDuels &7» ");
    }

    public static void sendMessage(String message) {
        final String prefix = getPrefix();
        if (message == null || message.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(prefix);
            return;
        }
        // Use appropriate console translation based on the format of the message
        if (message.indexOf('§') >= 0) {
            Bukkit.getConsoleSender().sendMessage(prefix + CC.translateConsoleFromSection(message));
        } else {
            Bukkit.getConsoleSender().sendMessage(prefix + CC.translateConsole(message));
        }
    }

    public void initializeLogManager() throws IOException {
        logManager = new LogManager(this);
        Log.addSource(logManager);
        logManager.debug("onEnable start -> " + System.currentTimeMillis() + "\n");
    }

    private boolean initializeLogManagerEarly() {
        try {
            initializeLogManager();
            return true;
        } catch (Exception ex) {
            getLogger().severe("Failed to initialize LogManager: " + ex.getMessage());
            return false;
        }
    }
    
    private boolean initializeBasicConfigurations() {
        try {
            // Initialize Config first
            Config config = new Config(this);
            config.handleLoad();
            setConfiguration(config);
            
            // Initialize Lang after Config (Lang depends on Config)
            Lang lang = new Lang(this);
            lang.handleLoad();
            setLang(lang);
            
            return true;
        } catch (Exception ex) {
            getLogger().log(java.util.logging.Level.SEVERE, "Failed to initialize basic configurations", ex);
            sendMessage("&cFailed to load basic configurations. Please check your config.yml and lang.yml files.");
            return false;
        }
    }

    // Convenience delegates used by commands
    public Loadable find(final String name) {
        return loadableManager != null ? loadableManager.find(name) : null;
    }

    private void registerCommandConditions() {
        commandManager.getCommandConditions().addCondition(Player.class, "not_in_creative", (context, execContext, player) -> {
            if (player.getGameMode() == GameMode.CREATIVE && getConfiguration().isPreventCreativeMode()) {
                throw new ConditionFailedException(getLang().getMessage("ERROR.duel.in-creative-mode"));
            }
        });

        commandManager.getCommandConditions().addCondition(Player.class, "inventory_empty", (context, execContext, player) -> {
            if (getConfiguration().isRequiresClearedInventory() && InventoryUtil.hasItem(player)) {
                throw new ConditionFailedException(getLang().getMessage("ERROR.duel.inventory-not-empty"));
            }
        });

        commandManager.getCommandConditions().addCondition(Player.class, "not_in_blacklisted_world", (context, execContext, player) -> {
            if (!getConfiguration().getBlacklistedWorlds().isEmpty() && getConfiguration().getBlacklistedWorlds().contains(player.getWorld().getName())) {
                throw new ConditionFailedException(getLang().getMessage("ERROR.duel.in-blacklisted-world"));
            }
        });

        commandManager.getCommandConditions().addCondition(Player.class, "not_combat_tagged", (context, execContext, player) -> {
            DeluxeCombatHook deluxeCombat = getHookManager().getHook(DeluxeCombatHook.class);
            if (deluxeCombat != null && getConfiguration().isDcPreventDuel() && deluxeCombat.isTagged(player)) {
                throw new ConditionFailedException(getLang().getMessage("ERROR.duel.is-tagged"));
            }
        });

        commandManager.getCommandConditions().addCondition(Player.class, "in_duel_zone", (context, execContext, player) -> {
            WorldGuardHook worldGuard = getHookManager().getHook(WorldGuardHook.class);
            if (getConfiguration().isDuelzoneEnabled() && worldGuard != null && worldGuard.findDuelZone(player) == null) {
                throw new ConditionFailedException(getLang().getMessage("ERROR.duel.not-in-duelzone", "regions", getConfiguration().getDuelzones()));
            }
        });

        commandManager.getCommandConditions().addCondition(Player.class, "not_in_match", (context, execContext, player) -> {
            if (getArenaManager().isInMatch(player)) {
                throw new ConditionFailedException(getLang().getMessage("ERROR.duel.already-in-match.sender"));
            }
        });

        commandManager.getCommandConditions().addCondition(Player.class, "not_spectating", (context, execContext, player) -> {
            if (getSpectateManager().isSpectating(player)) {
                throw new ConditionFailedException(getLang().getMessage("ERROR.duel.already-spectating.sender"));
            }
        });

        commandManager.getCommandConditions().addCondition(Player.class, "can_receive_requests", (context, execContext, player) -> {
            UserData user = getUserManager().get(player);
            if (user != null && !user.canRequest() && !execContext.getIssuer().getPlayer().hasPermission("duels.admin")) {
                throw new ConditionFailedException(getLang().getMessage("ERROR.duel.requests-disabled", "name", player.getName()));
            }
        });

        commandManager.getCommandConditions().addCondition(Player.class, "target_not_self", (context, execContext, player) -> {
            if (player.equals(execContext.getIssuer().getPlayer())) {
                throw new ConditionFailedException(getLang().getMessage("ERROR.duel.is-self"));
            }
        });
    }

    private void setupRedisSubscriptions() {
        try {
            // Ensure we don't stack multiple subscribers across reloads
            if (this.redisSubscriber != null) {
                try {
                    this.redisSubscriber.unsubscribe();
                } catch (Exception ignored) {}
                this.redisSubscriber = null;
            }
            final var sub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    doSync(() -> {
                        // Expect messages formatted as "serverId:payload"; ignore self-originated
                        String payload = message;
                        try {
                            final int idx = message.indexOf(':');
                            if (idx > 0) {
                                final String origin = message.substring(0, idx);
                                if (origin.equals(getSelfServerId())) {
                                    return;
                                }
                                payload = message.substring(idx + 1);
                            }
                        } catch (Exception ignored) {}
                        switch (channel) {
                            case CHANNEL_INVALIDATE_USER -> {
                                try {
                                    final UUID uuid = UUID.fromString(payload);
                                    if (userManager != null) userManager.reloadUser(uuid);
                                } catch (Exception ignored) {
                                }
                            }
                            case CHANNEL_INVALIDATE_KIT -> {
                                if (kitManager != null) kitManager.reloadKit(payload);
                            }
                            case CHANNEL_INVALIDATE_ARENA -> {
                                if (arenaManager != null) arenaManager.reloadArena(payload);
                            }
                        }
                    });
                }
            };
            this.redisSubscriber = sub;
            redisService.subscribe(sub,
                    CHANNEL_INVALIDATE_USER,
                    CHANNEL_INVALIDATE_KIT,
                    CHANNEL_INVALIDATE_ARENA
            );
        } catch (Exception ex) {
            sendMessage("&eFailed to subscribe to Redis channels; continuing without cross-server sync.");
        }
    }

    private String getSelfServerId() {
        final String configured = databaseConfig != null ? databaseConfig.getServerId() : null;
        if (configured != null && !configured.trim().isEmpty()) {
            return configured.trim();
        }
        final int port = getServer().getPort();
        return port > 0 ? String.valueOf(port) : "default";
    }
}