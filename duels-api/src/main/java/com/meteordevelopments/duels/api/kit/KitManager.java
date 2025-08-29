package com.meteordevelopments.duels.api.kit;

import com.meteordevelopments.duels.api.event.kit.KitCreateEvent;
import com.meteordevelopments.duels.api.event.kit.KitRemoveEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface KitManager {

    @Nullable
    Kit get(@NotNull final String name);


    @Nullable
    Kit create(@NotNull final Player creator, @NotNull final String name);


    @Nullable
    Kit remove(@Nullable CommandSender source, @NotNull final String name);


    @Nullable
    Kit remove(@NotNull final String name);


    @NotNull
    List<Kit> getKits();
}
