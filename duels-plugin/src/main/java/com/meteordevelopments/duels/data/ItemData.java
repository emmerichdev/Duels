package com.meteordevelopments.duels.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.meteordevelopments.duels.util.EnumUtil;
import com.meteordevelopments.duels.util.ItemIdentifiers;
import com.meteordevelopments.duels.util.inventory.ItemBuilder;
import com.meteordevelopments.duels.util.inventory.ItemUtil;
import com.meteordevelopments.duels.util.json.DefaultBasedDeserializer;
import com.meteordevelopments.duels.util.yaml.YamlUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ItemData {

    private Map<String, Object> item;

    private ItemData() {
    }

    private ItemData(ItemStack item) {
        item = ItemIdentifiers.removeIdentifier(item);
        final String dumped = YamlUtil.bukkitYamlDump(item);
        this.item = YamlUtil.yamlLoad(dumped);
    }

    public static ItemData fromItemStack(final ItemStack item) {
        return new ItemData(item);
    }

    @SuppressWarnings("unchecked")
    private static void patchItemFlags(final Map<String, Object> item) {
        final Object meta = item.get("meta");

        if (meta instanceof Map) {
            final Map<String, Object> metaAsMap = (Map<String, Object>) meta;
            final Object itemFlags = metaAsMap.get("ItemFlags");

            if (itemFlags instanceof Iterable) {
                metaAsMap.put("ItemFlags", StreamSupport.stream(((Iterable<?>) itemFlags).spliterator(), false).map(Object::toString).collect(Collectors.toSet()));
            }
        }
    }

    public ItemStack toItemStack(final boolean kitItem) {
        if (item == null || item.isEmpty()) {
            return null;
        }

        final String dumped = YamlUtil.yamlDump(item);
        ItemStack item = YamlUtil.bukkitYamlLoadAs(dumped, ItemStack.class);
        return kitItem ? ItemIdentifiers.addIdentifier(item) : item;
    }

    public ItemStack toItemStack() {
        return toItemStack(true);
    }

    public static class ItemDataDeserializer extends DefaultBasedDeserializer<ItemData> {

        private static boolean checkOldJson = true;

        public ItemDataDeserializer(final JsonDeserializer<?> defaultDeserializer) {
            super(ItemData.class, defaultDeserializer);
        }

        @Override
        public ItemData deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
            JsonNode node = null;
            JsonParser actual = parser;


            if (checkOldJson) {
                node = parser.readValueAsTree();
                actual = parser.getCodec().treeAsTokens(node);
                actual.nextToken();
            }

            ItemData data = (ItemData) defaultDeserializer.deserialize(actual, context);

            if (data.item != null) {
                // If an item was successfully parsed to new json, disable old json check (assume kit file is in new json format) to reduce overhead.
                checkOldJson = false;
            } else if (node != null) {
                if (!node.isObject()) {
                    return null;
                }

                if (node.has("serializedItem")) {
                    return new ItemData(ItemUtil.itemFrom64(node.get("serializedItem").textValue()));
                }

                final String material = node.get("material").textValue();
                final int amount = node.has("amount") ? node.get("amount").intValue() : 1;
                final short damage = node.has("data") ? node.get("data").shortValue() : 0;
                final Material type = Material.getMaterial(material);

                if (type == null) {
                    return null;
                }

                final ItemBuilder builder = ItemBuilder.of(type, amount, damage);

                if (node.has("displayName")) {
                    builder.name(node.get("displayName").textValue());
                }

                if (node.has("lore")) {
                    builder.lore(StreamSupport.stream(node.get("lore").spliterator(), false).map(JsonNode::textValue).collect(Collectors.toList()));
                }

                if (node.has("enchantments")) {
                    final JsonNode enchantments = node.get("enchantments");

                    StreamSupport.stream(Spliterators.spliteratorUnknownSize(enchantments.fieldNames(), 0), false).forEach(entry -> {
                        final Enchantment enchantment = Enchantment.getByName(entry);

                        if (enchantment == null) {
                            return;
                        }

                        builder.enchant(enchantment, enchantments.get(entry).asInt());
                    });
                }

                if (node.has("flags")) {
                    final JsonNode flags = node.get("flags");
                    StreamSupport.stream(flags.spliterator(), false).forEach(flagNode -> {
                        final ItemFlag flag = EnumUtil.getByName(flagNode.textValue(), ItemFlag.class);

                        if (flag == null) {
                            return;
                        }

                        builder.editMeta(meta -> meta.addItemFlags(flag));
                    });
                }

                if (node.has("unbreakable") && node.get("unbreakable").booleanValue()) {
                    builder.unbreakable();
                }

                if (node.has("owner")) {
                    builder.head(node.get("owner").textValue());
                }

                if (node.has("color")) {
                    builder.leatherArmorColor(node.get("color").textValue());
                }

                if (node.has("effects")) {
                    final JsonNode effects = node.get("effects");
                    builder.editMeta(meta -> {
                        final PotionMeta potionMeta = (PotionMeta) meta;

                        StreamSupport.stream(Spliterators.spliteratorUnknownSize(effects.fieldNames(), 0), false).forEach(entry -> {
                            final String[] split = effects.get(entry).textValue().split("-");
                            final int duration = Integer.parseInt(split[0]);
                            final int amplifier = Integer.parseInt(split[1]);
                            final PotionEffectType effectType = PotionEffectType.getByName(entry);

                            if (effectType == null) {
                                return;
                            }

                            potionMeta.addCustomEffect(new PotionEffect(effectType, duration, amplifier), true);
                        });
                    });
                }

                if (node.has("itemData")) {
                    final List<String> args = Arrays.asList(node.get("itemData").textValue().split("-"));
                    final PotionType potionType = EnumUtil.getByName(args.getFirst(), PotionType.class);

                    if (potionType != null) {
                        builder.potion(potionType, args.contains("extended"), args.contains("strong"));
                    }
                }

                if (node.has("attributeModifiers")) {
                    final JsonNode attributes = node.get("attributeModifiers");
                    StreamSupport.stream(attributes.spliterator(), false).forEach(attributeNode -> builder.attribute(
                            attributeNode.get("name").textValue(),
                            attributeNode.get("operation").intValue(),
                            attributeNode.get("amount").doubleValue(),
                            attributeNode.has("slot") ? attributeNode.get("slot").textValue() : null
                    ));
                }

                return new ItemData(builder.build());
            }

            return data;
        }
    }
}