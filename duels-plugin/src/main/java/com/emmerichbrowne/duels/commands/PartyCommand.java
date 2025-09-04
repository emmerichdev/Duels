package com.emmerichbrowne.duels.commands;

import co.aikar.commands.annotation.*;
import com.emmerichbrowne.duels.DuelsPlugin;

import com.emmerichbrowne.duels.data.UserData;
import com.emmerichbrowne.duels.party.Party;
import com.emmerichbrowne.duels.party.PartyInvite;
import com.emmerichbrowne.duels.party.PartyMember;
import com.emmerichbrowne.duels.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("party")
@CommandPermission("duels.party")
public class PartyCommand extends BaseCommand {

    public PartyCommand(DuelsPlugin plugin) {
        super(plugin);
    }

    @Default
    @CommandCompletion("@players")
    public void onParty(Player player, @Optional Player target) {
        if (target == null) {
            lang.sendMessage(player, "COMMAND.party.usage", "command", "party");
            return;
        }

        if (player.equals(target)) {
            lang.sendMessage(player, "ERROR.party.is-self");
            return;
        }

        final UserData user = userManager.get(target);

        if (user == null) {
            lang.sendMessage(player, "ERROR.data.not-found", "name", target.getName());
            return;
        }

        if (!user.canPartyRequest()) {
            lang.sendMessage(player, "ERROR.party.requests-disabled", "name", target.getName());
            return;
        }

        if (partyManager.isInParty(target)) {
            lang.sendMessage(player, "ERROR.party.already-in-party.target", "name", target.getName());
            return;
        }

        if (partyManager.hasInvite(player, target)) {
            lang.sendMessage(player, "ERROR.party.already-has-invite", "name", target.getName());
            return;
        }

        final Party party = partyManager.getOrCreate(player);

        if (!party.isOwner(player)) {
            lang.sendMessage(player, "ERROR.party.is-not-owner");
            return;
        }

        if (!partyManager.sendInvite(player, target, party)) {
            lang.sendMessage(player, "ERROR.party.max-size-reached.sender");
            return;
        }

        lang.sendMessage(party.getOnlineMembers(), "COMMAND.party.invite.send.members", "owner", player.getName(), "name", target.getName());
        lang.sendMessage(target, "COMMAND.party.invite.send.receiver", "name", player.getName());
    }

    @Subcommand("toggle")
    public void onToggle(Player player) {
        final UserData user = userManager.get(player);

        if (user == null) {
            lang.sendMessage(player, "ERROR.data.load-failure");
            return;
        }

        user.setPartyRequests(!user.canPartyRequest());
        lang.sendMessage(player, "COMMAND.party.toggle." + (user.canPartyRequest() ? "enabled" : "disabled"));
    }

    @Subcommand("accept|a")
    @CommandCompletion("@players")
    public void onAccept(Player player, Player target) {
        if (partyManager.isInParty(player)) {
            lang.sendMessage(player, "ERROR.party.already-in-party.sender");
            return;
        }

        final PartyInvite invite = partyManager.removeInvite(target, player);

        if (invite == null) {
            lang.sendMessage(player, "ERROR.party.no-invite", "name", target.getName());
            return;
        }

        final Party party = invite.getParty();

        if (party.isRemoved()) {
            lang.sendMessage(player, "ERROR.party.not-found");
            return;
        }

        if (party.size() >= config.getPartyMaxSize()) {
            lang.sendMessage(player, "ERROR.party.max-size-reached.target", "name", target.getName());
            return;
        }

        lang.sendMessage(player, "COMMAND.party.invite.accept.receiver", "name", target.getName());
        lang.sendMessage(party.getOnlineMembers(), "COMMAND.party.invite.accept.members", "name", player.getName());
        partyManager.join(player, party);
    }

    @Subcommand("list|ls")
    @CommandCompletion("@players")
    public void onList(Player player, @Optional Player target) {
        final Party party;

        if (target != null) {
            if (!player.hasPermission("duels.party.list.others")) {
                lang.sendMessage(player, "ERROR.no-permission", "permission", "duels.party.list.others");
                return;
            }

            party = partyManager.get(target);

            if (party == null) {
                lang.sendMessage(player, "ERROR.party.not-in-party.target", "name", target.getName());
                return;
            }

            showList(player, party);
            return;
        }

        party = partyManager.get(player);

        if (party == null) {
            lang.sendMessage(player, "ERROR.party.not-in-party.sender");
            return;
        }

        showList(player, party);
    }

    private void showList(final CommandSender sender, final Party party) {
        if (party == null) {
            lang.sendMessage(sender, "ERROR.party.not-in-party.sender");
            return;
        }

        final List<String> memberNames = new ArrayList<>(party.size());
        final List<String> onlineNames = new ArrayList<>();

        for (final PartyMember member : party.getMembers()) {
            memberNames.add(member.getName());

            if (member.isOnline()) {
                onlineNames.add(member.getName());
            }
        }

        lang.sendMessage(sender, "COMMAND.party.list",
                "members_count", memberNames.size(),
                "members", !memberNames.isEmpty() ? StringUtil.join(memberNames, ", ") : lang.getMessage("GENERAL.none"),
                "online_count", onlineNames.size(),
                "online_members", !onlineNames.isEmpty() ? StringUtil.join(onlineNames, ", ") : lang.getMessage("GENERAL.none"),
                "owner", party.getOwner().getName()
        );
    }

    @Subcommand("leave|l")
    public void onLeave(Player player) {
        final Party party = partyManager.get(player);

        if (party == null) {
            lang.sendMessage(player, "ERROR.party.not-in-party.sender");
            return;
        }

        if (party.isOwner(player)) {
            lang.sendMessage(player, "ERROR.party.is-owner");
            return;
        }

        partyManager.remove(player, party);
        lang.sendMessage(player, "COMMAND.party.leave.sender", "name", party.getOwner().getName());
        lang.sendMessage(party.getOnlineMembers(), "COMMAND.party.leave.members", "name", player.getName());
    }

    @Subcommand("kick|remove")
    @CommandCompletion("@players")
    public void onKick(Player player, Player target) {
        final Party party = partyManager.get(player);

        if (party == null) {
            lang.sendMessage(player, "ERROR.party.not-in-party.sender");
            return;
        }

        if (!party.isOwner(player)) {
            lang.sendMessage(player, "ERROR.party.is-not-owner");
            return;
        }

        final PartyMember member = party.get(target);

        if (member == null) {
            lang.sendMessage(player, "ERROR.party.not-a-member", "name", target.getName());
            return;
        }

        if (member.getUuid().equals(player.getUniqueId())) {
            lang.sendMessage(player, "ERROR.party.kick-self");
            return;
        }

        partyManager.remove(member, party);

        lang.sendMessage(target, "COMMAND.party.kick.receiver", "owner", player.getName());
        lang.sendMessage(party.getOnlineMembers(), "COMMAND.party.kick.members", "owner", player.getName(), "name", member.getName());
    }

    @Subcommand("friendlyfire|ff")
    public void onFriendlyFire(Player player) {
        final Party party = partyManager.get(player);

        if (party == null) {
            lang.sendMessage(player, "ERROR.party.not-in-party.sender");
            return;
        }

        if (!party.isOwner(player)) {
            lang.sendMessage(player, "ERROR.party.is-not-owner");
            return;
        }

        party.setFriendlyFire(!party.isFriendlyFire());
        lang.sendMessage(party.getOnlineMembers(), "COMMAND.party.friendly-fire." + (party.isFriendlyFire() ? "enabled" : "disabled"));
    }

    @Subcommand("transfer")
    @CommandCompletion("@players")
    public void onTransfer(Player player, Player target) {
        final Party party = partyManager.get(player);

        if (party == null) {
            lang.sendMessage(player, "ERROR.party.not-in-party.sender");
            return;
        }

        if (!party.isOwner(player)) {
            lang.sendMessage(player, "ERROR.party.is-not-owner");
            return;
        }

        if (!party.isMember(target)) {
            lang.sendMessage(player, "ERROR.party.not-a-member", "name", target.getName());
            return;
        }

        party.setOwner(target);
        lang.sendMessage(party.getOnlineMembers(), "COMMAND.party.transfer", "owner", player.getName(), "name", target.getName());
    }

    @Subcommand("disband")
    public void onDisband(Player player) {
        final Party party = partyManager.get(player);

        if (party == null) {
            lang.sendMessage(player, "ERROR.party.not-in-party.sender");
            return;
        }

        if (!party.isOwner(player)) {
            lang.sendMessage(player, "ERROR.party.is-not-owner");
            return;
        }

        lang.sendMessage(party.getOnlineMembers(), "COMMAND.party.disband", "owner", player.getName());
        partyManager.remove(party);
    }
}
