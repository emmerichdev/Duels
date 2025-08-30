package com.meteordevelopments.duels.command.commands;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.command.BaseCommand;
import com.meteordevelopments.duels.rank.Rank;
import com.meteordevelopments.duels.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand extends BaseCommand {

    public RankCommand(final DuelsPlugin plugin) {
        super(plugin, "rank", "rank", "Shows your current rank information.", "duels.command.rank", 1, true);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        Player player = (Player) sender;

        if (!plugin.getRankManager().isEnabled()) {
            lang.sendMessage(sender, "RANK.system-disabled");
            return;
        }

        Rank currentRank = plugin.getRankManager().getPlayerRank(player);
        if (currentRank == null) {
            lang.sendMessage(sender, "RANK.not-found");
            return;
        }

        // Send rank information
        lang.sendMessage(sender, "RANK.info.header");
        lang.sendMessage(sender, "RANK.info.rank", "rank", currentRank.getColoredName());
        lang.sendMessage(sender, "RANK.info.description", "description", currentRank.getDescription());
        
        // Show progress to next rank
        var userData = plugin.getUserManager().get(player);
        if (userData == null) {
            lang.sendMessage(sender, "RANK.data-load-error");
            return;
        }
        
        int currentElo = userData.getTotalElo();
        double progress = currentRank.getProgress(currentElo);
        
        Rank nextRank = plugin.getRankManager().getNextRank(player);
        if (nextRank == null) {
            lang.sendMessage(sender, "RANK.max-rank");
        } else {
            int eloNeeded = Math.max(0, nextRank.getMinElo() - currentElo);
            lang.sendMessage(sender, "RANK.info.progress-to-next", 
                "next_rank", nextRank.getName(), 
                "progress", String.format("%.1f", progress),
                "elo_needed", String.valueOf(eloNeeded));
        }
        
        lang.sendMessage(sender, "RANK.info.total-elo", "elo", String.valueOf(currentElo));
    }
}
