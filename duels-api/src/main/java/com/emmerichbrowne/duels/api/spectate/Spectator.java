package com.emmerichbrowne.duels.api.spectate;

import com.emmerichbrowne.duels.api.arena.Arena;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Spectator {

    @NotNull
    UUID getUuid();

    @Nullable
    Player getPlayer();

    @NotNull
    UUID getTargetUuid();

    @Nullable
    Player getTarget();

    @NotNull
    Arena getArena();
}
