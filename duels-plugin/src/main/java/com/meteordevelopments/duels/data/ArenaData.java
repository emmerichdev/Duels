package com.meteordevelopments.duels.data;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.arena.ArenaImpl;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ArenaData {

    @Getter
    private String name;
    private boolean disabled;
    private final Set<String> kits = new HashSet<>();
    private final Map<Integer, LocationData> positions = new HashMap<>();

    private ArenaData() {
    }

    public ArenaData(final ArenaImpl arena) {
        this.name = arena.getName();
        this.disabled = arena.isDisabled();
        arena.getKits().forEach(kit -> this.kits.add(kit.getName()));
        arena.getPositions().entrySet()
                .stream().filter(entry -> entry.getValue() != null && entry.getValue().getWorld() != null)
                .forEach(entry -> positions.put(entry.getKey(), LocationData.fromLocation(entry.getValue())));
    }

    public ArenaImpl toArena(final DuelsPlugin plugin) {
        final ArenaImpl arena = new ArenaImpl(plugin, name, disabled);
        kits.stream().map(name -> plugin.getKitManager().get(name)).filter(Objects::nonNull).forEach(kit -> arena.getKits().add(kit));
        positions.forEach((key, value) -> arena.getPositions().put(key, value.toLocation()));
        arena.refreshGui(arena.isAvailable());
        return arena;
    }
}
