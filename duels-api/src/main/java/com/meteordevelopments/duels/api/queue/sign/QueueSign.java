package com.meteordevelopments.duels.api.queue.sign;

import com.meteordevelopments.duels.api.event.queue.sign.QueueSignRemoveEvent;
import com.meteordevelopments.duels.api.queue.DQueue;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface QueueSign {

    @NotNull
    Location getLocation();


    @NotNull
    DQueue getQueue();


    boolean isRemoved();
}
