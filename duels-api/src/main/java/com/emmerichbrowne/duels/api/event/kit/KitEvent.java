package com.emmerichbrowne.duels.api.event.kit;

import com.emmerichbrowne.duels.api.event.SourcedEvent;
import com.emmerichbrowne.duels.api.kit.Kit;
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
