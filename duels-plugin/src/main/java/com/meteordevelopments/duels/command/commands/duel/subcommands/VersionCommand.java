package com.meteordevelopments.duels.command.commands.duel.subcommands;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.Permissions;
import com.meteordevelopments.duels.command.BaseCommand;
import com.meteordevelopments.duels.util.StringUtil;
import com.meteordevelopments.duels.util.TextBuilder;
import net.md_5.bungee.api.chat.ClickEvent.Action;
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
        final java.util.List<String> authors = info.getAuthors();
        final String authorsJoined = (authors != null && !authors.isEmpty()) ? String.join(", ", authors) : "unknown";
        final String website = info.getWebsite();

        TextBuilder builder = TextBuilder.of(StringUtil.color("&b" + info.getFullName() + " by " + authorsJoined + (website != null && !website.isBlank() ? " &l[Click]" : "")));
        if (website != null && !website.isBlank()) {
            builder.setClickEvent(Action.OPEN_URL, website);
        }
        builder.send((Player) sender);
    }
}
