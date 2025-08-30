package com.meteordevelopments.duels.util.compat;

import com.meteordevelopments.duels.util.CC;
import com.meteordevelopments.duels.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public final class Titles {

    private Titles() {
    }

    public static void send(final Player player, final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut) {
        final Component titleComponent = title != null ? 
            LegacyComponentSerializer.legacySection().deserialize(CC.translate(title)) : Component.empty();
        final Component subtitleComponent = subtitle != null ? 
            LegacyComponentSerializer.legacySection().deserialize(CC.translate(subtitle)) : Component.empty();
        
        final Title adventureTitle = Title.title(
            titleComponent,
            subtitleComponent,
            Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
            )
        );
        
        player.showTitle(adventureTitle);
    }
}
