package com.meteordevelopments.duels.command.commands.duel.subcommands;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.Permissions;
import com.meteordevelopments.duels.command.BaseCommand;
import com.meteordevelopments.duels.util.StringUtil;
import com.meteordevelopments.duels.util.TextBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class VersionCommand extends BaseCommand {

    public VersionCommand(final DuelsPlugin plugin) {
        super(plugin, "version", null, null, Permissions.DUEL, 1, true, "v");
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final PluginDescriptionFile info = plugin.getDescription();
        final String authors = info.getAuthors().isEmpty() ? "unknown" : String.join(", ", info.getAuthors());
        final TextBuilder textBuilder = TextBuilder.of(StringUtil.color("&b" + info.getFullName() + " by " + authors + " &l[Click]"));
        
        final String website = info.getWebsite();
        if (website != null && !website.trim().isEmpty()) {
            textBuilder.setClickEvent(ClickEvent.Action.OPEN_URL, website);
        }
        
        textBuilder.send((Player) sender);
    }
}
