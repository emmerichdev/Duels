package com.emmerichbrowne.duels.api.event.kit;

import com.emmerichbrowne.duels.api.kit.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KitRemoveEvent extends KitEvent {

    private static final HandlerList handlers = new HandlerList();

    public KitRemoveEvent(@Nullable final CommandSender source, @NotNull final Kit kit) {
        super(source, kit);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
