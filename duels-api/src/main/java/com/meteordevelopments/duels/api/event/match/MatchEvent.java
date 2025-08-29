package com.meteordevelopments.duels.api.event.match;

import com.meteordevelopments.duels.api.match.Match;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class MatchEvent extends Event {

    private final Match match;

    MatchEvent(@NotNull final Match match) {
        Objects.requireNonNull(match, "match");
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }
}
