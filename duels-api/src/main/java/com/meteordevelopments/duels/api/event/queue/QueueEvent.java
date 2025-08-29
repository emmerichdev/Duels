package com.meteordevelopments.duels.api.event.queue;

import com.meteordevelopments.duels.api.event.SourcedEvent;
import com.meteordevelopments.duels.api.queue.DQueue;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class QueueEvent extends SourcedEvent {

    private final DQueue queue;

    QueueEvent(@Nullable final CommandSender source, @NotNull final DQueue queue) {
        super(source);
        Objects.requireNonNull(queue, "queue");
        this.queue = queue;
    }

    @NotNull
    public DQueue getQueue() {
        return queue;
    }
}
