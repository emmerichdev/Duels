package com.emmerichbrowne.duels.api.event.arena;

import com.emmerichbrowne.duels.api.arena.Arena;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ArenaSetPositionEvent extends ArenaEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private int pos;
    private Location location;
    private boolean cancelled;

    public ArenaSetPositionEvent(@Nullable final CommandSender source, @NotNull final Arena arena, final int pos, @NotNull final Location location) {
        super(source, arena);
        Objects.requireNonNull(location, "location");
        this.pos = pos;
        this.location = location;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(final int pos) {
        this.pos = pos;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
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
