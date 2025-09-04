package com.emmerichbrowne.duels.betting;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.gui.betting.BettingGui;
import com.emmerichbrowne.duels.hook.hooks.VaultHook;
import com.emmerichbrowne.duels.setting.Settings;
import com.emmerichbrowne.duels.util.Loadable;
import com.emmerichbrowne.duels.util.gui.GuiListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BettingManager implements Loadable, Listener {

    private final DuelsPlugin plugin;
    private final GuiListener<DuelsPlugin> guiListener;

    public BettingManager(final DuelsPlugin plugin) {
        this.plugin = plugin;
        this.guiListener = plugin.getGuiListener();
    }

    @Override
    public void handleLoad() {
        final VaultHook vaultHook = plugin.getHookManager().getHook(VaultHook.class);

        if (vaultHook == null) {
            DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.hooks.vault-not-found"));
        } else if (vaultHook.getEconomy() == null) {
            DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.hooks.economy-not-found"));
        }
    }

    @Override
    public void handleUnload() {
    }

    public void open(final Settings settings, final Player sender, final Player target) {
        final BettingGui gui = new BettingGui(plugin, settings, sender, target);
        guiListener.addGui(sender, gui).open(sender);
        guiListener.addGui(target, gui).open(target);
    }
}
