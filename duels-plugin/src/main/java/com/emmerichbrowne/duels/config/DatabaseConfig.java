package com.emmerichbrowne.duels.config;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.util.config.AbstractConfiguration;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseConfig extends AbstractConfiguration<DuelsPlugin> {

    @Getter private String mongoUri;
    @Getter private String mongoDatabase;


    public DatabaseConfig(final DuelsPlugin plugin) {
        super(plugin, "DB");
    }

    @Override
    protected void loadValues(FileConfiguration configuration) {
        mongoUri = configuration.getString("mongo.uri", "mongodb://localhost:27017");
        mongoDatabase = configuration.getString("mongo.database", "duels");
    }
}

