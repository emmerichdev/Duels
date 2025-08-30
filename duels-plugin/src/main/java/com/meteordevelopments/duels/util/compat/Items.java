package com.meteordevelopments.duels.util.compat;

import com.meteordevelopments.duels.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public final class Items {

    public static final ItemStack ORANGE_PANE;
    public static final ItemStack BLUE_PANE;
    public static final ItemStack RED_PANE;
    public static final ItemStack GRAY_PANE;
    public static final ItemStack WHITE_PANE;
    public static final ItemStack GREEN_PANE;
    public static final ItemStack HEAD;
    public static final Material SKELETON_HEAD;
    public static final ItemStack OFF;
    public static final ItemStack ON;
    public static final Material MUSHROOM_SOUP;
    public static final Material EMPTY_MAP;
    public static final Material SIGN;
    public static final ItemStack HEAL_SPLASH_POTION;
    public static final ItemStack WATER_BREATHING_POTION;
    public static final ItemStack ENCHANTED_GOLDEN_APPLE;

    static {
        ORANGE_PANE = ItemBuilder.of(Material.ORANGE_STAINED_GLASS_PANE).name(" ").build();
        BLUE_PANE = ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").build();
        RED_PANE = ItemBuilder.of(Material.RED_STAINED_GLASS_PANE).build();
        GRAY_PANE = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        WHITE_PANE = ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").build();
        GREEN_PANE = ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE).build();
        HEAD = ItemBuilder.of(Material.PLAYER_HEAD).build();
        SKELETON_HEAD = Material.SKELETON_SKULL;
        OFF = ItemBuilder.of(Material.GRAY_DYE).build();
        ON = ItemBuilder.of(Material.LIME_DYE).build();
        MUSHROOM_SOUP = Material.MUSHROOM_STEW;
        EMPTY_MAP = Material.MAP;
        SIGN = Material.OAK_SIGN;
        HEAL_SPLASH_POTION = ItemBuilder.of(Material.SPLASH_POTION).potion(PotionType.HEALING, false, true).build();
        WATER_BREATHING_POTION = ItemBuilder.of(Material.POTION).potion(PotionType.WATER_BREATHING, false, false).build();
        ENCHANTED_GOLDEN_APPLE = ItemBuilder.of(Material.ENCHANTED_GOLDEN_APPLE).build();
    }

    private Items() {
    }

    public static boolean equals(final ItemStack item, final ItemStack other) {
        return item.getType() == other.getType() && getDurability(item) == getDurability(other);
    }

    public static ItemStack from(final String type, final short data) {
        if (type.equalsIgnoreCase("STAINED_GLASS_PANE")) {
            return ItemBuilder.of(Panes.from(data)).name(" ").build();
        }

        return ItemBuilder.of(type, 1, data).name(" ").build();
    }

    public static short getDurability(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        return ((meta instanceof Damageable damageable)) ? (short) damageable.getDamage() : 0;
    }

    public static void setDurability(final ItemStack item, final short durability) {
        final ItemMeta meta = item.getItemMeta();

        if (meta instanceof Damageable damageable) {
            damageable.setDamage(durability);
            item.setItemMeta(meta);
        }
    }

    public static boolean isHealSplash(final ItemStack item) {
        if (item.getType() != Material.SPLASH_POTION) {
            return false;
        }

        final PotionMeta meta = (PotionMeta) item.getItemMeta();
        return meta != null && meta.getBasePotionType() == PotionType.HEALING;
    }
}