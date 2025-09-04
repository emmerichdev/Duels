package com.emmerichbrowne.duels.api.event.request;

import com.emmerichbrowne.duels.api.event.SourcedEvent;
import com.emmerichbrowne.duels.api.request.Request;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class RequestEvent extends SourcedEvent {

    private final Player source, target;
    private final Request request;

    RequestEvent(@NotNull final Player source, @NotNull final Player target, @NotNull final Request request) {
        super(source);
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(request, "request");
        this.source = source;
        this.target = target;
        this.request = request;
    }

    @NotNull
    @Override
    public Player getSource() {
        return source;
    }

    @NotNull
    public Player getTarget() {
        return target;
    }

    @NotNull
    public Request getRequest() {
        return request;
    }
}
