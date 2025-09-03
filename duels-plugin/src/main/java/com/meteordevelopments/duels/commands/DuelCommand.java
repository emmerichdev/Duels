package com.meteordevelopments.duels.commands;

import co.aikar.commands.annotation.*;
import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.Permissions;
import com.meteordevelopments.duels.api.event.request.RequestAcceptEvent;
import com.meteordevelopments.duels.api.event.request.RequestDenyEvent;
import com.meteordevelopments.duels.api.user.UserManager;
import com.meteordevelopments.duels.data.UserData;
import com.meteordevelopments.duels.gui.inventory.InventoryGui;
import com.meteordevelopments.duels.hook.hooks.VaultHook;
import com.meteordevelopments.duels.hook.hooks.worldguard.WorldGuardHook;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.party.Party;
import com.meteordevelopments.duels.request.RequestImpl;
import com.meteordevelopments.duels.setting.Settings;
import com.meteordevelopments.duels.util.DateUtil;
import com.meteordevelopments.duels.util.TextBuilder;
import com.meteordevelopments.duels.util.UUIDUtil;
import com.meteordevelopments.duels.util.function.Pair;
import com.meteordevelopments.duels.util.validator.ValidatorUtil;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

@CommandAlias("duel")
@CommandPermission(Permissions.DUEL)
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
    public void onDuel(Player player, Player target, @Optional Integer bet, @Optional Boolean itemBetting, @Optional String kitName) {
        if (userManager.get(player) == null) {
            lang.sendMessage(player, "ERROR.data.load-failure");
            return;
        }

        final Party party = partyManager.get(player);
        final Collection<Player> players = party == null ? Collections.singleton(player) : party.getOnlineMembers();

        if (!ValidatorUtil.validate(validatorManager.getDuelSelfValidators(), player, party, players)) {
            return;
        }

        final Party targetParty = partyManager.get(target);
        final Collection<Player> targetPlayers = targetParty == null ? Collections.singleton(target) : targetParty.getOnlineMembers();
        if (!ValidatorUtil.validate(validatorManager.getDuelTargetValidators(), new Pair<>(player, target), targetParty, targetPlayers)) {
            return;
        }

        final Settings settings = settingManager.getSafely(player);
        // Reset bet to prevent accidents
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
                if (config.isMoneyBettingUsePermission() && !player.hasPermission(Permissions.MONEY_BETTING) && !player.hasPermission(Permissions.SETTING_ALL)) {
                    lang.sendMessage(player, "ERROR.no-permission", "permission", Permissions.MONEY_BETTING);
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

            if (config.isItemBettingUsePermission() && !player.hasPermission(Permissions.ITEM_BETTING) && !player.hasPermission(Permissions.SETTING_ALL)) {
                lang.sendMessage(player, "ERROR.no-permission", "permission", Permissions.ITEM_BETTING);
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

                if (config.isOwnInventoryUsePermission() && !player.hasPermission(Permissions.OWN_INVENTORY) && !player.hasPermission(Permissions.SETTING_ALL)) {
                    lang.sendMessage(player, "ERROR.no-permission", "permission", Permissions.OWN_INVENTORY);
                    return;
                }

                settings.setOwnInventory(true);
            } else if (!config.isKitSelectingEnabled()) {
                lang.sendMessage(player, "ERROR.setting.disabled-option", "option", lang.getMessage("GENERAL.kit-selector"));
                return;
            } else {
                final KitImpl kit = kitManager.get(kitName);

                if (kit == null) {
                    lang.sendMessage(player, "ERROR.kit.not-found", "name", kitName);
                    return;
                }

                final String permission = String.format(Permissions.KIT, kitName.replace(" ", "-").toLowerCase());

                if (kit.isUsePermission() && !player.hasPermission(Permissions.KIT_ALL) && !player.hasPermission(permission)) {
                    lang.sendMessage(player, "ERROR.no-permission", "permission", permission);
                    return;
                }

                settings.setKit(kit);
            }
            sendRequest = true;
        }


        if (sendRequest) {
            // If all settings were selected via command, send request without opening settings GUI.
            requestManager.send(player, target, settings);
        } else if (config.isOwnInventoryEnabled()) {
            // If own inventory is enabled, prompt request settings GUI.
            settings.openGui(player);
        } else {
            // Maintain old behavior: If own inventory is disabled, prompt kit selector first instead of request settings GUI.
            kitManager.getGui().open(player);
        }
    }

    @Subcommand("accept")
    @CommandCompletion("@players")
    public void onAccept(Player player, Player target) {
        if (!ValidatorUtil.validate(validatorManager.getDuelAcceptTargetValidators(), new Pair<>(player, target), partyManager.get(target), Collections.emptyList())) {
            return;
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

        // Start the actual duel match
        duelManager.startMatch(target, player, request.getSettings(), Collections.emptyMap(), null);
    }

    @Subcommand("deny")
    @CommandCompletion("@players")
    public void onDeny(Player player, Player target) {
        if (!ValidatorUtil.validate(validatorManager.getDuelDenyTargetValidators(), new Pair<>(player, target), partyManager.get(target), Collections.emptyList())) {
            return;
        }

        final RequestImpl request = requestManager.remove(target, player);
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
            if (!player.hasPermission(Permissions.STATS_OTHERS)) {
                lang.sendMessage(player, "ERROR.no-permission", "permission", Permissions.STATS_OTHERS);
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
        final PluginDescriptionFile info = plugin.getDescription();
        final String authors = info.getAuthors().isEmpty() ? "unknown" : String.join(", ", info.getAuthors());
        final String versionText = lang.getMessage("COMMAND.version", "plugin_name", info.getFullName(), "authors", authors, "plugin_version", info.getVersion());
        final TextBuilder textBuilder = TextBuilder.of(versionText);

        final String website = info.getWebsite();
        if (website != null && !website.trim().isEmpty()) {
            textBuilder.setClickEvent(ClickEvent.Action.OPEN_URL, website);
        }

        textBuilder.send(player);
    }
}