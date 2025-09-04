package com.emmerichbrowne.duels.api.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.GregorianCalendar;

public interface MatchInfo {

    @NotNull
    String getWinner();


    @NotNull
    String getLoser();


    @Nullable
    String getKit();


    long getCreation();


    long getDuration();


    double getHealth();
}
