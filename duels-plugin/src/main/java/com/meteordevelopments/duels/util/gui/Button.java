package com.meteordevelopments.duels.util.gui;

import com.meteordevelopments.duels.util.CC;
import com.meteordevelopments.duels.util.CommonItems;

import com.meteordevelopments.duels.util.inventory.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Button<P extends JavaPlugin> {

    protected final P plugin;
    @Getter
    @Setter
    private ItemStack displayed;

    public Button(final P plugin, final ItemStack displayed) {
        this.plugin = plugin;
        this.displayed = displayed;
    }

    protected void editMeta(final Consumer<ItemMeta> consumer) {
        final ItemMeta meta = getDisplayed().getItemMeta();
        consumer.accept(meta);
        getDisplayed().setItemMeta(meta);
    }

    protected void setDisplayName(final String name) {
        editMeta(meta -> meta.displayName(parseComponent(name)));
    }

    protected void setLore(final List<String> lore) {
        editMeta(meta -> meta.lore(lore.stream()
            .map(this::parseComponent)
            .collect(java.util.stream.Collectors.toList())));
    }

    private Component parseComponent(final String text) {
        return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().deserialize(CC.translate(text));
    }

    // Modern Paper-specific methods
    protected void setDisplayName(final Component component) {
        editMeta(meta -> meta.displayName(component));
    }

    protected void setLore(final Component... components) {
        editMeta(meta -> meta.lore(Arrays.asList(components)));
    }

    protected void setLoreComponents(final List<Component> components) {
        editMeta(meta -> meta.lore(components));
    }

    protected void playClickSound(final Player player) {
        playClickSound(player, Sound.Source.MASTER);
    }

    protected void playClickSound(final Player player, final Sound.Source source) {
        player.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, source, 0.5f, 1.0f));
    }

    protected void playErrorSound(final Player player) {
        player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_VILLAGER_NO, Sound.Source.MASTER, 0.5f, 1.0f));
    }

    protected void playSuccessSound(final Player player) {
        player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.MASTER, 0.5f, 1.0f));
    }

    protected void setLore(final String... lore) {
        setLore(Arrays.asList(lore));
    }

    protected void setOwner(final Player player) {
        if (CommonItems.equals(displayed, CommonItems.HEAD)) {
            editMeta(meta -> ((SkullMeta) meta).setOwningPlayer(player));
        }
    }

    protected void setGlow(final boolean glow) {
        // Normal golden apples do not have enchantment glint even with an enchantment applied, so we change the item type.
        if (displayed.getType().name().endsWith("GOLDEN_APPLE")) {
            final ItemStack item = glow ? CommonItems.ENCHANTED_GOLDEN_APPLE.clone() : ItemBuilder.of(Material.GOLDEN_APPLE).build();
            item.setItemMeta(getDisplayed().getItemMeta());
            setDisplayed(item);
            return;
        }

        editMeta(meta -> {
            if (glow) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeEnchant(Enchantment.UNBREAKING);
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        });
    }


    public void update(final Player player) {
    }

    public void onClick(final Player player) {
    }
}