package com.meteordevelopments.duels.commands;

import co.aikar.commands.annotation.*;
import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.api.kit.Kit;
import com.meteordevelopments.duels.api.queue.DQueue;
import com.meteordevelopments.duels.api.user.User;
import com.meteordevelopments.duels.arena.ArenaImpl;
import com.meteordevelopments.duels.config.Config;
import com.meteordevelopments.duels.data.UserData;
import com.meteordevelopments.duels.gui.bind.BindGui;
import com.meteordevelopments.duels.gui.options.OptionsGui;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.queue.Queue;
import com.meteordevelopments.duels.queue.sign.QueueSignImpl;
import com.meteordevelopments.duels.util.BlockUtil;
import com.meteordevelopments.duels.util.StringUtil;
import com.meteordevelopments.duels.util.function.TriFunction;
import com.meteordevelopments.duels.util.inventory.InventoryUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@CommandAlias("duels")
@CommandPermission("duels.admin")
public class DuelsCommand extends BaseCommand {

    private final Map<String, Function<User, Integer>> getters = new HashMap<>();
    private final Map<String, BiConsumer<User, Integer>> setters = new HashMap<>();
    private final Map<String, TriFunction<User, String, Integer, Integer>> actions = new HashMap<>();

    public DuelsCommand(DuelsPlugin plugin) {
        super(plugin);
        getters.put("wins", User::getWins);
        getters.put("losses", User::getLosses);
        setters.put("wins", User::setWins);
        setters.put("losses", User::setLosses);
        actions.put("set", (user, type, amount) -> amount);
        actions.put("add", (user, type, amount) -> getters.get(type).apply(user) + amount);
        actions.put("remove", (user, type, amount) -> getters.get(type).apply(user) - amount);
    }

    @Default
    @HelpCommand
    public void onDefault(CommandSender sender) {
        lang.sendMessage(sender, "COMMAND.duels.usage", "command", "duels");
    }

    @Subcommand("help")
    @CommandCompletion("arena|kit|queue|sign|user|extra")
    public void onHelp(CommandSender sender, @Default("arena") String category) {
        lang.sendMessage(sender, "COMMAND.duels.help." + category.toLowerCase(), "command", "duels");
    }

    @Subcommand("create")
    @Description("Creates an arena with given name.")
    public void onCreateArena(CommandSender sender, String name) {
        if (!StringUtil.isAlphanumeric(name)) {
            lang.sendMessage(sender, "ERROR.command.name-not-alphanumeric", "name", name);
            return;
        }

        if (!arenaManager.create(sender, name)) {
            lang.sendMessage(sender, "ERROR.arena.already-exists", "name", name);
            return;
        }

        lang.sendMessage(sender, "COMMAND.duels.create", "name", name);
    }

    @Subcommand("set")
    @CommandCompletion("@arenas 1|2")
    @Description("Sets the teleport position of an arena.")
    public void onSetArena(Player player, ArenaImpl arena, int position) {
        if (position <= 0 || position > 2) {
            lang.sendMessage(player, "ERROR.arena.invalid-position");
            return;
        }

        final Location location = player.getLocation().clone();
        arena.setPosition(player, position, location);
        lang.sendMessage(player, "COMMAND.duels.set", "position", position, "name", arena.getName(), "location", StringUtil.parse(location));
    }

    @Subcommand("delete")
    @CommandCompletion("@arenas")
    @Description("Deletes an arena.")
    public void onDeleteArena(CommandSender sender, ArenaImpl arena) {
        if (arena.isUsed()) {
            lang.sendMessage(sender, "ERROR.arena.delete-failure", "name", arena.getName());
            return;
        }

        arenaManager.remove(sender, arena);
        lang.sendMessage(sender, "COMMAND.duels.delete", "name", arena.getName());
    }

    @Subcommand("info")
    @CommandCompletion("@arenas")
    @Description("Displays information about the selected arena.")
    public void onInfoArena(CommandSender sender, ArenaImpl arena) {
        final String inUse = arena.isUsed() ? lang.getMessage("GENERAL.true") : lang.getMessage("GENERAL.false");
        final String disabled = arena.isDisabled() ? lang.getMessage("GENERAL.true") : lang.getMessage("GENERAL.false");
        final String kits = StringUtil.join(arena.getKits().stream().map(KitImpl::getName).collect(Collectors.toList()), ", ");
        final String positions = StringUtil.join(arena.getPositions().values().stream().map(StringUtil::parse).collect(Collectors.toList()), ", ");
        final String players = StringUtil.join(arena.getPlayers().stream().map(Player::getName).collect(Collectors.toList()), ", ");
        lang.sendMessage(sender, "COMMAND.duels.info", "name", arena.getName(), "in_use", inUse, "disabled", disabled, "kits",
                !kits.isEmpty() ? kits : lang.getMessage("GENERAL.none"), "positions", !positions.isEmpty() ? positions : lang.getMessage("GENERAL.none"), "players",
                !players.isEmpty() ? players : lang.getMessage("GENERAL.none"));
    }

    @Subcommand("toggle")
    @CommandCompletion("@arenas")
    @Description("Enables or disables an arena.")
    public void onToggleArena(CommandSender sender, ArenaImpl arena) {
        arena.setDisabled(sender, !arena.isDisabled());
        lang.sendMessage(sender, "COMMAND.duels.toggle", "name", arena.getName(), "state", arena.isDisabled() ? lang.getMessage("GENERAL.disabled") : lang.getMessage("GENERAL.enabled"));
    }

    @Subcommand("teleport|tp|goto")
    @CommandCompletion("@arenas 1|2")
    @Description("Teleports to an arena.")
    public void onTeleportArena(Player player, ArenaImpl arena, @Default("1") int position) {
        if (arena.getPositions().isEmpty()) {
            lang.sendMessage(player, "ERROR.arena.no-position-set", "name", arena.getName());
            return;
        }

        final Location location = arena.getPosition(position);

        if (location == null) {
            lang.sendMessage(player, "ERROR.arena.invalid-position");
            return;
        }
        
        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                lang.sendMessage(player, "COMMAND.duels.teleport", "name", arena.getName(), "position", position);
            } else {
                lang.sendMessage(player, "ERROR.teleport.failed", "name", arena.getName(), "position", position);
            }
        }).exceptionally(throwable -> {
            lang.sendMessage(player, "ERROR.teleport.failed", "name", arena.getName(), "position", position);
            return null;
        });
    }

    @Subcommand("disable")
    @CommandCompletion("@arenas")
    @Description("Disables an arena.")
    public void onDisableArena(CommandSender sender, ArenaImpl arena) {
        if (arena.isDisabled()) {
            lang.sendMessage(sender, "COMMAND.duels.already-disabled", "name", arena.getName());
            return;
        }

        arena.setDisabled(sender, true);
        lang.sendMessage(sender, "COMMAND.duels.disable", "name", arena.getName());
    }

    @Subcommand("enable")
    @CommandCompletion("@arenas")
    @Description("Enables an arena.")
    public void onEnableArena(CommandSender sender, ArenaImpl arena) {
        if (!arena.isDisabled()) {
            lang.sendMessage(sender, "COMMAND.duels.already-enabled", "name", arena.getName());
            return;
        }

        arena.setDisabled(sender, false);
        lang.sendMessage(sender, "COMMAND.duels.enable", "name", arena.getName());
    }

    @Subcommand("savekit")
    @CommandCompletion("@nothing")
    @Description("Saves a kit with given name.")
    public void onSaveKit(Player player, String name, @Optional String override) {
        if (!StringUtil.isAlphanumeric(name)) {
            lang.sendMessage(player, "ERROR.command.name-not-alphanumeric", "name", name);
            return;
        }

        if (kitManager.create(player, name, override != null && override.equals("-o")) == null) {
            lang.sendMessage(player, "ERROR.kit.already-exists", "name", name);
            return;
        }

        lang.sendMessage(player, "COMMAND.duels.save-kit", "name", name);
    }

    @Subcommand("loadkit")
    @CommandCompletion("@kits")
    @Description("Loads the selected kit to your inventory.")
    public void onLoadKit(Player player, KitImpl kit) {
        player.getInventory().clear();
        kit.equip(player);
        lang.sendMessage(player, "COMMAND.duels.load-kit", "name", kit.getName());
    }

    @Subcommand("deletekit")
    @CommandCompletion("@kits")
    @Description("Deletes a kit.")
    public void onDeleteKit(CommandSender sender, KitImpl kit) {
        if (kitManager.remove(sender, kit.getName()) == null) {
            lang.sendMessage(sender, "ERROR.kit.not-found", "name", kit.getName());
            return;
        }

        lang.sendMessage(sender, "COMMAND.duels.delete-kit", "name", kit.getName());
    }

    @Subcommand("setitem")
    @CommandCompletion("@kits")
    @Description("Sets the displayed item for selected kit.")
    public void onSetItem(Player player, KitImpl kit) {
        final ItemStack held = InventoryUtil.getItemInHand(player);

        if (held == null || held.getType() == Material.AIR) {
            lang.sendMessage(player, "ERROR.kit.empty-hand");
            return;
        }

        kit.setDisplayed(held.clone());
        kitManager.getGui().calculatePages();
        lang.sendMessage(player, "COMMAND.duels.set-item", "name", kit.getName());
    }

    @Subcommand("options")
    @CommandCompletion("@kits")
    @Description("Opens the options gui for kit.")
    public void onOptions(Player player, KitImpl kit) {
        plugin.getGuiListener().addGui(player, new OptionsGui(plugin, player, kit), true).open(player);
    }

    @Subcommand("bind")
    @CommandCompletion("@kits")
    @Description("Opens the arena bind gui for kit.")
    public void onBind(Player player, KitImpl kit) {
        plugin.getGuiListener().addGui(player, new BindGui(plugin, kit), true).open(player);
    }

    @Subcommand("createqueue|createq")
    @CommandCompletion("@nothing @kits")
    @Description("Creates a queue with given bet and kit.")
    public void onCreateQueue(CommandSender sender, int bet, @Optional String kitName) {
        KitImpl kit = null;
        if (kitName != null && !kitName.equals("-")) {
            kit = kitManager.get(kitName);
            if (kit == null) {
                lang.sendMessage(sender, "ERROR.kit.not-found", "name", kitName);
                return;
            }
        }

        final String finalKitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");

        if (queueManager.create(sender, kit, bet) == null) {
            lang.sendMessage(sender, "ERROR.queue.already-exists", "kit", finalKitName, "bet_amount", bet);
            return;
        }

        lang.sendMessage(sender, "COMMAND.duels.create-queue", "kit", finalKitName, "bet_amount", bet);
    }

    @Subcommand("deletequeue|delqueue|delq")
    @CommandCompletion("@nothing @kits")
    @Description("Deletes a queue.")
    public void onDeleteQueue(CommandSender sender, int bet, @Optional String kitName) {
        KitImpl kit = null;
        if (kitName != null && !kitName.equals("-")) {
            kit = kitManager.get(kitName);
            if (kit == null) {
                lang.sendMessage(sender, "ERROR.kit.not-found", "name", kitName);
                return;
            }
        }

        final String finalKitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");

        if (queueManager.remove(sender, kit, bet) == null) {
            lang.sendMessage(sender, "ERROR.queue.not-found", "bet_amount", bet, "kit", finalKitName);
            return;
        }

        lang.sendMessage(sender, "COMMAND.duels.delete-queue", "kit", finalKitName, "bet_amount", bet);
    }

    @Subcommand("addsign")
    @CommandCompletion("@nothing @kits")
    @Description("Creates a queue sign with given bet and kit.")
    public void onAddSign(Player player, int bet, @Optional String kitName) {
        final Sign sign = BlockUtil.getTargetBlock(player, Sign.class, 6);

        if (sign == null) {
            lang.sendMessage(player, "ERROR.sign.not-a-sign");
            return;
        }

        KitImpl kit = null;
        if (kitName != null && !kitName.equals("-")) {
            kit = kitManager.get(kitName);
            if (kit == null) {
                lang.sendMessage(player, "ERROR.kit.not-found", "name", kitName);
                return;
            }
        }

        final String finalKitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");
        final Queue queue = queueManager.get(kit, bet);

        if (queue == null) {
            lang.sendMessage(player, "ERROR.queue.not-found", "bet_amount", bet, "kit", finalKitName);
            return;
        }

        if (!queueSignManager.create(player, sign.getLocation(), queue)) {
            lang.sendMessage(player, "ERROR.sign.already-exists");
            return;
        }

        final Location location = sign.getLocation();
        lang.sendMessage(player, "COMMAND.duels.add-sign", "location", StringUtil.parse(location), "kit", finalKitName, "bet_amount", bet);
    }

    @Subcommand("deletesign|delsign")
    @Description("Deletes the queue sign you are looking at.")
    public void onDeleteSign(Player player) {
        final Sign sign = BlockUtil.getTargetBlock(player, Sign.class, 6);

        if (sign == null) {
            lang.sendMessage(player, "ERROR.sign.not-a-sign");
            return;
        }

        final QueueSignImpl queueSign = queueSignManager.remove(player, sign.getLocation());

        if (queueSign == null) {
            lang.sendMessage(player, "ERROR.sign.not-found");
            return;
        }

        sign.setType(Material.AIR);
        sign.update(true);

        final Location location = sign.getLocation();
        final Queue queue = queueSign.getQueue();
        final com.meteordevelopments.duels.api.kit.Kit kit = queue.getKit();
        final String kitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");
        lang.sendMessage(player, "COMMAND.duels.del-sign", "location", StringUtil.parse(location), "kit", kitName, "bet_amount", queue.getBet());
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

    @Subcommand("setlobby|setspawn")
    @Description("Sets duel lobby location.")
    public void onSetLobby(Player player) {
        if (!playerManager.setLobby(player)) {
            lang.sendMessage(player, "ERROR.command.lobby-save-failure");
            return;
        }

        lang.sendMessage(player, "COMMAND.duels.set-lobby");
    }

    @Subcommand("lobby")
    @Description("Teleports to duel lobby.")
    public void onLobby(Player player) {
        final Location lobbyLocation = playerManager.getLobby();
        
        player.teleportAsync(lobbyLocation).thenAccept(success -> {
            if (success) {
                lang.sendMessage(player, "COMMAND.duels.lobby");
            } else {
                lang.sendMessage(player, "ERROR.teleport.failed");
            }
        }).exceptionally(throwable -> {
            lang.sendMessage(player, "ERROR.teleport.failed");
            return null;
        });
    }

    @Subcommand("playsound")
    @CommandCompletion("@sounds")
    @Description("Plays the selected sound if defined.")
    public void onPlaySound(Player player, String soundName) {
        final Config.MessageSound sound = config.getSound(soundName);

        if (sound == null) {
            lang.sendMessage(player, "ERROR.sound.not-found", "name", soundName);
            return;
        }

        player.playSound(player.getLocation(), sound.getType(), sound.getVolume(), sound.getPitch());
    }

    @Subcommand("list|ls")
    @Description("Displays the list of all arenas, kits, queues, etc.")
    public void onList(CommandSender sender) {
        final List<String> arenas = new ArrayList<>();
        arenaManager.getArenasImpl().forEach(arena -> arenas.add("&" + getColor(arena) + arena.getName()));
        final String kits = StringUtil.join(kitManager.getKits().stream().map(Kit::getName).collect(Collectors.toList()), ", ");
        final String queues = StringUtil.join(queueManager.getQueues().stream().map(DQueue::toString).collect(Collectors.toList()), ", ");
        final String signs = StringUtil.join(queueSignManager.getSigns().stream().map(QueueSignImpl::toString).collect(Collectors.toList()), ", ");
        lang.sendMessage(sender, "COMMAND.duels.list",
                "arenas", !arenas.isEmpty() ? StringUtil.join(arenas, "&r, &r") : lang.getMessage("GENERAL.none"),
                "kits", !kits.isEmpty() ? kits : lang.getMessage("GENERAL.none"),
                "queues", !queues.isEmpty() ? queues : lang.getMessage("GENERAL.none"),
                "queue_signs", !signs.isEmpty() ? signs : lang.getMessage("GENERAL.none"),
                "lobby", StringUtil.parse(playerManager.getLobby()));
    }

    private String getColor(final ArenaImpl arena) {
        return arena.isDisabled() ? "4" : (arena.getPositions().size() < 2 ? "9" : arena.isUsed() ? "c" : "a");
    }
}

