package com.meteordevelopments.duels.command.commands.duel.subcommands;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.api.event.request.RequestAcceptEvent;
import com.meteordevelopments.duels.command.BaseCommand;
import com.meteordevelopments.duels.request.RequestImpl;
import com.meteordevelopments.duels.util.function.Pair;
import com.meteordevelopments.duels.util.validator.ValidatorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class AcceptCommand extends BaseCommand {

    public AcceptCommand(final DuelsPlugin plugin) {
        super(plugin, "accept", "accept [player]", "Accepts a duel request.", 2, true);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final Player player = (Player) sender;
        final Player target = Bukkit.getPlayerExact(args[1]);

        if (target == null || !player.canSee(target)) {
            lang.sendMessage(sender, "ERROR.player.not-found", "name", args[1]);
            return;
        }

        if (!ValidatorUtil.validate(validatorManager.getDuelAcceptTargetValidators(), new Pair<>(player, target), partyManager.get(target), Collections.emptyList())) {
            return;
        }

        final RequestImpl request = requestManager.remove(target, player);

        if (request == null) {
            lang.sendMessage(sender, "ERROR.duel.no-request", "name", target.getName());
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
        duelManager.startMatch(target, player, request.getSettings(), null, null);
    }
}
