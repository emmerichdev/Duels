package com.meteordevelopments.duels.api.kit;

import com.meteordevelopments.duels.api.event.kit.KitEquipEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Kit {

    @NotNull
    String getName();

    @NotNull
    ItemStack getDisplayed();

    boolean isUsePermission();

    void setUsePermission(final boolean usePermission);

    boolean isArenaSpecific();

    void setArenaSpecific(final boolean arenaSpecific);

    boolean equip(@NotNull final Player player);

    boolean isRemoved();
}
