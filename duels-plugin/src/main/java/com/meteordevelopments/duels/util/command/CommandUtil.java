package com.meteordevelopments.duels.util.command;

import com.meteordevelopments.duels.arena.ArenaImpl;
import com.meteordevelopments.duels.arena.ArenaManagerImpl;
import com.meteordevelopments.duels.config.Lang;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.kit.KitManagerImpl;
import com.meteordevelopments.duels.util.NumberUtil;
import com.meteordevelopments.duels.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public final class CommandUtil {

    private CommandUtil() {
    }

    @Nullable
    public static KitImpl parseAndValidateOptionalKit(String[] args, int startIndex, KitManagerImpl kitManager, 
                                                      Lang lang, CommandSender sender) {
        if (args[startIndex].equals("-")) {
            return null; // Valid null kit (no kit specified)
        }

        return parseAndValidateRequiredKit(args, startIndex, kitManager, lang, sender);
    }

    @Nullable
    public static KitImpl parseAndValidateRequiredKit(String[] args, int startIndex, KitManagerImpl kitManager,
                                                      Lang lang, CommandSender sender) {
        String name = StringUtil.join(args, " ", startIndex, args.length).replace("-", " ");
        KitImpl kit = kitManager.get(name);
        
        if (kit == null) {
            lang.sendMessage(sender, "ERROR.kit.not-found", "name", name);
        }
        
        return kit;
    }

    @Nullable
    public static ArenaImpl parseAndValidateArena(String[] args, int startIndex, ArenaManagerImpl arenaManager,
                                                  Lang lang, CommandSender sender) {
        String name = StringUtil.join(args, " ", startIndex, args.length).replace("-", " ");
        ArenaImpl arena = arenaManager.get(name);
        
        if (arena == null) {
            lang.sendMessage(sender, "ERROR.arena.not-found", "name", name);
        }
        
        return arena;
    }

    public static int parseBetAmount(String[] args, int betIndex) {
        return NumberUtil.parseInt(args[betIndex]).orElse(0);
    }

    public static String getKitDisplayName(KitImpl kit, Lang lang) {
        return kit != null ? kit.getName() : lang.getMessage("GENERAL.none");
    }

    public static List<String> getBetTabCompletion() {
        return Arrays.asList("0", "10", "50", "100", "500", "1000");
    }

    @Nullable
    public static Player validatePlayerTarget(String targetName, CommandSender sender, Lang lang) {
        Player target = Bukkit.getPlayerExact(targetName);
        
        if (target == null || (sender instanceof Player && !((Player) sender).canSee(target))) {
            lang.sendMessage(sender, "ERROR.player.not-found", "name", targetName);
            return null;
        }
        
        return target;
    }

    public static SettingsDisplay formatSettingsDisplay(com.meteordevelopments.duels.setting.Settings settings, Lang lang) {
        final String kit = settings.getKit() != null ? settings.getKit().getName() : lang.getMessage("GENERAL.not-selected");
        final String ownInventory = settings.isOwnInventory() ? lang.getMessage("GENERAL.enabled") : lang.getMessage("GENERAL.disabled");
        final String arena = settings.getArena() != null ? settings.getArena().getName() : lang.getMessage("GENERAL.random");
        return new SettingsDisplay(kit, ownInventory, arena);
    }

}
