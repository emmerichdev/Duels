package com.meteordevelopments.duels.api.command;

import com.meteordevelopments.duels.api.Duels;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class SubCommand {

    private final String name, usage, description, permission;
    private final boolean playerOnly;
    private final int length;
    private final String[] aliases;

    public SubCommand(@NotNull final String name, @Nullable final String usage, @Nullable final String description, @Nullable final String permission,
                      final boolean playerOnly, final int length, final String... aliases) {
        Objects.requireNonNull(name, "name");
        this.name = name;
        this.usage = usage;
        this.description = description;
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.length = Math.max(length, 1);
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public int getLength() {
        return length;
    }

    public String[] getAliases() {
        return aliases;
    }

    public abstract void execute(final CommandSender sender, final String label, final String[] args);
}
