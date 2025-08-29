package com.meteordevelopments.duels.util.command;

import com.meteordevelopments.duels.arena.ArenaImpl;
import com.meteordevelopments.duels.arena.ArenaManagerImpl;
import com.meteordevelopments.duels.config.Lang;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.kit.KitManagerImpl;
import com.meteordevelopments.duels.util.NumberUtil;
import com.meteordevelopments.duels.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public final class CommandUtil {

    private CommandUtil() {
    }

    /**
     * Parses and validates a kit name from command arguments (allows null/no kit)
     * @param args Command arguments
     * @param startIndex Starting index for kit name in args
     * @param kitManager Kit manager instance
     * @param lang Language manager instance
     * @param sender Command sender for error messages
     * @return KitImpl if valid, null if no kit specified or invalid (error message sent for invalid)
     */
    @Nullable
    public static KitImpl parseAndValidateOptionalKit(String[] args, int startIndex, KitManagerImpl kitManager, 
                                                      Lang lang, CommandSender sender) {
        if (args[startIndex].equals("-")) {
            return null; // Valid null kit (no kit specified)
        }

        return parseAndValidateRequiredKit(args, startIndex, kitManager, lang, sender);
    }

    /**
     * Parses and validates a kit name from command arguments (requires valid kit)
     * @param args Command arguments
     * @param startIndex Starting index for kit name in args
     * @param kitManager Kit manager instance
     * @param lang Language manager instance
     * @param sender Command sender for error messages
     * @return KitImpl if valid, null if invalid (error message already sent)
     */
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

    /**
     * Parses and validates an arena name from command arguments
     * @param args Command arguments
     * @param startIndex Starting index for arena name in args
     * @param arenaManager Arena manager instance
     * @param lang Language manager instance
     * @param sender Command sender for error messages
     * @return ArenaImpl if valid, null if invalid (error message already sent)
     */
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

    /**
     * Parses bet amount from command arguments with default value
     * @param args Command arguments
     * @param betIndex Index of bet argument
     * @return Parsed bet amount or 0 if invalid
     */
    public static int parseBetAmount(String[] args, int betIndex) {
        return NumberUtil.parseInt(args[betIndex]).orElse(0);
    }

    /**
     * Gets kit name for display purposes
     * @param kit Kit instance (can be null)
     * @param lang Language manager for fallback message
     * @return Display name for the kit
     */
    public static String getKitDisplayName(KitImpl kit, Lang lang) {
        return kit != null ? kit.getName() : lang.getMessage("GENERAL.none");
    }

    /**
     * Standard bet amount tab completion options
     * @return List of common bet amounts
     */
    public static List<String> getBetTabCompletion() {
        return Arrays.asList("0", "10", "50", "100", "500", "1000");
    }

    /**
     * Validates that a player target exists and is visible
     * @param targetName Target player name
     * @param sender Command sender
     * @param lang Language manager
     * @return Player if valid, null if invalid (error message sent)
     */
    @Nullable
    public static org.bukkit.entity.Player validatePlayerTarget(String targetName, CommandSender sender, Lang lang) {
        org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayerExact(targetName);
        
        if (target == null || (sender instanceof org.bukkit.entity.Player && !((org.bukkit.entity.Player) sender).canSee(target))) {
            lang.sendMessage(sender, "ERROR.player.not-found", "name", targetName);
            return null;
        }
        
        return target;
    }

    /**
     * Formats settings display strings for consistency
     * @param settings Settings object
     * @param lang Language manager
     * @return SettingsDisplay object with formatted strings
     */
    public static SettingsDisplay formatSettingsDisplay(com.meteordevelopments.duels.setting.Settings settings, Lang lang) {
        final String kit = settings.getKit() != null ? settings.getKit().getName() : lang.getMessage("GENERAL.not-selected");
        final String ownInventory = settings.isOwnInventory() ? lang.getMessage("GENERAL.enabled") : lang.getMessage("GENERAL.disabled");
        final String arena = settings.getArena() != null ? settings.getArena().getName() : lang.getMessage("GENERAL.random");
        return new SettingsDisplay(kit, ownInventory, arena);
    }

}
