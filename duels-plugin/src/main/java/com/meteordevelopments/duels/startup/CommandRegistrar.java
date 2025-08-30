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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandRegistrar {
    
    private final DuelsPlugin plugin;
    private final Map<String, AbstractCommand<DuelsPlugin>> commands = new HashMap<>();
    
    public CommandRegistrar(DuelsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void registerCommands() {
        long start = System.currentTimeMillis();
        DuelsPlugin.sendMessage("&eRegistering commands...");
        
        registerCommand(new DuelCommand(plugin));
        registerCommand(new PartyCommand(plugin));
        registerCommand(new QueueCommand(plugin));
        registerCommand(new SpectateCommand(plugin));
        registerCommand(new DuelsCommand(plugin));
        registerCommand(new RankCommand(plugin));
        
        DuelsPlugin.sendMessage("&dSuccessfully registered commands [" + CC.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + "&f]");
    }
    
    private void registerCommand(AbstractCommand<DuelsPlugin> command) {
        commands.put(command.getName().toLowerCase(), command);
        command.register();
    }
    
    public boolean registerSubCommand(String commandName, SubCommand subCommand) {
        Objects.requireNonNull(commandName, "command");
        Objects.requireNonNull(subCommand, "subCommand");

        final AbstractCommand<DuelsPlugin> result = commands.get(commandName.toLowerCase());

        if (result == null || result.isChild(subCommand.getName().toLowerCase())) {
            return false;
        }

        result.child(new AbstractCommand<>(plugin, subCommand) {
            @Override
            protected void execute(final CommandSender sender, final String label, final String[] args) {
                subCommand.execute(sender, label, args);
            }
        });
        return true;
    }
    
    public void clearCommands() {
        commands.clear();
    }
}