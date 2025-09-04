package com.emmerichbrowne.duels.api.event.queue;

import com.emmerichbrowne.duels.api.queue.DQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class QueueLeaveEvent extends QueueEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player source;

    public QueueLeaveEvent(@NotNull final Player source, @NotNull final DQueue queue) {
        super(source, queue);
        Objects.requireNonNull(source, "source");
        this.source = source;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public Player getSource() {
        return source;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
