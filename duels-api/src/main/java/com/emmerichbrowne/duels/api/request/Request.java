package com.emmerichbrowne.duels.api.request;

import com.emmerichbrowne.duels.api.arena.Arena;
import com.emmerichbrowne.duels.api.kit.Kit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Request {

    @NotNull
    UUID getSender();


    @NotNull
    UUID getTarget();


    @Nullable
    Kit getKit();


    @Nullable
    Arena getArena();


    boolean canBetItems();


    int getBet();
}
