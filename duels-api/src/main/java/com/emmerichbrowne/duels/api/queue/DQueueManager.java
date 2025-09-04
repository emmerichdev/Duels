package com.emmerichbrowne.duels.api.queue;

import com.emmerichbrowne.duels.api.kit.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DQueueManager {

    @Nullable
    DQueue get(@Nullable final Kit kit, final int bet);


    @Nullable
    DQueue get(@NotNull final Player player);


    @Nullable
    DQueue create(@Nullable final CommandSender source, @Nullable final Kit kit, final int bet);


    @Nullable
    DQueue create(@Nullable final Kit kit, final int bet);


    @Nullable
    DQueue remove(@Nullable final CommandSender source, @Nullable final Kit kit, final int bet);


    @Nullable
    DQueue remove(@Nullable final Kit kit, final int bet);


    boolean isInQueue(@NotNull final Player player);


    boolean addToQueue(@NotNull final Player player, @NotNull final DQueue queue);


    @Nullable
    DQueue removeFromQueue(@NotNull final Player player);


    @NotNull
    List<DQueue> getQueues();
}
