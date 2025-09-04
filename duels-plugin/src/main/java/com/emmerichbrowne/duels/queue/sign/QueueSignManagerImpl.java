package com.emmerichbrowne.duels.queue.sign;

import com.google.common.collect.Lists;
import com.emmerichbrowne.duels.DuelsPlugin;

import com.emmerichbrowne.duels.api.event.queue.sign.QueueSignCreateEvent;
import com.emmerichbrowne.duels.api.event.queue.sign.QueueSignRemoveEvent;
import com.emmerichbrowne.duels.api.queue.sign.QueueSign;
import com.emmerichbrowne.duels.api.queue.sign.QueueSignManager;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.data.QueueSignData;
import com.emmerichbrowne.duels.queue.Queue;
import com.emmerichbrowne.duels.queue.QueueManager;
import com.emmerichbrowne.duels.util.Loadable;
import com.emmerichbrowne.duels.util.Log;
import com.emmerichbrowne.duels.util.json.JsonUtil;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.io.IOException;
import java.util.*;

public class QueueSignManagerImpl implements Loadable, QueueSignManager, Listener {

    private static final String SIGNS_LOADED = "&2Loaded %s queue sign(s).";

    private final DuelsPlugin plugin;
    private final Lang lang;
    private final QueueManager queueManager;

    private final Map<Location, QueueSignImpl> signs = new HashMap<>();

    private ScheduledTask updateTask;

    public QueueSignManagerImpl(final DuelsPlugin plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.queueManager = plugin.getQueueManager();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void handleLoad() throws IOException {
        // Load signs from MongoDB instead of file
        try {
            final var mongo = plugin.getMongoService();
            if (mongo != null) {
                final var collection = mongo.collection("signs");
                final String serverId = resolveServerId();
                final Document filter = new Document("serverId", serverId);

                for (final Document doc : collection.find(filter)) {
                    final String json = doc.toJson();
                    final QueueSignData data = JsonUtil.getObjectMapper().readValue(json, QueueSignData.class);
                    if (data == null) { continue; }

                    final QueueSignImpl queueSign = data.toQueueSign(plugin);
                    if (queueSign == null) { continue; }

                    signs.put(queueSign.getLocation(), queueSign);
                }
            }
        } catch (Exception ex) {
            Log.error(this, ex.getMessage(), ex);
        }

        DuelsPlugin.sendMessage(String.format(SIGNS_LOADED, signs.size()));

        this.updateTask = plugin.doSyncRepeat(() -> signs.entrySet().removeIf(entry -> {
            entry.getValue().update();
            return entry.getValue().getQueue().isRemoved();
        }), 20L, 20L);
    }

    @Override
    public void handleUnload() {
        plugin.cancelTask(updateTask);
        signs.clear();
    }

    private void saveQueueSigns() {
        try {
            final var mongo = plugin.getMongoService();
            if (mongo != null) {
                final var collection = mongo.collection("signs");
                final java.util.List<com.mongodb.client.model.WriteModel<Document>> ops = new java.util.ArrayList<>();
                final String serverId = resolveServerId();
                for (final QueueSignImpl sign : signs.values()) {
                    if (sign.getQueue().isRemoved()) {
                        continue;
                    }
                    final QueueSignData data = new QueueSignData(sign);
                    final String json = JsonUtil.getObjectWriter().writeValueAsString(data);
                    final Document doc = Document.parse(json);
                    // scope by server
                    doc.put("serverId", serverId);
                    final String id = sign.getLocation().getWorld().getName() + ":" + sign.getLocation().getBlockX() + ":" + sign.getLocation().getBlockY() + ":" + sign.getLocation().getBlockZ() + ":" + serverId;
                    doc.put("_id", id);
                    final Document filter = new Document("_id", id);
                    ops.add(new ReplaceOneModel<>(filter, doc, new ReplaceOptions().upsert(true)));
                }
                if (!ops.isEmpty()) {
                    // run unordered bulk upsert off-thread
                    plugin.doAsync(() -> {
                        try {
                            collection.bulkWrite(ops, new BulkWriteOptions().ordered(false));
                        } catch (Exception ex) {
                            Log.error(this, "Failed to persist queue signs asynchronously", ex);
                        }
                    });
                }
            }
        } catch (Exception ex) {
            Log.error(this, ex.getMessage(), ex);
        }
    }

    private String resolveServerId() {
        final String configured = plugin.getDatabaseConfig() != null ? plugin.getDatabaseConfig().getServerId() : null;
        if (configured != null && !configured.trim().isEmpty()) {
            return configured.trim();
        }
        final int port = plugin.getServer().getPort();
        return port > 0 ? String.valueOf(port) : "default";
    }

    @Nullable
    @Override
    public QueueSignImpl get(@NotNull final Sign sign) {
        Objects.requireNonNull(sign, "sign");
        return get(sign.getLocation());
    }

    public QueueSignImpl get(final Location location) {
        return signs.get(location);
    }

    public boolean create(final Player creator, final Location location, final Queue queue) {
        if (get(location) != null) {
            return false;
        }

        final QueueSignImpl created;
        final String kitName = queue.getKit() != null ? queue.getKit().getName() : lang.getMessage("GENERAL.none");
        signs.put(location, created = new QueueSignImpl(location, lang.getMessage("SIGN.format", "kit", kitName, "bet_amount", queue.getBet()), queue));
        signs.values().stream().filter(sign -> sign.equals(created)).forEach(QueueSignImpl::update);
        saveQueueSigns();

        final QueueSignCreateEvent event = new QueueSignCreateEvent(creator, created);
        Bukkit.getPluginManager().callEvent(event);
        return true;
    }

    public QueueSignImpl remove(final Player source, final Location location) {
        final QueueSignImpl queueSign = signs.remove(location);

        if (queueSign == null) {
            return null;
        }

        queueSign.setRemoved(true);
        saveQueueSigns();

        final QueueSignRemoveEvent event = new QueueSignRemoveEvent(source, queueSign);
        Bukkit.getPluginManager().callEvent(event);
        return queueSign;
    }

    public Collection<QueueSignImpl> getSigns() {
        return signs.values();
    }

    @NotNull
    @Override
    public List<QueueSign> getQueueSigns() {
        return Lists.newArrayList(getSigns());
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof Sign)) {
            return;
        }

        Player player = event.getPlayer();
        QueueSignImpl sign = get(block.getLocation());

        if (sign != null && queueManager.queue(player, sign.getQueue())) {
            signs.values().stream()
                    .filter(queueSign -> queueSign.equals(sign))
                    .forEach(QueueSignImpl::update);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(final BlockBreakEvent event) {
        final Block block = event.getBlock();

        if (!(block.getState() instanceof Sign) || get(block.getLocation()) == null) {
            return;
        }

        final Player player = event.getPlayer();

        if (!player.hasPermission("duels.admin")) {
            lang.sendMessage(player, "ERROR.no-permission", "permission", "duels.admin");
            return;
        }

        lang.sendMessage(player, "ERROR.sign.cancel-break");
        event.setCancelled(true);
    }
}
