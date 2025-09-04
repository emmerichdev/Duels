package com.emmerichbrowne.duels.api.event.request;

import com.emmerichbrowne.duels.api.request.Request;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RequestDenyEvent extends RequestEvent {

    private static final HandlerList handlers = new HandlerList();

    public RequestDenyEvent(@NotNull final Player source, @NotNull final Player target, @NotNull final Request request) {
        super(source, target, request);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
