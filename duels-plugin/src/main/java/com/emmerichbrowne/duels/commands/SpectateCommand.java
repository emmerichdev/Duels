package com.emmerichbrowne.duels.commands;

import co.aikar.commands.annotation.*;
import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.api.spectate.SpectateManager;
import com.emmerichbrowne.duels.arena.ArenaImpl;
import com.emmerichbrowne.duels.match.DuelMatch;
import com.emmerichbrowne.duels.spectate.SpectatorImpl;
import com.emmerichbrowne.duels.util.inventory.InventoryUtil;
import com.emmerichbrowne.duels.scanner.AutoRegister;
import org.bukkit.entity.Player;

@AutoRegister
@CommandAlias("spectate|spec")
@CommandPermission("duels.spectate")
public class SpectateCommand extends BaseCommand {

    public SpectateCommand(DuelsPlugin plugin) {
        super(plugin);
    }

    @Default
    @CommandCompletion("@players")
    public void onSpectate(Player player, @Optional Player target) {
        final SpectatorImpl spectator = spectateManager.get(player);

        if (spectator != null) {
            spectateManager.stopSpectating(player);
            lang.sendMessage(player, "COMMAND.spectate.stop-spectate", "name", spectator.getTargetName());
            return;
        }

        if (target == null) {
            lang.sendMessage(player, "COMMAND.spectate.usage", "command", "spectate");
            return;
        }

        if (config.isSpecRequiresClearedInventory() && InventoryUtil.hasItem(player)) {
            lang.sendMessage(player, "ERROR.duel.inventory-not-empty");
            return;
        }

        final SpectateManager.Result result = spectateManager.startSpectating(player, target);

        switch (result) {
            case EVENT_CANCELLED:
                return;
            case IN_MATCH:
                lang.sendMessage(player, "ERROR.duel.already-spectating.sender");
                return;
            case IN_QUEUE:
                lang.sendMessage(player, "ERROR.duel.already-in-queue");
            case ALREADY_SPECTATING:
                lang.sendMessage(player, "ERROR.duel.already-in-match.sender");
                return;
            case TARGET_NOT_IN_MATCH:
                lang.sendMessage(player, "ERROR.duel.not-in-match", "name", target.getName());
                return;
            case SUCCESS:
                final ArenaImpl arena = arenaManager.get(target);

                if (arena == null || arena.getMatch() == null) {
                    return;
                }

                final DuelMatch match = arena.getMatch();
                final String kit = match.getKit() != null ? match.getKit().getName() : lang.getMessage("GENERAL.none");
                final Player opponent = arena.getOpponent(target);
                if (opponent == null) {
                    lang.sendMessage(player, "ERROR.player.not-found");
                    return;
                }
                lang.sendMessage(player, "COMMAND.spectate.start-spectate",
                        "name", target.getName(),
                        "opponent", opponent.getName(),
                        "kit", kit,
                        "arena", arena.getName(),
                        "bet_amount", match.getBet()
                );
        }
    }
}
