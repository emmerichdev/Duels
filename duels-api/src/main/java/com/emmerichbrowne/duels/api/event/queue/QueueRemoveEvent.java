package com.emmerichbrowne.duels.api.event.queue;

import com.emmerichbrowne.duels.api.queue.DQueue;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QueueRemoveEvent extends QueueEvent {

    private static final HandlerList handlers = new HandlerList();

    public QueueRemoveEvent(@Nullable final CommandSender source, @NotNull final DQueue queue) {
        super(source, queue);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
