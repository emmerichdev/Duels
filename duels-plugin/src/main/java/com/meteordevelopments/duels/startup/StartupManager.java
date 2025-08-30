package com.meteordevelopments.duels.startup;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.config.DatabaseConfig;
import com.meteordevelopments.duels.mongo.MongoService;
import com.meteordevelopments.duels.redis.RedisService;
import com.meteordevelopments.duels.util.CC;

import java.util.logging.Level;
import java.util.logging.Logger;

public record StartupManager(DuelsPlugin plugin) {

    private static final String PAPER_INSTALLATION_URL = "https://docs.papermc.io/paper/getting-started";
    private static final Logger LOGGER = Logger.getLogger("[Duels]");

    public boolean startup() {
        long start = System.currentTimeMillis();

        if (!initializeDatabase()) {
            return false;
        }

        if (!loadLogManager()) {
            return false;
        }

        if (!checkBukkitCompatibility()) {
            return false;
        }

        long end = System.currentTimeMillis();
        String timeString = CC.getTimeDifferenceAndColor(start, end);
        DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.startup.startup-complete", "time", timeString)));

        return true;
    }

    private boolean initializeDatabase() {
        // Load DB.yml
        try {
            DatabaseConfig databaseConfig = new DatabaseConfig(plugin);
            databaseConfig.handleLoad();
            plugin.setDatabaseConfig(databaseConfig);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to load database configuration (DB.yml)", ex);
            DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.database.db-config-failed")));
            plugin.setDatabaseConfig(null); // Clear any stale config
            return false;
        }

        // Initialize MongoDB (required)
        MongoService mongoService = new MongoService(plugin);
        try {
            mongoService.connect();
            plugin.setMongoService(mongoService);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to connect to MongoDB", ex);
            DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.database.mongodb-connect-failed")));
            // Clean up any partially initialized MongoDB resources
            try {
                mongoService.close();
            } catch (Exception closeEx) {
                LOGGER.log(Level.WARNING, "Failed to clean up MongoDB service during initialization failure", closeEx);
            }
            return false;
        }

        // Initialize Redis (optional)
        RedisService redisService = new RedisService(plugin);
        try {
            redisService.connect();
            plugin.setRedisService(redisService);
        } catch (Exception ex) {
            DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.database.redis-connect-failed")));
            LOGGER.log(Level.WARNING, "Redis connection failed; continuing without Redis.", ex);
            // Clean up any partially initialized Redis resources
            try {
                redisService.close();
            } catch (Exception closeEx) {
                LOGGER.log(Level.WARNING, "Failed to clean up Redis service during initialization failure", closeEx);
            }
            plugin.setRedisService(null);
        }

        return true;
    }

    private boolean loadLogManager() {
        long start = System.currentTimeMillis();

        DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.startup.loading-log-manager"));
        try {
            plugin.initializeLogManager();
            String timeString = CC.getTimeDifferenceAndColor(start, System.currentTimeMillis());
            DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.startup.log-manager-success", "time", timeString)));
            return true;
        } catch (Exception ex) {
            DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.errors.log-manager-failed")));
            LOGGER.log(Level.SEVERE, "Could not load LogManager. Please contact the developer.", ex);
            return false;
        }
    }

    private boolean checkBukkitCompatibility() {
        try {
            Class.forName("org.bukkit.Bukkit");
            return true;
        } catch (ClassNotFoundException ex) {
            DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.compatibility.paper-required-header")));
            DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.compatibility.paper-required-message")));
            DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.compatibility.paper-installation-guide", "url", PAPER_INSTALLATION_URL)));
            DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.compatibility.paper-compatible-servers")));
            DuelsPlugin.sendMessage(CC.translateConsole(plugin.getLang().getMessage("SYSTEM.compatibility.paper-required-footer")));
            return false;
        }
    }
}