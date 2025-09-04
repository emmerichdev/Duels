package com.emmerichbrowne.duels.gui;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaManagerImpl;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.kit.KitManagerImpl;
import com.emmerichbrowne.duels.queue.QueueManager;
import com.emmerichbrowne.duels.queue.sign.QueueSignManagerImpl;
import com.emmerichbrowne.duels.request.RequestManager;
import com.emmerichbrowne.duels.setting.SettingsManager;
import com.emmerichbrowne.duels.spectate.SpectateManagerImpl;
import com.emmerichbrowne.duels.util.gui.Button;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public abstract class BaseButton extends Button<DuelsPlugin> {

    protected final Config config;
    protected final Lang lang;
    protected final KitManagerImpl kitManager;
    protected final ArenaManagerImpl arenaManager;
    protected final SettingsManager settingManager;
    protected final QueueManager queueManager;
    protected final QueueSignManagerImpl queueSignManager;
    protected final SpectateManagerImpl spectateManager;
    protected final RequestManager requestManager;

    protected BaseButton(final DuelsPlugin plugin, final ItemStack displayed) {
        super(plugin, displayed);
        this.config = plugin.getConfiguration();
        this.lang = plugin.getLang();
        this.kitManager = plugin.getKitManager();
        this.arenaManager = plugin.getArenaManager();
        this.settingManager = plugin.getSettingManager();
        this.queueManager = plugin.getQueueManager();
        this.queueSignManager = plugin.getQueueSignManager();
        this.spectateManager = plugin.getSpectateManager();
        this.requestManager = plugin.getRequestManager();
    }

    // Helper methods for common button states
    protected Component createEnabledComponent(String text) {
        return Component.text(text).color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true);
    }

    protected Component createDisabledComponent(String text) {
        return Component.text(text).color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true);
    }

    protected Component createSelectedComponent(String text) {
        return Component.text(text).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true);
    }

    protected Component createInfoComponent(String text) {
        return Component.text(text).color(NamedTextColor.GRAY);
    }

    protected void handlePermissionError(Player player, String permission) {
        playErrorSound(player);
        lang.sendMessage(player, "ERROR.no-permission", "permission", permission);
    }

    protected void handleSuccess(Player player) {
        playSuccessSound(player);
    }
}
