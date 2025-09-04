package com.emmerichbrowne.duels.countdown.party;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaImpl;
import com.emmerichbrowne.duels.countdown.DuelCountdown;
import com.emmerichbrowne.duels.match.party.PartyDuelMatch;
import com.emmerichbrowne.duels.party.Party;
import com.emmerichbrowne.duels.util.AdventureUtil;
import com.emmerichbrowne.duels.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class PartyDuelCountdown extends DuelCountdown {

    private final PartyDuelMatch match;

    private final Map<Party, String> info = new HashMap<>();

    public PartyDuelCountdown(final DuelsPlugin plugin, final ArenaImpl arena, final PartyDuelMatch match) {
        super(plugin, arena, match, plugin.getConfiguration().getCdPartyDuelMessages(), plugin.getConfiguration().getCdPartyDuelTitles());
        this.match = match;
        match.getAllParties().forEach(party -> info.put(party, StringUtil.join(match.getNames(party), ", ")));
    }
    
    @Override
    protected void sendMessage(final String rawMessage, final String message, final String title) {
        final String kitName = match.getKit() != null ? match.getKit().getName() : lang.getMessage("GENERAL.none");
        match.getPlayerToParty().forEach((player, party) -> {
            config.playSound(player, rawMessage);
            final Party opponentParty = arena.getOpponent(party);
            final String opponentInfo = opponentParty != null ? info.get(opponentParty) : null;
            final String opponentDisplay = opponentInfo != null ? opponentInfo : "Unknown";
            
            player.sendMessage(message
                    .replace("%opponents%", opponentDisplay)
                    .replace("%kit%", kitName)
                    .replace("%arena%", arena.getName())
            );

            if (title != null) {
                AdventureUtil.sendTitle(player, title, null, 0, 20, 50);
            }
        });
    }
}
