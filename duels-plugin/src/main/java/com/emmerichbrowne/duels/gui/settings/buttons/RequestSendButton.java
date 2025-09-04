package com.emmerichbrowne.duels.gui.settings.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.gui.BaseButton;
import com.emmerichbrowne.duels.party.Party;
import com.emmerichbrowne.duels.setting.Settings;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RequestSendButton extends BaseButton {

    public RequestSendButton(final DuelsPlugin plugin) {
        super(plugin, ItemBuilder.of(CommonItems.GREEN_PANE.clone()).name(plugin.getLang().getMessage("GUI.settings.buttons.send.name")).build());
    }

    @Override
    public void onClick(final Player player) {
        final Settings settings = settingManager.getSafely(player);

        if (settings.getTarget() == null) {
            settings.reset();
            player.closeInventory();
            return;
        }

        final Player target = Bukkit.getPlayer(settings.getTarget());

        if (target == null) {
            settings.reset();
            player.closeInventory();
            lang.sendMessage(player, "ERROR.player.no-longer-online");
            return;
        }

        if (!settings.isOwnInventory() && settings.getKit() == null) {
            player.closeInventory();
            lang.sendMessage(player, "ERROR.duel.mode-unselected");
            return;
        }

        final Party senderParty = settings.getSenderParty();
        final Party targetParty = settings.getTargetParty();

        if ((senderParty != null && senderParty.isRemoved()) || (targetParty != null && targetParty.isRemoved())) {
            player.closeInventory();
            lang.sendMessage(player, "ERROR.party.not-found");
            return;
        }

        player.closeInventory();
        requestManager.send(player, target, settings);
    }
}
