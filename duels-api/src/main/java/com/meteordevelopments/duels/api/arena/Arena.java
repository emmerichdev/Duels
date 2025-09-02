package com.meteordevelopments.duels.api.arena;

import com.meteordevelopments.duels.api.event.arena.ArenaRemoveEvent;
import com.meteordevelopments.duels.api.event.arena.ArenaSetPositionEvent;
import com.meteordevelopments.duels.api.event.arena.ArenaStateChangeEvent;
import com.meteordevelopments.duels.api.match.Match;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Arena {

    @NotNull
    String getName();


    boolean isDisabled();


    boolean setDisabled(@Nullable final CommandSender source, final boolean disabled);


    boolean setDisabled(final boolean disabled);


    @Nullable
    World getWorld();


    @Nullable
    Location getPosition(final int pos);


    boolean setPosition(@Nullable final Player source, final int pos, @NotNull final Location location);


    boolean setPosition(final int pos, @NotNull final Location location);


    boolean isUsed();


    @Nullable
    Match getMatch();

    @Nullable
    Player getOpponent(Player player);


    boolean has(@NotNull final Player player);


    boolean isRemoved();
}
