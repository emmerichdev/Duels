package com.emmerichbrowne.duels.menus.betting;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.duel.DuelManager;
import com.emmerichbrowne.duels.menus.betting.buttons.CancelButton;
import com.emmerichbrowne.duels.menus.betting.buttons.DetailsButton;
import com.emmerichbrowne.duels.menus.betting.buttons.HeadButton;
import com.emmerichbrowne.duels.menus.betting.buttons.StateButton;
import com.emmerichbrowne.duels.setting.Settings;
import com.emmerichbrowne.duels.util.CommonItems;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.util.*;

public class BettingMenu {

	private final DuelsPlugin plugin;
    private final DuelManager duelManager;
    private final Settings settings;
	private final Gui guiSender;
	private final Gui guiTarget;
	private final UUID senderId;
	private final UUID targetId;
    private boolean firstReady;
    private boolean secondReady;
    private boolean cancelWait;

    public BettingMenu(final DuelsPlugin plugin, final Settings settings, final Player sender, final Player target) {
		this.plugin = plugin;
        this.duelManager = plugin.getDuelManager();
        this.settings = settings;
		this.senderId = sender.getUniqueId();
		this.targetId = target.getUniqueId();

		final var title = LegacyComponentSerializer.legacySection().deserialize(plugin.getLang().getMessage("GUI.item-betting.title"));
		this.guiSender = Gui.gui().title(title).rows(6).create();
		this.guiTarget = Gui.gui().title(title).rows(6).create();

		// Cancel all by default; allow only defined interactions
		this.guiSender.setDefaultClickAction(e -> e.setCancelled(true));
		this.guiTarget.setDefaultClickAction(e -> e.setCancelled(true));

		setupStaticLayout(guiSender);
		setupStaticLayout(guiTarget);

		placeFunctionalItems(guiSender, sender, target);
		placeFunctionalItems(guiTarget, target, sender);
	}

	private void setupStaticLayout(final Gui gui) {
		// Colored spacers as before
		for (int s = 0; s < 3; s++) gui.setItem(s, new GuiItem(CommonItems.ORANGE_PANE.clone()));
		for (int s = 45; s < 48; s++) gui.setItem(s, new GuiItem(CommonItems.ORANGE_PANE.clone()));
		for (int s = 6; s < 9; s++) gui.setItem(s, new GuiItem(CommonItems.BLUE_PANE.clone()));
		for (int s = 51; s < 54; s++) gui.setItem(s, new GuiItem(CommonItems.BLUE_PANE.clone()));
	}

	private void placeFunctionalItems(final Gui gui, final Player self, final Player other) {
		// Ready state buttons and details
		final StateButton selfState = new StateButton(plugin, this, self);
		final StateButton otherState = new StateButton(plugin, this, other);
		final DetailsButton details = new DetailsButton(plugin, settings);
		final CancelButton cancel = new CancelButton(plugin);
		final HeadButton selfHead = new HeadButton(plugin, self);
		final HeadButton otherHead = new HeadButton(plugin, other);

		gui.setItem(3, selfState.toGuiItem(self));
		gui.setItem(4, details.toGuiItem(self));
		gui.setItem(5, otherState.toGuiItem(self));

		gui.setItem(48, new GuiItem(selfHead.getDisplayed().clone()));
		gui.setItem(50, new GuiItem(otherHead.getDisplayed().clone()));

		// Cancel button column down the middle (13, 22, 31, 40, 49)
		for (int i = 0; i < 5; i++) {
			final int slot = 13 + 9 * i;
			gui.setItem(slot, cancel.toGuiItem(self));
		}

		// Allow placing/removing only in player's section until ready
		gui.setDefaultTopClickAction(e -> {
			final Player clicker = (Player) e.getWhoClicked();
			final int slot = e.getSlot();
			if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
				e.setCancelled(true);
            return;
        }
			final boolean isSelf = clicker.getUniqueId().equals(self.getUniqueId());
			final boolean isReady = isReady(clicker);
			final boolean inOwnSection = isSlotInSection(slot, isSelf);
			if ((firstReady && secondReady) || (!inOwnSection && slot < 54) || (isReady && inOwnSection)) {
				e.setCancelled(true);
			}
		});
	}

	private boolean isSlotInSection(final int slot, final boolean firstSection) {
		// Sections: [9..12] x 4 rows and [14..17] x 4 rows
		if (slot > 53) return false;
		final int col = slot % 9;
		final int row = slot / 9;
		if (row < 1 || row > 4) return false;
		return firstSection ? col >= 0 && col <= 3 : col >= 6;
	}

	public boolean isReady(final Player player) {
		return player.getUniqueId().equals(senderId) ? firstReady : secondReady;
	}

	public void setReady(final Player player) {
		if (player.getUniqueId().equals(senderId)) firstReady = true; else secondReady = true;
		if (firstReady && secondReady) startWaitTask();
	}

	public void open(final Player sender, final Player target) {
		guiSender.open(sender);
		guiTarget.open(target);
    }

    private void startWaitTask() {
        new WaitTask().startTask();
    }

    private class WaitTask implements Runnable {
        private static final int SLOT_START = 13;
        private ScheduledTask task;
        private int counter;

        public void startTask() {
            task = DuelsPlugin.getMorePaperLib().scheduling().globalRegionalScheduler().runAtFixedRate(this, 10L, 20L);
        }

        @Override
        public void run() {
            if (cancelWait) {
                task.cancel();
                return;
            }

            if (counter < 5) {
                final int slot = SLOT_START + 9 * counter;
				final ItemStack green = CommonItems.GREEN_PANE.clone();
				guiSender.setItem(slot, new GuiItem(green.clone()));
				guiTarget.setItem(slot, new GuiItem(green.clone()));
                counter++;
                return;
            }
            task.cancel();

            final Player sender = Bukkit.getPlayer(senderId);
			final Player target = Bukkit.getPlayer(targetId);
			if (sender == null || target == null) return;

			safeClose(sender);
			safeClose(target);

            final Map<UUID, List<ItemStack>> items = new HashMap<>();
			items.put(sender.getUniqueId(), collectItemsFrom(guiSender, true));
			items.put(target.getUniqueId(), collectItemsFrom(guiTarget, false));

            duelManager.startMatch(sender, target, settings, items, null);
        }

		private void safeClose(final Player player) {
			DuelsPlugin.getMorePaperLib().scheduling().entitySpecificScheduler(player).run((Runnable) player::closeInventory, () -> {});
		}

		private List<ItemStack> collectItemsFrom(final Gui gui, final boolean first) {
			final List<ItemStack> result = new ArrayList<>();
			for (int row = 1; row <= 4; row++) {
				for (int col = first ? 0 : 6; col <= (first ? 3 : 8); col++) {
					final int slot = row * 9 + col;
					final ItemStack item = gui.getInventory().getItem(slot);
					if (item != null && item.getType() != Material.AIR) {
						result.add(item);
						gui.getInventory().setItem(slot, null);
					}
				}
			}
			return result;
		}
    }
}
