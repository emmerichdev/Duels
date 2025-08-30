package com.meteordevelopments.duels.data;

import com.meteordevelopments.duels.api.user.MatchInfo;
import lombok.Getter;

public class MatchData implements MatchInfo {

    @Getter
    private String winner;
    @Getter
    private String loser;
    @Getter
    private String kit;
    @Getter
    private long time;
    @Getter
    private long duration;
    @Getter
    private double health;

    private MatchData() {
    }

    public MatchData(final String winner, final String loser, final String kit, final long time, final long duration, final double health) {
        this.winner = winner;
        this.loser = loser;
        this.kit = kit;
        this.time = time;
        this.duration = duration;
        this.health = health;
    }

    @Override
    public long getCreation() {
        return time;
    }

    @Override
    public String toString() {
        return "MatchData{" +
                "winner='" + winner + '\'' +
                ", loser='" + loser + '\'' +
                ", time=" + time +
                ", duration=" + duration +
                ", health=" + health +
                '}';
    }
}
