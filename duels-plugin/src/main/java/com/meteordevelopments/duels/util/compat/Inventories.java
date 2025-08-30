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
        if (CB_INVENTORY == null || CB_INVENTORY_TITLE == null) {
            return;
        }
        
        try {
            Object handle = CB_INVENTORY.get(inventory);
            if (handle == null) {
                return;
            }
            
            // Verify handle is of expected type before setting
            if (CB_INVENTORY_TITLE.getDeclaringClass().isInstance(handle)) {
                CB_INVENTORY_TITLE.set(handle, title);
            }
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            Log.error("Failed to set inventory title: " + ex.getMessage(), ex);
        }
    }
}
