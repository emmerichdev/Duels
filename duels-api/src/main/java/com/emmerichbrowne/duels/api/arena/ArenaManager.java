package com.emmerichbrowne.duels.api.arena;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ArenaManager {

    @Nullable
    Arena get(@NotNull final String name);


    @Nullable
    Arena get(@NotNull final Player player);


    boolean isInMatch(@NotNull final Player player);


    @NotNull
    List<Arena> getArenas();
}
