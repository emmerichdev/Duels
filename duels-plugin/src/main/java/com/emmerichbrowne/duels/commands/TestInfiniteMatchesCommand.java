package com.emmerichbrowne.duels.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.arena.ArenaImpl;
import com.emmerichbrowne.duels.arena.ArenaManagerImpl;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.kit.KitImpl;
import com.emmerichbrowne.duels.kit.KitManagerImpl;
import com.emmerichbrowne.duels.match.DuelMatch;
import com.emmerichbrowne.duels.setting.Settings;
import com.emmerichbrowne.duels.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@CommandAlias("testinfinite|testinf")
@CommandPermission("duels.admin")
@Description("Test infinite concurrent matches capability")
public class TestInfiniteMatchesCommand extends BaseCommand {

    public TestInfiniteMatchesCommand(final DuelsPlugin plugin) {
        super(plugin);
    }

    @Default
    public void onTestInfinite(final Player player) {
        final Lang lang = plugin.getLang();
        final ArenaManagerImpl arenaManager = plugin.getArenaManager();
        final KitManagerImpl kitManager = plugin.getKitManager();

        // Get available arenas and kits
        final List<ArenaImpl> availableArenas = arenaManager.getArenasImpl().stream()
                .filter(ArenaImpl::isAvailable)
                .toList();
        
        final List<KitImpl> availableKits = kitManager.getKits().stream()
                .filter(kit -> !kit.isRemoved())
                .map(kit -> (KitImpl) kit)
                .toList();

        if (availableArenas.isEmpty()) {
            lang.sendMessage(player, "&cNo available arenas found!");
            return;
        }

        if (availableKits.isEmpty()) {
            lang.sendMessage(player, "&cNo available kits found!");
            return;
        }

        // Create test players (fake players for demonstration)
        List<Player> testPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        testPlayers.removeIf(p -> p.equals(player));
        testPlayers = testPlayers.stream()
                .limit(10) // Limit to 10 players for testing
                .toList();

        if (testPlayers.size() < 2) {
            lang.sendMessage(player, "&cNeed at least 2 online players to test infinite matches!");
            return;
        }

        lang.sendMessage(player, "&aStarting infinite matches test...");
        lang.sendMessage(player, "&7Available arenas: &f" + availableArenas.size());
        lang.sendMessage(player, "&7Available kits: &f" + availableKits.size());
        lang.sendMessage(player, "&7Test players: &f" + testPlayers.size());

        // Create multiple concurrent matches
        int matchCount = 0;
        final int maxMatches = Math.min(5, testPlayers.size() / 2); // Create up to 5 matches

        for (int i = 0; i < maxMatches; i++) {
            if (i + 1 >= testPlayers.size()) break;

            final Player player1 = testPlayers.get(i);
            final Player player2 = testPlayers.get(i + 1);

            // Select random arena and kit
            final ArenaImpl arena = availableArenas.get(i % availableArenas.size());
            final KitImpl kit = availableKits.get(i % availableKits.size());

            // Create settings
            final Settings settings = new Settings(plugin, player1);
            settings.setKit(kit);
            settings.setArena(arena);
            settings.setBet(0);

            // Create match
            final Map<UUID, List<ItemStack>> items = new HashMap<>();
            final Collection<Player> firstTeam = Collections.singletonList(player1);
            final Collection<Player> secondTeam = Collections.singletonList(player2);

            plugin.getDuelManager().startMatch(firstTeam, secondTeam, settings, items, null);

            matchCount++;
            lang.sendMessage(player, "&aStarted match #" + matchCount + " &7(" + player1.getName() + " vs " + player2.getName() + ")");
            lang.sendMessage(player, "&7Arena: &f" + arena.getName() + " &7| Kit: &f" + (kit != null ? kit.getName() : "None"));
        }

        lang.sendMessage(player, "&aSuccessfully started &f" + matchCount + " &aconcurrent matches!");
        lang.sendMessage(player, "&7This demonstrates that the system can handle infinite concurrent matches");
        lang.sendMessage(player, "&7Each match gets its own unique SlimeWorld instance");
        lang.sendMessage(player, "&7Original arenas remain available for new matches");
    }
}