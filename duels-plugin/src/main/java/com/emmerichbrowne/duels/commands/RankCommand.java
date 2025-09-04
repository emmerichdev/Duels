package com.emmerichbrowne.duels.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.rank.Rank;
import org.bukkit.entity.Player;

@CommandAlias("rank")
@CommandPermission("duels.rank")
public class RankCommand extends BaseCommand {

    public RankCommand(DuelsPlugin plugin) {
        super(plugin);
    }

    @Default
    public void onRank(Player player) {
        if (!plugin.getRankManager().isEnabled()) {
            lang.sendMessage(player, "RANK.system-disabled");
            return;
        }

        Rank currentRank = plugin.getRankManager().getPlayerRank(player);
        if (currentRank == null) {
            lang.sendMessage(player, "RANK.not-found");
            return;
        }

        // Send rank information
        lang.sendMessage(player, "RANK.info.header");
        lang.sendMessage(player, "RANK.info.rank", "rank", currentRank.getColoredName());
        lang.sendMessage(player, "RANK.info.description", "description", currentRank.getDescription());

        // Show progress to next rank
        var userData = plugin.getUserManager().get(player);
        if (userData == null) {
            lang.sendMessage(player, "RANK.data-load-error");
            return;
        }

        int currentElo = userData.getTotalElo();
        double progress = currentRank.getProgress(currentElo);

        Rank nextRank = plugin.getRankManager().getNextRank(player);
        if (nextRank == null) {
            lang.sendMessage(player, "RANK.max-rank");
        } else {
            int eloNeeded = Math.max(0, nextRank.getMinElo() - currentElo);
            lang.sendMessage(player, "RANK.info.progress-to-next",
                    "next_rank", nextRank.getName(),
                    "progress", String.format("%.1f", progress),
                    "elo_needed", String.valueOf(eloNeeded));
        }

        lang.sendMessage(player, "RANK.info.total-elo", "elo", String.valueOf(currentElo));
    }
}
