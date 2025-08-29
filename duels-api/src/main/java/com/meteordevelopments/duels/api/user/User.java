package com.meteordevelopments.duels.api.user;

import com.meteordevelopments.duels.api.kit.Kit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface User {

    @NotNull
    UUID getUuid();


    @NotNull
    String getName();


    int getWins();


    void setWins(final int wins);


    int getLosses();


    void setLosses(final int losses);


    boolean canRequest();


    void setRequests(final boolean requests);


    @NotNull
    List<MatchInfo> getMatches();


    int getRating();


    int getRating(@NotNull final Kit kit);


    int getTotalElo();
    
    void resetRating();


    void resetRating(@NotNull final Kit kit);


    void reset();
}
