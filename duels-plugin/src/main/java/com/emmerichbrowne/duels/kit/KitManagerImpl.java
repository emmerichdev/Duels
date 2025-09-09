package com.emmerichbrowne.duels.kit;

import com.google.common.collect.Lists;
import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.api.event.kit.KitCreateEvent;
import com.emmerichbrowne.duels.api.event.kit.KitRemoveEvent;
import com.emmerichbrowne.duels.api.kit.Kit;
import com.emmerichbrowne.duels.api.kit.KitManager;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.data.KitData;
import com.emmerichbrowne.duels.util.Loadable;
import com.emmerichbrowne.duels.util.Log;
import com.emmerichbrowne.duels.util.StringUtil;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.menu.PaginatedMenu;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import com.emmerichbrowne.duels.util.json.JsonUtil;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class KitManagerImpl implements Loadable, KitManager {

    private static final String KITS_LOADED = "&2Loaded %s kit(s).";

    private final DuelsPlugin plugin;
    private final Config config;
    private final Lang lang;

    private final Map<String, KitImpl> kits = new LinkedHashMap<>();

    @Getter
    private PaginatedMenu gui;

    public KitManagerImpl(final DuelsPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.lang = plugin.getLang();
    }

    @Override
    public void handleLoad() throws IOException {
        gui = new PaginatedMenu(lang.getMessage("GUI.kit-selector.title"), config.getKitSelectorRows(), kits.values());
        gui.setSpaceFiller(CommonItems.from(config.getKitSelectorFillerType()));
        gui.setPrevButton(ItemBuilder.of(Material.PAPER).name(lang.getMessage("GUI.kit-selector.buttons.previous-page.name")).build());
        gui.setNextButton(ItemBuilder.of(Material.PAPER).name(lang.getMessage("GUI.kit-selector.buttons.next-page.name")).build());
        gui.setEmptyIndicator(ItemBuilder.of(Material.PAPER).name(lang.getMessage("GUI.kit-selector.buttons.empty.name")).build());

        // Load from MongoDB instead of file
        try {
            final var mongo = plugin.getMongoService();
            if (mongo != null) {
                final var collection = mongo.collection("kits");
                for (final Document doc : collection.find()) {
                    final String json = doc.toJson();
                    final KitData data = JsonUtil.getObjectMapper().readValue(json, KitData.class);
                    final String kitName = data != null ? data.getName() : null;
                    if (kitName != null && !kitName.isEmpty() && StringUtil.isAlphanumeric(kitName)) {
                        kits.put(kitName, data.toKit(plugin));
                    } else {
                        Log.warn(this, "Skipping invalid kit document (missing/invalid name)");
                    }
                }
            }
        } catch (Exception ex) {
            Log.error(this, ex.getMessage(), ex);
        }

        DuelsPlugin.sendMessage(String.format(KITS_LOADED, kits.size()));
        gui.calculatePages();
    }

    @Override
    public void handleUnload() {
        kits.clear();
    }

    void saveKits() {
        try {
            final var mongo = plugin.getMongoService();
            if (mongo != null) {
                final var collection = mongo.collection("kits");
                for (final Map.Entry<String, KitImpl> entry : kits.entrySet()) {
                    final KitData kd = KitData.fromKit(entry.getValue());
                    final String json = JsonUtil.getObjectWriter().writeValueAsString(kd);
                    final Document doc = Document.parse(json);
                    doc.put("_id", kd.getName());
                    collection.replaceOne(new Document("_id", kd.getName()), doc, new ReplaceOptions().upsert(true));
                }
            }
        } catch (Exception ex) {
            Log.error(this, ex.getMessage(), ex);
        }
    }

    @Nullable
    @Override
    public KitImpl get(@NotNull final String name) {
        Objects.requireNonNull(name, "name");
        return kits.get(name);
    }


    public KitImpl create(@NotNull final Player creator, @NotNull final String name, final boolean override) {
        Objects.requireNonNull(creator, "creator");
        Objects.requireNonNull(name, "name");

        if (!StringUtil.isAlphanumeric(name) || (!override && kits.containsKey(name))) {
            return null;
        }

        final KitImpl kit = new KitImpl(plugin, name, creator.getInventory());
        kits.put(name, kit);
        saveKits();

        final KitCreateEvent event = new KitCreateEvent(creator, kit);
        Bukkit.getPluginManager().callEvent(event);
        if (gui != null) gui.calculatePages();
        return kit;
    }

    @Nullable
    @Override
    public KitImpl create(@NotNull final Player creator, @NotNull final String name) {
        return create(creator, name, false);
    }

    @Nullable
    @Override
    public KitImpl remove(@Nullable CommandSender source, @NotNull final String name) {
        Objects.requireNonNull(name, "name");

        final KitImpl kit = kits.remove(name);

        if (kit == null) {
            return null;
        }

        kit.setRemoved(true);
        plugin.getArenaManager().clearBinds(kit);
        saveKits();
        plugin.doAsync(() -> {
            try {
                final var mongo = plugin.getMongoService();
                if (mongo != null) {
                    mongo.collection("kits").deleteOne(new Document("_id", name));
                }
            } catch (Exception ex) {
                Log.error(this, "Failed to finalize removal for kit: " + name, ex);
            }
        });

        final KitRemoveEvent event = new KitRemoveEvent(source, kit);
        Bukkit.getPluginManager().callEvent(event);
        if (gui != null) gui.calculatePages();
        return kit;
    }

    @Nullable
    @Override
    public KitImpl remove(@NotNull final String name) {
        return remove(null, name);
    }

    @NotNull
    @Override
    public List<Kit> getKits() {
        return Collections.unmodifiableList(Lists.newArrayList(kits.values()));
    }

    public List<String> getNames(final boolean nokit) {
        final List<String> names = new ArrayList<>(kits.keySet());

        if (nokit) {
            names.add("-"); // Special case: Change the nokit rating
        }

        return names;
    }
}