package com.meteordevelopments.duels.startup;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.config.DatabaseConfig;
import com.meteordevelopments.duels.mongo.MongoService;
import com.meteordevelopments.duels.redis.RedisService;
import com.meteordevelopments.duels.util.CC;

import java.util.logging.Level;
import java.util.logging.Logger;

public record StartupManager(DuelsPlugin plugin) {

    private static final Logger LOGGER = Logger.getLogger("[Duels]");

    public boolean startup() {
        long start = System.currentTimeMillis();

        if (!initializeDatabase()) {
            return false;
        }

        long end = System.currentTimeMillis();
        String timeString = CC.getTimeDifferenceAndColor(start, end);
        DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.startup.startup-complete", "time", timeString));

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
            DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.database.db-config-failed"));
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
            String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Unknown error";
            DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.database.mongodb-connect-failed", "error", errorMessage));
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
            DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.database.redis-connect-failed"));
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
}