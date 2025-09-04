package com.emmerichbrowne.duels.api.spectate;

import com.emmerichbrowne.duels.api.arena.Arena;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SpectateManager {

    @Nullable
    Spectator get(@NotNull final Player player);


    boolean isSpectating(@NotNull final Player player);


    @NotNull
    Result startSpectating(@NotNull final Player player, @NotNull final Player target);


    void stopSpectating(@NotNull final Player player);


    @NotNull
    List<Spectator> getSpectators(@NotNull final Arena arena);


    enum Result {

        ALREADY_SPECTATING,
        IN_QUEUE,
        IN_MATCH,
        TARGET_NOT_IN_MATCH,
        EVENT_CANCELLED,
        SUCCESS

    }
}
