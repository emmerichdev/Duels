package com.meteordevelopments.duels.util.compat;

import com.meteordevelopments.duels.util.Log;
import com.meteordevelopments.duels.util.reflect.ReflectionUtil;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;

public final class Inventories {

    private static final Field CB_INVENTORY;
    private static final Field CB_INVENTORY_TITLE;
    static {
        CB_INVENTORY = ReflectionUtil.getDeclaredField(ReflectionUtil.getCBClass("inventory.CraftInventory"), "inventory");
        CB_INVENTORY_TITLE = ReflectionUtil.getDeclaredField(ReflectionUtil.getCBClass("inventory.CraftInventoryCustom$MinecraftInventory"), "title");
    }

    private Inventories() {
    }

    public static void setTitle(final Inventory inventory, final String title) {
        try {
            CB_INVENTORY_TITLE.set(CB_INVENTORY.get(inventory), title);
        } catch (IllegalAccessException ex) {
            Log.error("Failed to set inventory title", ex);
        }
    }
}
