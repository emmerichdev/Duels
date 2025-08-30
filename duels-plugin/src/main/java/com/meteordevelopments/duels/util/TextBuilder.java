package com.meteordevelopments.duels.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

public final class TextBuilder {

    private Component component = Component.empty();

    private TextBuilder(final String base,
                        final ClickEvent.Action clickAction, final String clickValue,
                        final String hoverValue
    ) {
        if (base == null) {
            return;
        }

        component = LegacyComponentSerializer.legacyAmpersand().deserialize(base);
        
        if (clickValue != null) {
            component = component.clickEvent(createClickEvent(clickAction, clickValue));
        }

        if (hoverValue != null) {
            component = component.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(hoverValue)));
        }
    }

    public static TextBuilder of(final String base,
                                 final ClickEvent.Action clickAction, final String clickValue,
                                 final String hoverValue
    ) {
        return new TextBuilder(base, clickAction, clickValue, hoverValue);
    }

    public static TextBuilder of(final String base) {
        return of(base, null, null, null);
    }

    public TextBuilder add(final String text) {
        if (text == null) {
            return this;
        }

        component = component.append(LegacyComponentSerializer.legacyAmpersand().deserialize(text));
        return this;
    }

    public TextBuilder add(final String text, final ClickEvent.Action action, final String value) {
        if (text == null || value == null) {
            return this;
        }

        Component textComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(text)
                .clickEvent(createClickEvent(action, value));
        component = component.append(textComponent);
        return this;
    }

    public TextBuilder add(final String text, final String hoverValue) {
        if (text == null || hoverValue == null) {
            return this;
        }

        Component textComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(text)
                .hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(hoverValue)));
        component = component.append(textComponent);
        return this;
    }

    public TextBuilder add(final String text,
                           final ClickEvent.Action clickAction, final String clickValue,
                           final String hoverValue
    ) {
        if (text == null) {
            return this;
        }

        Component textComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        
        if (clickValue != null) {
            textComponent = textComponent.clickEvent(createClickEvent(clickAction, clickValue));
        }

        if (hoverValue != null) {
            textComponent = textComponent.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(hoverValue)));
        }

        component = component.append(textComponent);
        return this;
    }

    public void setClickEvent(final ClickEvent.Action action, final String value) {
        if (value == null) {
            return;
        }

        component = component.clickEvent(createClickEvent(action, value));
    }

    public TextBuilder setHoverEvent(final String value) {
        if (value == null) {
            return this;
        }

        component = component.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(value)));
        return this;
    }

    public void send(final Collection<Player> players) {
        players.forEach(player -> {
            if (player.isOnline()) {
                player.sendMessage(component);
            }
        });
    }

    public void send(final Player... players) {
        send(Arrays.asList(players));
    }

    private static ClickEvent createClickEvent(final ClickEvent.Action action, final String value) {
        return switch (action) {
            case OPEN_URL -> ClickEvent.openUrl(value);
            case RUN_COMMAND -> ClickEvent.runCommand(value);
            case SUGGEST_COMMAND -> ClickEvent.suggestCommand(value);
            case CHANGE_PAGE -> {
                try {
                    yield ClickEvent.changePage(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    // If the value isn't a valid integer, fall back to suggesting it as a command
                    yield ClickEvent.suggestCommand(value);
                }
            }
            case COPY_TO_CLIPBOARD -> ClickEvent.copyToClipboard(value);
            default -> // Fallback for any unknown or future actions
                    ClickEvent.suggestCommand(value);
        };
    }
}
