package com.emmerichbrowne.duels.api.event.arena;

import com.emmerichbrowne.duels.api.arena.Arena;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArenaStateChangeEvent extends ArenaEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean disabled;
    private boolean cancelled;

    public ArenaStateChangeEvent(@Nullable final CommandSender source, @NotNull final Arena arena, final boolean disabled) {
        super(source, arena);
        this.disabled = disabled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
