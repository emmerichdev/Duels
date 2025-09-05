package com.emmerichbrowne.duels.menus.bind.buttons;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaImpl;
import com.emmerichbrowne.duels.menus.BaseButton;
import com.emmerichbrowne.duels.menus.bind.BindMenu;
import com.emmerichbrowne.duels.kit.KitImpl;
import com.emmerichbrowne.duels.util.StringUtil;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class BindButton extends BaseButton {

    private final KitImpl kit;
    private final ArenaImpl arena;
    @Setter
    private BindMenu gui;

    public BindButton(final DuelsPlugin plugin, final KitImpl kit, final ArenaImpl arena) {
        super(plugin, ItemBuilder.of(CommonItems.EMPTY_MAP).build());
        this.kit = kit;
        this.arena = arena;
        setDisplayName(plugin.getLang().getMessage("GUI.options.bind.buttons.arena.name", "arena", arena.getName()));
        update();
    }

    private void update() {
        final boolean state = arena.isBound(kit);
        setGlow(state);

        String kits = StringUtil.join(arena.getKits().stream().map(KitImpl::getName).collect(Collectors.toList()), ", ");
        kits = kits.isEmpty() ? lang.getMessage("GENERAL.none") : kits;
        setLore(lang.getMessage("GUI.options.bind.buttons.arena.lore-" + (state ? "bound" : "not-bound"), "kits", kits).split("\n"));
    }

    @Override
    public void onClick(final Player player) {
        arena.bind(kit);
        update();
        if (gui != null) gui.refresh();
    }
}
