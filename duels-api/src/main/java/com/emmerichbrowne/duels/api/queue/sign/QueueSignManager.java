package com.emmerichbrowne.duels.api.queue.sign;

import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface QueueSignManager {

    @Nullable
    QueueSign get(@NotNull final Sign sign);


    @NotNull
    List<QueueSign> getQueueSigns();
}
