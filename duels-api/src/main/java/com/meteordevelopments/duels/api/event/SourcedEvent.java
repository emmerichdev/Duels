package com.meteordevelopments.duels.api.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public abstract class SourcedEvent extends Event {

    private final CommandSender source;

    protected SourcedEvent(@Nullable final CommandSender source) {
        this.source = source;
    }

    @Nullable
    public CommandSender getSource() {
        return source;
    }


    public boolean hasSource() {
        return getSource() != null;
    }
}
