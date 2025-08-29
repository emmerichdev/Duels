package com.meteordevelopments.duels.api.event.queue;

import com.meteordevelopments.duels.api.kit.Kit;
import com.meteordevelopments.duels.api.queue.DQueue;
import com.meteordevelopments.duels.api.queue.DQueueManager;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QueueCreateEvent extends QueueEvent {

    private static final HandlerList handlers = new HandlerList();

    public QueueCreateEvent(@Nullable final CommandSender source, @NotNull final DQueue queue) {
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
