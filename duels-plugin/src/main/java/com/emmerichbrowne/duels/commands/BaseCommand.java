package com.emmerichbrowne.duels.commands;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaManagerImpl;
import com.emmerichbrowne.duels.betting.BettingManager;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.data.UserManagerImpl;
import com.emmerichbrowne.duels.duel.DuelManager;
import com.emmerichbrowne.duels.hook.HookManager;
import com.emmerichbrowne.duels.inventories.InventoryManager;
import com.emmerichbrowne.duels.kit.KitManagerImpl;
import com.emmerichbrowne.duels.party.PartyManagerImpl;
import com.emmerichbrowne.duels.player.PlayerInfoManager;
import com.emmerichbrowne.duels.queue.QueueManager;
import com.emmerichbrowne.duels.queue.sign.QueueSignManagerImpl;
import com.emmerichbrowne.duels.request.RequestManager;
import com.emmerichbrowne.duels.setting.SettingsManager;
import com.emmerichbrowne.duels.spectate.SpectateManagerImpl;

public class BaseCommand extends co.aikar.commands.BaseCommand {

    protected final DuelsPlugin plugin;
    protected final Config config;
    protected final Lang lang;
    protected final UserManagerImpl userManager;
    protected final KitManagerImpl kitManager;
    protected final ArenaManagerImpl arenaManager;
    protected final QueueManager queueManager;
    protected final QueueSignManagerImpl queueSignManager;
    protected final SettingsManager settingManager;
    protected final PlayerInfoManager playerManager;
    protected final SpectateManagerImpl spectateManager;
    protected final BettingManager bettingManager;
    protected final InventoryManager inventoryManager;
    protected final DuelManager duelManager;
    protected final RequestManager requestManager;
    protected final HookManager hookManager;
    protected final PartyManagerImpl partyManager;

    public BaseCommand(DuelsPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.lang = plugin.getLang();
        this.userManager = plugin.getUserManager();
        this.partyManager = plugin.getPartyManager();
        this.kitManager = plugin.getKitManager();
        this.arenaManager = plugin.getArenaManager();
        this.queueManager = plugin.getQueueManager();
        this.queueSignManager = plugin.getQueueSignManager();
        this.settingManager = plugin.getSettingManager();
        this.playerManager = plugin.getPlayerManager();
        this.spectateManager = plugin.getSpectateManager();
        this.bettingManager = plugin.getBettingManager();
        this.inventoryManager = plugin.getInventoryManager();
        this.duelManager = plugin.getDuelManager();
        this.requestManager = plugin.getRequestManager();
        this.hookManager = plugin.getHookManager();
    }
}