package com.meteordevelopments.duels.api.event.queue;

import com.meteordevelopments.duels.api.queue.DQueue;
import com.meteordevelopments.duels.api.queue.DQueueManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class QueueJoinEvent extends QueueEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player source;
    private boolean cancelled;

    public QueueJoinEvent(@NotNull final Player source, @NotNull final DQueue queue) {
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
