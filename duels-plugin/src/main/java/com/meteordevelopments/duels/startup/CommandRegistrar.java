package com.meteordevelopments.duels.startup;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.api.command.SubCommand;
import com.meteordevelopments.duels.command.commands.RankCommand;
import com.meteordevelopments.duels.command.commands.SpectateCommand;
import com.meteordevelopments.duels.command.commands.duel.DuelCommand;
import com.meteordevelopments.duels.command.commands.duels.DuelsCommand;
import com.meteordevelopments.duels.command.commands.party.PartyCommand;
import com.meteordevelopments.duels.command.commands.queue.QueueCommand;
import com.meteordevelopments.duels.util.CC;
import com.meteordevelopments.duels.util.command.AbstractCommand;
import org.bukkit.command.CommandSender;

import java.util.*;

public class CommandRegistrar {
    
    private final DuelsPlugin plugin;
    private final Map<String, AbstractCommand<DuelsPlugin>> commands = new HashMap<>();
    private final List<QueuedSubCommand> queuedSubCommands = new ArrayList<>();
    
    private static class QueuedSubCommand {
        final String commandName;
        final SubCommand subCommand;
        
        QueuedSubCommand(String commandName, SubCommand subCommand) {
            this.commandName = commandName;
            this.subCommand = subCommand;
        }
    }
    
    public CommandRegistrar(DuelsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void registerCommands() {
        long start = System.currentTimeMillis();
        DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.startup.registering-commands"));
        
        registerCommand(new DuelCommand(plugin));
        registerCommand(new PartyCommand(plugin));
        registerCommand(new QueueCommand(plugin));
        registerCommand(new SpectateCommand(plugin));
        registerCommand(new DuelsCommand(plugin));
        registerCommand(new RankCommand(plugin));
        
        String timeString = CC.getTimeDifferenceAndColor(start, System.currentTimeMillis());
        DuelsPlugin.sendMessage(plugin.getLang().getMessage("SYSTEM.startup.commands-success", "time", timeString));
        
        // Process queued subcommands from extensions
        processQueuedSubCommands();
    }
    
    private void registerCommand(AbstractCommand<DuelsPlugin> command) {
        commands.put(command.getName().toLowerCase(), command);
        command.register();
    }
    
    public boolean registerSubCommand(String commandName, SubCommand subCommand) {
        Objects.requireNonNull(commandName, "command");
        Objects.requireNonNull(subCommand, "subCommand");

        final AbstractCommand<DuelsPlugin> result = commands.get(commandName.toLowerCase());

        if (result == null) {
            // Queue the subcommand for later registration
            queuedSubCommands.add(new QueuedSubCommand(commandName, subCommand));
            plugin.getLogger().info("Queued subcommand '" + subCommand.getName() + "' for command '" + commandName + "' (command not yet registered)");
            return true;
        }

        return registerSubCommandNow(commandName, subCommand, result);
    }
    
    private boolean registerSubCommandNow(String commandName, SubCommand subCommand, AbstractCommand<DuelsPlugin> command) {
        if (command.isChild(subCommand.getName().toLowerCase())) {
            plugin.getLogger().warning("Failed to register subcommand '" + subCommand.getName() + "': Subcommand already exists");
            return false;
        }

        command.child(new AbstractCommand<>(plugin, subCommand) {
            @Override
            protected void execute(final CommandSender sender, final String label, final String[] args) {
                subCommand.execute(sender, label, args);
            }
        });
        
        plugin.getLogger().info("Successfully registered subcommand '" + subCommand.getName() + "' for command '" + commandName + "'");
        return true;
    }
    
    private void processQueuedSubCommands() {
        if (queuedSubCommands.isEmpty()) {
            return;
        }
        
        plugin.getLogger().info("Processing " + queuedSubCommands.size() + " queued subcommand(s)...");
        
        Iterator<QueuedSubCommand> iterator = queuedSubCommands.iterator();
        while (iterator.hasNext()) {
            QueuedSubCommand queued = iterator.next();
            final AbstractCommand<DuelsPlugin> command = commands.get(queued.commandName.toLowerCase());
            
            if (command != null) {
                registerSubCommandNow(queued.commandName, queued.subCommand, command);
                iterator.remove();
            } else {
                plugin.getLogger().warning("Failed to register queued subcommand '" + queued.subCommand.getName() + "': Command '" + queued.commandName + "' still not found");
            }
        }
    }
    
    public void clearCommands() {
        commands.clear();
    }
}