package com.meteordevelopments.duels.api.event.match;

import com.meteordevelopments.duels.api.match.Match;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class MatchEndEvent extends MatchEvent {

    private static final HandlerList handlers = new HandlerList();

    private final UUID winner, loser;
    private final Reason reason;

    public MatchEndEvent(@NotNull final Match match, @Nullable final UUID winner, @Nullable final UUID loser, @NotNull final Reason reason) {
        super(match);
        Objects.requireNonNull(reason, "reason");
        this.winner = winner;
        this.loser = loser;
        this.reason = reason;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Nullable
    public UUID getWinner() {
        return winner;
    }

    @Nullable
    public UUID getLoser() {
        return loser;
    }

    @NotNull
    public Reason getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public enum Reason {

        OPPONENT_DEFEAT,
        TIE,
        MAX_TIME_REACHED,
        PLUGIN_DISABLE,
        OTHER
    }
}
