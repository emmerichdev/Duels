package com.meteordevelopments.duels.api.event.arena;

import com.meteordevelopments.duels.api.arena.Arena;
import com.meteordevelopments.duels.api.event.SourcedEvent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ArenaEvent extends SourcedEvent {

    private final Arena arena;

    ArenaEvent(@Nullable final CommandSender source, @NotNull final Arena arena) {
        super(source);
        Objects.requireNonNull(arena, "arena");
        this.arena = arena;
    }

    @NotNull
    public Arena getArena() {
        return arena;
    }
}
