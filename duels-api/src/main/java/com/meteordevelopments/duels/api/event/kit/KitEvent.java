package com.meteordevelopments.duels.api.event.kit;

import com.meteordevelopments.duels.api.event.SourcedEvent;
import com.meteordevelopments.duels.api.kit.Kit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class KitEvent extends SourcedEvent {

    private final Kit kit;

    KitEvent(@Nullable final CommandSender source, @NotNull final Kit kit) {
        super(source);
        Objects.requireNonNull(kit, "kit");
        this.kit = kit;
    }

    @NotNull
    public Kit getKit() {
        return kit;
    }
}
