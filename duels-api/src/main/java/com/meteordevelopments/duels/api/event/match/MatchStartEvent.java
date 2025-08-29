package com.meteordevelopments.duels.api.event.match;

import com.meteordevelopments.duels.api.match.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MatchStartEvent extends MatchEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player[] players;

    public MatchStartEvent(@NotNull final Match match, @NotNull final Player... players) {
        super(match);
        Objects.requireNonNull(players, "players");
        this.players = players;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public Player[] getPlayers() {
        return players;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
