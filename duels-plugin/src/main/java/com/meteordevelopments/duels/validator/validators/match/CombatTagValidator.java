package com.meteordevelopments.duels.validator.validators.match;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.hook.hooks.DeluxeCombatHook;
import com.meteordevelopments.duels.setting.Settings;
import com.meteordevelopments.duels.validator.BaseBiValidator;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CombatTagValidator extends BaseBiValidator<Collection<Player>, Settings> {

    private static final String MESSAGE_KEY = "DUEL.start-failure.is-tagged";
    private static final String PARTY_MESSAGE_KEY = "DUEL.party-start-failure.is-tagged";

    private final DeluxeCombatHook deluxeCombat;

    public CombatTagValidator(final DuelsPlugin plugin) {
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
    public boolean validate(final Collection<Player> players, final Settings settings) {
        if (players.stream().anyMatch(this::isTagged)) {
            lang.sendMessage(players, settings.isPartyDuel() ? PARTY_MESSAGE_KEY : MESSAGE_KEY);
            return false;
        }

        return true;
    }
}
