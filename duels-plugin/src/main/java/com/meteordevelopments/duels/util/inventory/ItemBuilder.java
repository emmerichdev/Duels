package com.meteordevelopments.duels.util.inventory;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.util.CC;
import com.meteordevelopments.duels.util.EnumUtil;
import com.meteordevelopments.duels.util.StringUtil;
import com.meteordevelopments.duels.util.CommonItems;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public record ItemBuilder(ItemStack result) {

    private ItemBuilder(final Material type, final int amount, final short durability) {
        this(new ItemStack(type, amount));
        CommonItems.setDurability(result, durability);
    }

    private ItemBuilder(final String type, final int amount, final short durability) {
        this(validateMaterial(type), amount, durability);
    }
    
    private static Material validateMaterial(final String type) {
        final Material material = Material.matchMaterial(type);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material type: " + type);
        }
        return material;
    }

    public static ItemBuilder of(final Material type) {
        return of(type, 1);
    }

    public static ItemBuilder of(final Material type, final int amount) {
        return of(type, amount, (short) 0);
    }

    public static ItemBuilder of(final Material type, final int amount, final short durability) {
        return new ItemBuilder(type, amount, durability);
    }

    public static ItemBuilder of(final String type, final int amount, final short durability) {
        return new ItemBuilder(type, amount, durability);
    }

    public static ItemBuilder of(final ItemStack item) {
        return new ItemBuilder(item);
    }

    public ItemBuilder editMeta(final Consumer<ItemMeta> consumer) {
        final ItemMeta meta = result.getItemMeta();
        consumer.accept(meta);
        result.setItemMeta(meta);
        return this;
    }

    public ItemBuilder name(final String name) {
        return editMeta(meta -> meta.displayName(
            LegacyComponentSerializer.legacySection().deserialize(CC.translate(name))));
    }

    public ItemBuilder lore(final List<String> lore) {
        return editMeta(meta -> meta.lore(lore.stream()
            .map(CC::translate)
            .map(line -> LegacyComponentSerializer.legacySection().deserialize(line))
            .collect(Collectors.toList())));
    }

    public ItemBuilder lore(final String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder enchant(final Enchantment enchantment, final int level) {
        result.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder unbreakable() {
        editMeta(meta -> {
            try {
                // Use reflection for compatibility with pre-1.11 servers
                Method setUnbreakable = meta.getClass().getMethod("setUnbreakable", boolean.class);
                setUnbreakable.invoke(meta, true);
            } catch (Exception ignored) {
                // Silently ignore on older servers that don't support unbreakable
            }
        });
        return this;
    }

    public ItemBuilder head(final String owner) {
        editMeta(meta -> {
            if (owner != null && CommonItems.equals(CommonItems.HEAD, result) && meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
            }
        });
        return this;
    }

    public ItemBuilder leatherArmorColor(final String color) {
        editMeta(meta -> {
            if (!(meta instanceof LeatherArmorMeta leatherArmorMeta)) {
                return;
            }

            if (color != null) {
                try {
                    leatherArmorMeta.setColor(DyeColor.valueOf(color).getColor());
                } catch (IllegalArgumentException ex) {
                    // Invalid color name, silently ignore
                }
            }
        });
        return this;
    }

    public ItemBuilder potion(final PotionType type, final boolean extended, final boolean upgraded) {
        if (result == null) {
            return this;
        }
        
        final ItemMeta itemMeta = result.getItemMeta();
        if (!(itemMeta instanceof PotionMeta meta)) {
            return this;
        }

        // Compute target PotionType based on extended/upgraded flags
        PotionType targetType = type;
        if ((upgraded || extended) && type != null) {
            final String typeName = type.name();
            if (!typeName.startsWith("LONG_") && !typeName.startsWith("STRONG_")) {
                String prefix = upgraded ? "STRONG_" : "LONG_";
                String variantName = prefix + typeName;
                
                PotionType variant = EnumUtil.getByName(variantName, PotionType.class);
                if (variant != null) {
                    targetType = variant;
                }
                // If lookup returns null, fall back to the original type (already set)
            }
        }
        
        // Only set if we have a valid target type
        if (targetType != null) {
            meta.setBasePotionType(targetType);
            result.setItemMeta(meta);
        }
        
        return this;
    }

    public ItemBuilder attribute(final String name, final int operation, final double amount, final String slotName) {
        editMeta(meta -> {
            final Attribute attribute = EnumUtil.getByName(attributeNameToEnum(name), Attribute.class);

            if (attribute == null) {
                return;
            }

            final AttributeModifier modifier;

            // Validate operation index
            if (operation < 0 || operation >= AttributeModifier.Operation.values().length) {
                return;
            }

            // Create NamespacedKey using plugin instance and include slot group if available
            String keyName = name.toLowerCase().replace(" ", "_");
            if (slotName != null) {
                final EquipmentSlot slot = EnumUtil.getByName(slotName, EquipmentSlot.class);
                if (slot == null) {
                    return;
                }
                // Use slot name instead of unstable getGroup() API
                keyName += "_" + slot.name().toLowerCase();
                NamespacedKey key = new NamespacedKey(DuelsPlugin.getInstance(), keyName);
                modifier = new AttributeModifier(key, amount, AttributeModifier.Operation.values()[operation]);
            } else {
                NamespacedKey key = new NamespacedKey(DuelsPlugin.getInstance(), keyName);
                modifier = new AttributeModifier(key, amount, AttributeModifier.Operation.values()[operation]);
            }

            meta.addAttributeModifier(attribute, modifier);
        });
        return this;
    }

    private String attributeNameToEnum(String name) {
        int len = name.length();
        int capitalLetterIndex = -1;

        for (int i = 0; i < len; i++) {
            if (Character.isUpperCase(name.charAt(i))) {
                capitalLetterIndex = i;
                break;
            }
        }

        if (capitalLetterIndex != -1) {
            name = name.substring(0, capitalLetterIndex) + "_" + name.substring(capitalLetterIndex);
        }

        return name.replace(".", "_").toUpperCase();
    }

    public ItemStack build() {
        return result;
    }
}