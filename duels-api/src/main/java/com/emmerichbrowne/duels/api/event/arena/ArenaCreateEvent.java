package com.emmerichbrowne.duels.api.event.arena;

import com.emmerichbrowne.duels.api.arena.Arena;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArenaCreateEvent extends ArenaEvent {

    private static final HandlerList handlers = new HandlerList();

    public ArenaCreateEvent(@Nullable final CommandSender source, @NotNull final Arena arena) {
        super(source, arena);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
