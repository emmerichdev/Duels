package com.meteordevelopments.duels.request;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.api.event.request.RequestSendEvent;
import com.meteordevelopments.duels.config.Config;
import com.meteordevelopments.duels.config.Lang;
import com.meteordevelopments.duels.setting.Settings;
import com.meteordevelopments.duels.util.Loadable;
import com.meteordevelopments.duels.util.command.CommandUtil;
import com.meteordevelopments.duels.util.command.SettingsDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestManager implements Loadable, Listener {

    private final Config config;
    private final Lang lang;
    private final Map<UUID, Map<UUID, RequestImpl>> requests = new HashMap<>();

    public RequestManager(final DuelsPlugin plugin) {
        this.config = plugin.getConfiguration();
        this.lang = plugin.getLang();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void handleLoad() {
    }

    @Override
    public void handleUnload() {
        requests.clear();
    }

    private Map<UUID, RequestImpl> get(final Player player, final boolean create) {
        Map<UUID, RequestImpl> cached = requests.get(player.getUniqueId());

        if (cached == null && create) {
            requests.put(player.getUniqueId(), cached = new HashMap<>());
            return cached;
        }

        return cached;
    }

    public void send(final Player sender, final Player target, final Settings settings) {
        final RequestImpl request = new RequestImpl(sender, target, settings);
        final RequestSendEvent event = new RequestSendEvent(sender, target, request);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        final boolean isParty = request.isPartyDuel();
        get(sender, true).put(isParty ? request.getTargetParty().getOwner().getUuid() : target.getUniqueId(), request);

        final SettingsDisplay display = CommandUtil.formatSettingsDisplay(settings, lang);

        if (request.isPartyDuel()) {
            final Player targetPartyLeader = request.getTargetParty().getOwner().getPlayer();
            lang.sendMessage(Collections.singleton(sender), "COMMAND.duel.party-request.send.sender-party",
                    "owner", sender.getName(), "name", target.getName(), "kit", display.kit(), "own_inventory", display.ownInventory(), "arena", display.arena());
            lang.sendMessage(targetPartyLeader, "COMMAND.duel.party-request.send.receiver-party",
                    "name", sender.getName(), "kit", display.kit(), "own_inventory", display.ownInventory(), "arena", display.arena());
            sendClickableMessage("COMMAND.duel.party-request.send.clickable-text.", sender, targetPartyLeader);
        } else {
            final int betAmount = settings.getBet();
            final String itemBetting = settings.isItemBetting() ? lang.getMessage("GENERAL.enabled") : lang.getMessage("GENERAL.disabled");
            lang.sendMessage(sender, "COMMAND.duel.request.send.sender",
                    "name", target.getName(), "kit", display.kit(), "own_inventory", display.ownInventory(), "arena", display.arena(), "bet_amount", betAmount, "item_betting", itemBetting);
            lang.sendMessage(target, "COMMAND.duel.request.send.receiver",
                    "name", sender.getName(), "kit", display.kit(), "own_inventory", display.ownInventory(), "arena", display.arena(), "bet_amount", betAmount, "item_betting", itemBetting);
            sendClickableMessage("COMMAND.duel.request.send.clickable-text.", sender, target);
        }
    }

    private void sendClickableMessage(final String path, final Player sender, final Player target) {
        Component infoComponent = Component.text(lang.getMessage(path + "info.text"))
                .hoverEvent(HoverEvent.showText(Component.text(lang.getMessage(path + "info.hover-text"))));
        
        Component acceptComponent = Component.text(lang.getMessage(path + "accept.text"))
                .clickEvent(ClickEvent.suggestCommand("/duel accept " + sender.getName()))
                .hoverEvent(HoverEvent.showText(Component.text(lang.getMessage(path + "accept.hover-text"))))
                .color(NamedTextColor.GREEN);
        
        Component denyComponent = Component.text(lang.getMessage(path + "deny.text"))
                .clickEvent(ClickEvent.suggestCommand("/duel deny " + sender.getName()))
                .hoverEvent(HoverEvent.showText(Component.text(lang.getMessage(path + "deny.hover-text"))))
                .color(NamedTextColor.RED);
        
        Component fullMessage = infoComponent.append(acceptComponent).append(denyComponent);
        target.sendMessage(fullMessage);
    }

    public RequestImpl get(final Player sender, final Player target) {
        final Map<UUID, RequestImpl> cached = get(sender, false);

        if (cached == null) {
            return null;
        }

        final RequestImpl request = cached.get(target.getUniqueId());

        if (request == null) {
            return null;
        }

        if (System.currentTimeMillis() - request.getCreation() >= config.getExpiration() * 1000L) {
            cached.remove(target.getUniqueId());
            return null;
        }

        return request;
    }

    public boolean has(final Player sender, final Player target) {
        return get(sender, target) != null;
    }

    public RequestImpl remove(final Player sender, final Player target) {
        final Map<UUID, RequestImpl> cached = get(sender, false);

        if (cached == null) {
            return null;
        }

        final RequestImpl request = cached.remove(target.getUniqueId());

        if (request == null) {
            return null;
        }

        if (System.currentTimeMillis() - request.getCreation() >= config.getExpiration() * 1000L) {
            cached.remove(target.getUniqueId());
            return null;
        }

        return request;
    }

    @EventHandler
    public void on(final PlayerQuitEvent event) {
        requests.remove(event.getPlayer().getUniqueId());
    }
}
