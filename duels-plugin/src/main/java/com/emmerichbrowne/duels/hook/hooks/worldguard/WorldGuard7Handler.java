package com.emmerichbrowne.duels.hook.hooks.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.logging.Logger;

public class WorldGuard7Handler implements WorldGuardHandler {

    @Override
    public String findRegion(final Player player, final Collection<String> regions) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (regions == null) {
            throw new IllegalArgumentException("Regions collection cannot be null");
        }
        if (regions.isEmpty()) {
            return null;
        }
        try {
            final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            final RegionManager manager = container.get(BukkitAdapter.adapt(player.getWorld()));
            if (manager == null) {
                return null;
            }
            final BlockVector3 vec = BlockVector3.at(
                    player.getLocation().getBlockX(),
                    player.getLocation().getBlockY(),
                    player.getLocation().getBlockZ()
            );
            for (ProtectedRegion region : manager.getApplicableRegions(vec)) {
                if (regions.contains(region.getId())) {
                    return region.getId();
                }
            }
            return null;
        } catch (Exception ex) {
            Logger.getLogger(WorldGuard7Handler.class.getName()).warning("Failed WorldGuard region check for " + player.getName() + ": " + ex.getMessage());
            return null;
        }
    }
}


