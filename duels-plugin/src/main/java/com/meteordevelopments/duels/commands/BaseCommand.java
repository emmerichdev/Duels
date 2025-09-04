package com.meteordevelopments.duels.commands;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.arena.ArenaManagerImpl;
import com.meteordevelopments.duels.betting.BettingManager;
import com.meteordevelopments.duels.config.Config;
import com.meteordevelopments.duels.config.Lang;
import com.meteordevelopments.duels.data.UserManagerImpl;
import com.meteordevelopments.duels.duel.DuelManager;
import com.meteordevelopments.duels.hook.HookManager;
import com.meteordevelopments.duels.inventories.InventoryManager;
import com.meteordevelopments.duels.kit.KitManagerImpl;
import com.meteordevelopments.duels.party.PartyManagerImpl;
import com.meteordevelopments.duels.player.PlayerInfoManager;
import com.meteordevelopments.duels.queue.QueueManager;
import com.meteordevelopments.duels.queue.sign.QueueSignManagerImpl;
import com.meteordevelopments.duels.request.RequestManager;
import com.meteordevelopments.duels.setting.SettingsManager;
import com.meteordevelopments.duels.spectate.SpectateManagerImpl;

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