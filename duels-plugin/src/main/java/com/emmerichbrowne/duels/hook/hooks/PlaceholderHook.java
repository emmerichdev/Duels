package com.emmerichbrowne.duels.hook.hooks;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.api.kit.Kit;
import com.emmerichbrowne.duels.api.queue.DQueue;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.hook.HookManager;
import com.emmerichbrowne.duels.queue.Queue;
import com.emmerichbrowne.duels.util.Log;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceholderHook extends PlaceholderExpansion {

    public static final String NAME = "PlaceholderAPI";

    private final DuelsPlugin plugin;

    public PlaceholderHook(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "duels";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        try {
            final String[] args = params.split("_");
            if (args.length == 0) return null;

            return switch (args[0].toLowerCase()) {
                case "queue" -> handleQueue(args);
                default -> null;
            };
        } catch (Exception ex) {
            Log.error("PlaceholderAPI request failed: " + params, ex);
            return null;
        }
    }

    private String handleQueue(String[] args) {
        if (plugin.getServerRole() != com.emmerichbrowne.duels.core.ServerRole.LOBBY) {
            return "0";
        }
        if (args.length < 2) return null;
        // queue_<kit or ->_<bet>
        final String kitParam = args[1];
        final int bet = args.length >= 3 ? Integer.parseInt(args[2]) : 0;
        final Kit kit = kitParam.equals("-") ? null : plugin.getKitManager().get(kitParam);
        var queue = plugin.getQueueManager().get(kit, bet);
        if (queue == null) {
            plugin.getQueueManager().create(kit, bet);
            queue = plugin.getQueueManager().get(kit, bet);
        }
        if (queue == null) return "0";
        return String.valueOf(queue.getQueuedPlayers().size());
    }
}