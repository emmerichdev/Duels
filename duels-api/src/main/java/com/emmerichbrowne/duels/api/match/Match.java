package com.emmerichbrowne.duels.api.match;

import java.util.List;
import java.util.Set;

import com.emmerichbrowne.duels.api.arena.Arena;
import com.emmerichbrowne.duels.api.kit.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Match {


    @NotNull
    Arena getArena();


    long getStart();


    @Nullable
    Kit getKit();


    @NotNull
    List<ItemStack> getItems(@NotNull final Player player);


    int getBet();


    boolean isFinished();


    @NotNull
    Set<Player> getPlayers();


    @NotNull
    Set<Player> getStartingPlayers();
}
