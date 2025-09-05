package com.emmerichbrowne.duels.commands;

import co.aikar.commands.annotation.*;
import com.emmerichbrowne.duels.DuelsPlugin;

import com.emmerichbrowne.duels.api.event.request.RequestAcceptEvent;
import com.emmerichbrowne.duels.api.event.request.RequestDenyEvent;
import com.emmerichbrowne.duels.api.user.UserManager;
import com.emmerichbrowne.duels.data.UserData;
import com.emmerichbrowne.duels.gui.inventory.InventoryGui;
import com.emmerichbrowne.duels.hook.hooks.VaultHook;
import com.emmerichbrowne.duels.hook.hooks.worldguard.WorldGuardHook;
import com.emmerichbrowne.duels.kit.KitImpl;
import com.emmerichbrowne.duels.party.Party;
import com.emmerichbrowne.duels.request.RequestImpl;
import com.emmerichbrowne.duels.setting.Settings;
import com.emmerichbrowne.duels.util.DateUtil;
import com.emmerichbrowne.duels.util.TextBuilder;
import com.emmerichbrowne.duels.util.UUIDUtil;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import io.papermc.paper.plugin.configuration.PluginMeta;
import com.emmerichbrowne.duels.command.AutoRegister;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

@AutoRegister
@CommandAlias("duel")
@CommandPermission("duels.duel")
public class DuelCommand extends BaseCommand {

    private final WorldGuardHook worldGuard;
    private final VaultHook vault;

    public DuelCommand(DuelsPlugin plugin) {
        super(plugin);
        this.worldGuard = plugin.getHookManager().getHook(WorldGuardHook.class);
        this.vault = plugin.getHookManager().getHook(VaultHook.class);
    }

    @Default
    @CommandCompletion("@players")
    public void onDuel(
        @Conditions("not_in_creative|inventory_empty|not_in_blacklisted_world|not_combat_tagged|in_duel_zone|not_in_match|not_spectating") Player player,
        @Conditions("can_receive_requests|not_in_match|not_spectating|target_not_self") Player target,
        @Optional Integer bet,
        @Optional Boolean itemBetting,
        @Optional String kitName
    ) {
        if (userManager.get(player) == null) {
            lang.sendMessage(player, "ERROR.data.load-failure");
            return;
        }

        final Party party = partyManager.get(player);
        final Collection<Player> players = party == null ? Collections.singleton(player) : party.getOnlineMembers();

        final Party targetParty = partyManager.get(target);
        final Collection<Player> targetPlayers = targetParty == null ? Collections.singleton(target) : targetParty.getOnlineMembers();

        if (targetParty == null) {
            if (party != null) {
                lang.sendMessage(player, "ERROR.party.not-in-party.target", "name", target.getName());
                return;
            }
        } else {
            if (party == null) {
                lang.sendMessage(player, "ERROR.party.not-in-party.sender", "name", player.getName());
                return;
            }
            if (party.equals(targetParty)) {
                lang.sendMessage(player, "ERROR.party.in-same-party", "name", target.getName());
                return;
            }
            if (config.isPartySameSizeOnly() && party.size() != targetParty.size()) {
                lang.sendMessage(player, "ERROR.party.is-not-same-size");
                return;
            }
            if (targetPlayers.size() != targetParty.size()) {
                lang.sendMessage(player, "ERROR.party.is-not-online.target", "name", target.getName());
                return;
            }
        }
        if (targetParty != null) {
            if (!party.isOwner(player)) {
                lang.sendMessage(player, "ERROR.party.is-not-owner");
                return;
            }
        }

        if (requestManager.has(player, target)) {
            lang.sendMessage(player, targetParty != null ? "ERROR.party-duel.already-has-request" : "ERROR.duel.already-has-request", "name", target.getName());
            return;
        }


        final Settings settings = settingManager.getSafely(player);
        settings.setBet(0);
        settings.setTarget(target);
        settings.setSenderParty(party);
        settings.setTargetParty(targetParty);
        settings.clearCache();
        players.forEach(all -> {
            settings.setBaseLoc(all);
            settings.setDuelzone(all, worldGuard != null ? worldGuard.findDuelZone(all) : null);
        });

        boolean sendRequest = false;

        if (bet != null) {
            if (party != null) {
                lang.sendMessage(player, "ERROR.party-duel.option-unavailable");
                return;
            }

            if (bet > 0 && config.isMoneyBettingEnabled()) {
                if (config.isMoneyBettingUsePermission() && !player.hasPermission("duels.moneybetting") && !player.hasPermission("duels.setting.all")) {
                    lang.sendMessage(player, "ERROR.no-permission", "permission", "duels.use.money-betting");
                    return;
                }

                if (vault == null || vault.getEconomy() == null) {
                    lang.sendMessage(player, "ERROR.setting.disabled-option", "option", lang.getMessage("GENERAL.betting"));
                    return;
                }

                if (!vault.getEconomy().has(player, bet)) {
                    lang.sendMessage(player, "ERROR.command.not-enough-money");
                    return;
                }

                settings.setBet(bet);
            }
        }

        if (itemBetting != null && itemBetting) {
            if (!config.isItemBettingEnabled()) {
                lang.sendMessage(player, "ERROR.setting.disabled-option", "option", lang.getMessage("GENERAL.item-betting"));
                return;
            }

            if (config.isItemBettingUsePermission() && !player.hasPermission("duels.itembetting") && !player.hasPermission("duels.setting.all")) {
                lang.sendMessage(player, "ERROR.no-permission", "permission", "duels.use.item-betting");
                return;
            }

            settings.setItemBetting(true);
        }

        if (kitName != null) {
            if (kitName.equals("-")) {
                if (!config.isOwnInventoryEnabled()) {
                    lang.sendMessage(player, "ERROR.setting.disabled-option", "option", lang.getMessage("GENERAL.own-inventory"));
                    return;
                }

                if (config.isOwnInventoryUsePermission() && !player.hasPermission("duels.owninventory") && !player.hasPermission("duels.setting.all")) {
                    lang.sendMessage(player, "ERROR.no-permission", "permission", "duels.use.own-inventory");
                    return;
                }

                settings.setOwnInventory(true);
            } else if (!config.isKitSelectingEnabled()) {
                lang.sendMessage(player, "ERROR.setting.disabled-option", "option", lang.getMessage("GENERAL.kit-selector"));
                return;
            }
            else {
                final KitImpl kit = kitManager.get(kitName);

                if (kit == null) {
                    lang.sendMessage(player, "ERROR.kit.not-found", "name", kitName);
                    return;
                }

                final String permission = String.format("duels.kits.%s", kitName.replace(" ", "-").toLowerCase());

                if (kit.isUsePermission() && !player.hasPermission("duels.kits.*") && !player.hasPermission(permission)) {
                    lang.sendMessage(player, "ERROR.no-permission", "permission", permission);
                    return;
                }

                settings.setKit(kit);
            }
            sendRequest = true;
        }


        if (sendRequest) {
            requestManager.send(player, target, settings);
        } else if (config.isOwnInventoryEnabled()) {
            settings.openGui(player);
        } else {
            kitManager.getGui().open(player);
        }
    }

    @Subcommand("accept")
    @CommandCompletion("@players")
    public void onAccept(
        @Conditions("not_in_match|not_spectating") Player player,
        @Conditions("not_in_match|not_spectating|target_not_self") Player target
    ) {
        final Party targetParty = partyManager.get(target);
        final Party senderParty = partyManager.get(player);
        if (targetParty != null) {
            if (senderParty == null) {
                lang.sendMessage(player, "ERROR.party.not-in-party.sender", "name", player.getName());
                return;
            }
            if (senderParty.equals(targetParty)) {
                lang.sendMessage(player, "ERROR.party.in-same-party", "name", target.getName());
                return;
            }
            if (!targetParty.isOwner(target)) {
                lang.sendMessage(player, "ERROR.party.is-not-owner.target", "name", target.getName());
                return;
            }
        }

        final RequestImpl request = requestManager.remove(target, player);

        if (request == null) {
            lang.sendMessage(player, "ERROR.duel.no-request", "name", target.getName());
            return;
        }

        final RequestAcceptEvent event = new RequestAcceptEvent(player, target, request);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (request.isPartyDuel()) {
            final Player targetPartyLeader = request.getTargetParty().getOwner().getPlayer();
            final Player senderPartyLeader = request.getSenderParty().getOwner().getPlayer();

            if (senderPartyLeader != null) {
                lang.sendMessage(Collections.singleton(senderPartyLeader), "COMMAND.duel.party-request.accept.receiver-party", "owner", player.getName(), "name", target.getName());
            }
            if (targetPartyLeader != null) {
                lang.sendMessage(targetPartyLeader, "COMMAND.duel.party-request.accept.sender-party", "owner", target.getName(), "name", player.getName());
            }
        } else {
            lang.sendMessage(player, "COMMAND.duel.request.accept.receiver", "name", target.getName());
            lang.sendMessage(target, "COMMAND.duel.request.accept.sender", "name", player.getName());
        }

        duelManager.startMatch(target, player, request.getSettings(), Collections.emptyMap(), null);
    }

    @Subcommand("deny")
    @CommandCompletion("@players")
    public void onDeny(Player player, Player target) {
        final RequestImpl request = requestManager.remove(target, player);

        if (request == null) {
            lang.sendMessage(player, "ERROR.duel.no-request", "name", target.getName());
            return;
        }

        final RequestDenyEvent event = new RequestDenyEvent(player, target, request);
        Bukkit.getPluginManager().callEvent(event);

        if (request.isPartyDuel()) {
            final Player targetPartyLeader = request.getTargetParty().getOwner().getPlayer();
            final Player senderPartyLeader = request.getSenderParty().getOwner().getPlayer();
            lang.sendMessage(Collections.singleton(senderPartyLeader), "COMMAND.duel.party-request.deny.receiver-party", "owner", player.getName(), "name", target.getName());
            lang.sendMessage(targetPartyLeader, "COMMAND.duel.party-request.deny.sender-party", "owner", target.getName(), "name", player.getName());
        } else {
            lang.sendMessage(player, "COMMAND.duel.request.deny.receiver", "name", target.getName());
            lang.sendMessage(target, "COMMAND.duel.request.deny.sender", "name", player.getName());
        }
    }

    @Subcommand("stats")
    @CommandCompletion("@players")
    public void onStats(Player player, @Optional Player target) {
        if (target != null) {
            if (!player.hasPermission("duels.stats.others")) {
                lang.sendMessage(player, "ERROR.no-permission", "permission", "duels.stats.others");
                return;
            }
            displayStats(player, target.getName());
            return;
        }

        displayStats(player, player.getName());
    }

    private void displayStats(final Player sender, final String name) {
        final UserData user = userManager.get(name);

        if (user == null) {
            lang.sendMessage(sender, "ERROR.data.not-found", "name", name);
            return;
        }

        final String wins = String.valueOf(user.getWins());
        final String losses = String.valueOf(user.getLosses());
        final String wlRatio = String.valueOf(user.getLosses() > 0 ? Math.round(((double) user.getWins() / (double) user.getLosses()) * 100.0) / 100.0 : user.getWins());
        final String requests = String.valueOf(user.canRequest() ? lang.getMessage("GENERAL.enabled") : lang.getMessage("GENERAL.disabled"));
        final Object[] args = {"name", user.getName(), "wins", wins, "losses", losses, "wl_ratio", wlRatio, "requests_enabled", requests};
        lang.sendMessage(sender, "COMMAND.duel.stats.displayed", args);

        if (config.isDisplayKitRatings() || config.isDisplayNoKitRating()) {
            lang.sendMessage(sender, "COMMAND.duel.stats.rating.header", args);

            if (config.isDisplayNoKitRating()) {
                lang.sendMessage(sender, "COMMAND.duel.stats.rating.format",
                        "type", config.getTopNoKitType(), "kit", config.getTopNoKitType(), "rating", user.getRating());
            }

            if (config.isDisplayKitRatings()) {
                kitManager.getKits().forEach(kit -> lang.sendMessage(sender, "COMMAND.duel.stats.rating.format",
                        "type", kit.getName(), "kit", kit.getName(), "rating", user.getRating(kit)));
            }

            lang.sendMessage(sender, "COMMAND.duel.stats.rating.footer", args);
        }

        if (config.isDisplayPastMatches()) {
            lang.sendMessage(sender, "COMMAND.duel.stats.match.header", args);

            final Calendar calendar = new GregorianCalendar();

            user.getMatches().forEach(match -> {
                final String kit = match.getKit() != null ? match.getKit() : lang.getMessage("GENERAL.none");
                final String duration = DateUtil.formatMilliseconds(match.getDuration());
                final String timeSince = DateUtil.formatMilliseconds(calendar.getTimeInMillis() - match.getCreation());
                TextBuilder
                        .of(lang.getMessage("COMMAND.duel.stats.match.format", "winner", match.getWinner(), "loser", match.getLoser()))
                        .setHoverEvent(lang.getMessage("COMMAND.duel.stats.match.hover-text",
                                "kit", kit, "duration", duration, "time", timeSince, "health", match.getHealth()))
                        .send(sender);
            });
            lang.sendMessage(sender, "COMMAND.duel.stats.match.footer", args);
        }
    }

    @Subcommand("toggle")
    public void onToggle(Player player) {
        final UserData user = userManager.get(player);

        if (user == null) {
            lang.sendMessage(player, "ERROR.data.load-failure");
            return;
        }

        user.setRequests(!user.canRequest());
        lang.sendMessage(player, "COMMAND.duel.toggle." + (user.canRequest() ? "enabled" : "disabled"));
    }

    @Subcommand("top")
    @CommandCompletion("-|wins|losses|@kits")
    public void onTop(Player player, @Default("-") String type) {
        if (!userManager.isLoaded()) {
            lang.sendMessage(player, "ERROR.data.not-loaded");
            return;
        }

        final UserManager.TopEntry topEntry;

        if (type.equals("-")) {
            topEntry = userManager.getTopRatings();
        } else if (type.equalsIgnoreCase("wins")) {
            topEntry = userManager.getWins();
        } else if (type.equalsIgnoreCase("losses")) {
            topEntry = userManager.getLosses();
        } else {
            final KitImpl kit = kitManager.get(type);

            if (kit == null) {
                lang.sendMessage(player, "ERROR.kit.not-found", "name", type);
                return;
            }

            topEntry = userManager.getTopRatings(kit);
        }

        final List<UserManager.TopData> top;

        if (topEntry == null || (top = topEntry.getData()).isEmpty()) {
            lang.sendMessage(player, "ERROR.top.no-data-available");
            return;
        }

        lang.sendMessage(player, "COMMAND.duel.top.next-update", "remaining", userManager.getNextUpdate(topEntry.getCreation()));
        lang.sendMessage(player, "COMMAND.duel.top.header", "type", topEntry.getType());

        for (int i = 0; i < top.size(); i++) {
            final UserManager.TopData data = top.get(i);
            lang.sendMessage(player, "COMMAND.duel.top.display-format",
                    "rank", i + 1, "name", data.name(), "score", data.value(), "identifier", topEntry.getIdentifier());
        }

        lang.sendMessage(player, "COMMAND.duel.top.footer", "type", topEntry.getType());
    }

    @Subcommand("_")
    public void onInventory(Player player, String uuid) {
        final UUID target = UUIDUtil.parseUUID(uuid);

        if (target == null) {
            lang.sendMessage(player, "ERROR.inventory-view.not-a-uuid", "input", uuid);
            return;
        }

        final InventoryGui gui = inventoryManager.get(UUID.fromString(uuid));

        if (gui == null) {
            lang.sendMessage(player, "ERROR.inventory-view.not-found", "uuid", target);
            return;
        }

        gui.open(player);
    }

    @Subcommand("version|v")
    public void onVersion(Player player) {
        final PluginMeta info = plugin.getPluginMeta();
        final String authors = info.getAuthors().isEmpty() ? "unknown" : String.join(", ", info.getAuthors());
        final String versionText = lang.getMessage("COMMAND.version", "plugin_name", info.getName(), "authors", authors, "plugin_version", info.getVersion());
        final TextBuilder textBuilder = TextBuilder.of(versionText);

        final String website = info.getWebsite();
        if (website != null && !website.trim().isEmpty()) {
            textBuilder.setClickEvent(ClickEvent.Action.OPEN_URL, website);
        }

        textBuilder.send(player);
    }
}