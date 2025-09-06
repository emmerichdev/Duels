package com.emmerichbrowne.duels.commands;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaManagerImpl;
import com.emmerichbrowne.duels.betting.BettingManager;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.inventories.InventoryManager;
import com.emmerichbrowne.duels.kit.KitManagerImpl;
import com.emmerichbrowne.duels.leaderboard.manager.LeaderboardManager;
import com.emmerichbrowne.duels.party.PartyManagerImpl;
import com.emmerichbrowne.duels.player.PlayerInfoManager;
import com.emmerichbrowne.duels.queue.QueueManager;
import com.emmerichbrowne.duels.queue.sign.QueueSignManagerImpl;
import com.emmerichbrowne.duels.rank.manager.RankManager;
import com.emmerichbrowne.duels.request.RequestManager;
import com.emmerichbrowne.duels.setting.SettingsManager;
import com.emmerichbrowne.duels.duel.DuelManager;
import com.emmerichbrowne.duels.data.UserManagerImpl;
import com.emmerichbrowne.duels.spectate.SpectateManagerImpl;
import com.emmerichbrowne.duels.util.CC;

public abstract class BaseCommand extends co.aikar.commands.BaseCommand {

    protected final DuelsPlugin plugin;
    protected final Config config;
    protected final Lang lang;
    protected final KitManagerImpl kitManager;
    protected final ArenaManagerImpl arenaManager;
    protected final BettingManager bettingManager;
    protected final SettingsManager settingManager;
    protected final QueueManager queueManager;
    protected final QueueSignManagerImpl queueSignManager;
    protected final RequestManager requestManager;
    protected final PartyManagerImpl partyManager;
    protected final LeaderboardManager leaderboardManager;
    protected final RankManager rankManager;
    protected final UserManagerImpl userManager;
    protected final PlayerInfoManager playerManager;
    protected final InventoryManager inventoryManager;
    protected final DuelManager duelManager;
    protected final SpectateManagerImpl spectateManager;

    public BaseCommand(final DuelsPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.lang = plugin.getLang();
        this.kitManager = plugin.getKitManager();
        this.arenaManager = plugin.getArenaManager();
        this.bettingManager = plugin.getBettingManager();
        this.settingManager = plugin.getSettingManager();
        this.queueManager = plugin.getServerRole() == com.emmerichbrowne.duels.core.ServerRole.LOBBY ? plugin.getQueueManager() : null;
        this.queueSignManager = plugin.getServerRole() == com.emmerichbrowne.duels.core.ServerRole.LOBBY ? plugin.getQueueSignManager() : null;
        this.requestManager = plugin.getRequestManager();
        this.partyManager = plugin.getPartyManager();
        this.leaderboardManager = plugin.getLeaderboardManager();
        this.rankManager = plugin.getRankManager();
        this.userManager = plugin.getUserManager();
        this.playerManager = plugin.getPlayerManager();
        this.inventoryManager = plugin.getInventoryManager();
        this.duelManager = plugin.getDuelManager();
        this.spectateManager = plugin.getSpectateManager();
    }

    protected String getPrefix() {
        return CC.translateConsole("&b&lDuels &7» ");
    }
}