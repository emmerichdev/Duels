package com.emmerichbrowne.duels.api.queue;

import com.emmerichbrowne.duels.api.kit.Kit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DQueue {

    @Nullable
    Kit getKit();


    int getBet();


    boolean isInQueue(@NotNull final Player player);


    @NotNull
    List<Player> getQueuedPlayers();

    @NotNull
    long getPlayersInMatch();

    boolean isRemoved();
}
