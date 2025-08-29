package com.meteordevelopments.duels.validator.validators.request.self;

import java.util.Collection;
import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.hook.hooks.DeluxeCombatHook;
import com.meteordevelopments.duels.party.Party;
import com.meteordevelopments.duels.validator.BaseTriValidator;
import org.bukkit.entity.Player;

public class SelfCombatTagValidator extends BaseTriValidator<Player, Party, Collection<Player>> {
   
    private static final String MESSAGE_KEY = "ERROR.duel.is-tagged";
    private static final String PARTY_MESSAGE_KEY = "ERROR.party-duel.is-tagged";

    private final DeluxeCombatHook deluxeCombat;

    public SelfCombatTagValidator(final DuelsPlugin plugin) {
        super(plugin);
        this.deluxeCombat = plugin.getHookManager().getHook(DeluxeCombatHook.class);
    }

    @Override
    public boolean shouldValidate() {
        return deluxeCombat != null && config.isDcPreventDuel();
    }

    private boolean isTagged(final Player player) {
        return deluxeCombat != null && deluxeCombat.isTagged(player);
    }

    @Override
    public boolean validate(final Player sender, final Party party, final Collection<Player> players) {
        if (players.stream().anyMatch(this::isTagged)) {
            lang.sendMessage(sender, party != null ? PARTY_MESSAGE_KEY : MESSAGE_KEY);
            return false;
        }

        return true;
    }
}
