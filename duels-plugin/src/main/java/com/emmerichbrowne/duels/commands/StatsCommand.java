package com.emmerichbrowne.duels.commands;

import co.aikar.commands.annotation.*;
import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.api.user.User;
import com.emmerichbrowne.duels.data.UserData;
import com.emmerichbrowne.duels.kit.KitImpl;
import com.emmerichbrowne.duels.util.function.TriFunction;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@CommandAlias("duels")
@CommandPermission("duels.admin")
public class StatsCommand extends BaseCommand {

    private final Map<String, Function<User, Integer>> getters = new HashMap<>();
    private final Map<String, BiConsumer<User, Integer>> setters = new HashMap<>();
    private final Map<String, TriFunction<User, String, Integer, Integer>> actions = new HashMap<>();

    public StatsCommand(DuelsPlugin plugin) {
        super(plugin);
        getters.put("wins", User::getWins);
        getters.put("losses", User::getLosses);
        setters.put("wins", User::setWins);
        setters.put("losses", User::setLosses);
        actions.put("set", (user, type, amount) -> amount);
        actions.put("add", (user, type, amount) -> getters.get(type).apply(user) + amount);
        actions.put("remove", (user, type, amount) -> getters.get(type).apply(user) - amount);
    }

    @Subcommand("setrating")
    @CommandCompletion("@players @kits 0|100|1000|1400")
    @Description("Sets player's rating for kit.")
    public void onSetRating(CommandSender sender, UserData user, String kitName, int rating) {
        KitImpl kit = null;
        if (!kitName.equals("-")) {
            kit = kitManager.get(kitName);
            if (kit == null) {
                lang.sendMessage(sender, "ERROR.kit.not-found", "name", kitName);
                return;
            }
        }

        final String finalKitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");
        user.setRating(kit, rating);
        lang.sendMessage(sender, "COMMAND.duels.set-rating", "name", user.getName(), "kit", finalKitName, "rating", rating);
    }

    @Subcommand("edit")
    @CommandCompletion("@players add|remove|set wins|losses 0|10|100|1000")
    @Description("Edits player's wins or losses.")
    public void onEdit(CommandSender sender, UserData user, String action, String type, int amount) {
        final TriFunction<User, String, Integer, Integer> actionFunction = actions.get(action.toLowerCase());

        if (actionFunction == null) {
            lang.sendMessage(sender, "ERROR.command.invalid-action", "action", action, "available_actions", actions.keySet());
            return;
        }

        final BiConsumer<User, Integer> setter = setters.get(type.toLowerCase());

        if (setter == null) {
            lang.sendMessage(sender, "ERROR.command.invalid-option", "option", type, "available_options", getters.keySet());
            return;
        }

        setter.accept(user, actionFunction.apply(user, type.toLowerCase(), amount));
        lang.sendMessage(sender, "COMMAND.duels.edit", "name", user.getName(), "type", type, "action", action, "amount", amount);
    }

    @Subcommand("resetrating")
    @CommandCompletion("@players @kits|all")
    @Description("Resets specified kit's rating or all.")
    public void onResetRating(CommandSender sender, UserData user, String kitName) {
        if (kitName.equalsIgnoreCase("all")) {
            user.resetRating();
            kitManager.getKits().forEach(user::resetRating);
            lang.sendMessage(sender, "COMMAND.duels.reset-rating", "name", user.getName(), "kit", "all");
        } else if (kitName.equals("-")) {
            user.resetRating();
            lang.sendMessage(sender, "COMMAND.duels.reset-rating", "name", user.getName(), "kit", lang.getMessage("GENERAL.none"));
        } else {
            final KitImpl kit = kitManager.get(kitName);

            if (kit == null) {
                lang.sendMessage(sender, "ERROR.kit.not-found", "name", kitName);
                return;
            }

            user.resetRating(kit);
            lang.sendMessage(sender, "COMMAND.duels.reset-rating", "name", user.getName(), "kit", kitName);
        }
    }

    @Subcommand("reset")
    @CommandCompletion("@players")
    @Description("Resets player's stats.")
    public void onReset(CommandSender sender, UserData user) {
        user.reset();
        lang.sendMessage(sender, "COMMAND.duels.reset", "name", user.getName());
    }
}
