package com.meteordevelopments.duels.api.event.spectate;

import com.meteordevelopments.duels.api.event.SourcedEvent;
import com.meteordevelopments.duels.api.spectate.Spectator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class SpectateEvent extends SourcedEvent {

    private final Player source;
    private final Spectator spectator;

    SpectateEvent(@NotNull final Player source, @NotNull Spectator spectator) {
        super(source);
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(spectator, "spectator");
        this.source = source;
        this.spectator = spectator;
    }

    @NotNull
    @Override
    public Player getSource() {
        return source;
    }

    @NotNull
    public Spectator getSpectator() {
        return spectator;
    }
}
