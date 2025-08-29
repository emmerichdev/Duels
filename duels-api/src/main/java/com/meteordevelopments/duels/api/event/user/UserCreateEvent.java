package com.meteordevelopments.duels.api.event.user;

import com.meteordevelopments.duels.api.user.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UserCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final User user;

    public UserCreateEvent(@NotNull final User user) {
        Objects.requireNonNull(user, "user");
        this.user = user;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
