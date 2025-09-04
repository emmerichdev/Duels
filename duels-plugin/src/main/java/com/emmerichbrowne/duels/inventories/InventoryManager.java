package com.emmerichbrowne.duels.inventories;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.gui.inventory.InventoryGui;
import com.emmerichbrowne.duels.match.DuelMatch;
import com.emmerichbrowne.duels.util.CC;
import com.emmerichbrowne.duels.util.Loadable;
import com.emmerichbrowne.duels.util.TextBuilder;
import com.emmerichbrowne.duels.util.gui.GuiListener;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.util.*;

public class InventoryManager implements Loadable {

    private final DuelsPlugin plugin;
    private final GuiListener<DuelsPlugin> guiListener;
    private final Map<UUID, InventoryGui> inventories = new HashMap<>();
    private final Config config;
    private final Lang lang;

    private ScheduledTask expireTask;

    public InventoryManager(final DuelsPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.lang = plugin.getLang();
        this.guiListener = plugin.getGuiListener();
    }

    @Override
    public void handleLoad() {
        this.expireTask = plugin.doSyncRepeat(() -> {
            final long now = System.currentTimeMillis();

            inventories.entrySet().removeIf(entry -> {
                if (now - entry.getValue().getCreation() >= 1000L * 60 * 5) {
                    guiListener.removeGui(entry.getValue());
                    return true;
                }

                return false;
            });
        }, 20L, 20L * 5);
    }

    @Override
    public void handleUnload() {
        plugin.cancelTask(expireTask);
        inventories.clear();
    }

    public InventoryGui get(final UUID uuid) {
        return inventories.get(uuid);
    }

    public void create(final Player player, final boolean dead) {
        // Remove previously existing gui
        InventoryGui gui = inventories.remove(player.getUniqueId());

        if (gui != null) {
            guiListener.removeGui(gui);
        }

        gui = new InventoryGui(plugin, player, dead);
        guiListener.addGui(gui);
        inventories.put(player.getUniqueId(), gui);
    }

    public void handleMatchEnd(final DuelMatch match) {
        if (!config.isDisplayInventories()) {
            return;
        }

        String color = lang.getMessage("DUEL.inventories.name-color");
        final TextBuilder builder = TextBuilder.of(lang.getMessage("DUEL.inventories.message"));
        final Set<Player> players = match.getAllPlayers();
        final Iterator<Player> iterator = players.iterator();

        while (iterator.hasNext()) {
            final Player player = iterator.next();
            builder.add(CC.translate(color + player.getName()), ClickEvent.Action.RUN_COMMAND, "/duel _ " + player.getUniqueId());

            if (iterator.hasNext()) {
                builder.add(CC.translate(color + ", "));
            }
        }

        builder.send(players);
    }

}
