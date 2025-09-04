package com.emmerichbrowne.duels.api.queue.sign;

import com.emmerichbrowne.duels.api.queue.DQueue;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface QueueSign {

    @NotNull
    Location getLocation();


    @NotNull
    DQueue getQueue();


    boolean isRemoved();
}
