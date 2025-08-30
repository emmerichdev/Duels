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
        DuelsPlugin.sendMessage("&2Successfully completed startup in " + CC.getTimeDifferenceAndColor(start, end) + "&a.");

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
            DuelsPlugin.sendMessage("&cFailed to load DB.yml. Disabling plugin.");
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
            DuelsPlugin.sendMessage("&cFailed to connect to MongoDB. Disabling plugin.");
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
            DuelsPlugin.sendMessage("&eRedis connection failed, continuing without cross-server sync cache.");
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

        DuelsPlugin.sendMessage("&eLoading log manager...");
        try {
            plugin.initializeLogManager();
            DuelsPlugin.sendMessage("&dSuccessfully loaded Log Manager in &f[" + CC.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + "&f]");
            return true;
        } catch (Exception ex) {
            DuelsPlugin.sendMessage("&c&lCould not load LogManager. Please contact the developer.");
            LOGGER.log(Level.SEVERE, "Could not load LogManager. Please contact the developer.", ex);
            return false;
        }
    }

    private boolean checkBukkitCompatibility() {
        try {
            Class.forName("org.bukkit.Bukkit");
            return true;
        } catch (ClassNotFoundException ex) {
            DuelsPlugin.sendMessage("&c&l================= *** DUELS LOAD FAILURE *** =================");
            DuelsPlugin.sendMessage("&c&lDuels requires a Paper-compatible server!");
            DuelsPlugin.sendMessage("&c&lFor Paper installation, follow this guide: " + PAPER_INSTALLATION_URL);
            DuelsPlugin.sendMessage("&c&lOther compatible servers include Purpur and other Paper forks.");
            DuelsPlugin.sendMessage("&c&l================= *** DUELS LOAD FAILURE *** =================");
            return false;
        }
    }
}