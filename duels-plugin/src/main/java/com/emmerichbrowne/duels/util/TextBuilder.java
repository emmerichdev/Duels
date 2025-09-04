package com.emmerichbrowne.duels.util;

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

        String translatedBase = CC.translate(base);
        component = LegacyComponentSerializer.legacySection().deserialize(translatedBase);
        
        if (clickAction != null && clickValue != null) {
            component = component.clickEvent(createClickEvent(clickAction, clickValue));
        }

        if (hoverValue != null) {
            String translatedHover = CC.translate(hoverValue);
            component = component.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(translatedHover)));
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

        String translatedText = CC.translate(text);
        component = component.append(LegacyComponentSerializer.legacySection().deserialize(translatedText));
        return this;
    }

    public TextBuilder add(final String text, final ClickEvent.Action action, final String value) {
        if (text == null || action == null || value == null) {
            return this;
        }

        String translatedText = CC.translate(text);
        Component textComponent = LegacyComponentSerializer.legacySection().deserialize(translatedText)
                .clickEvent(createClickEvent(action, value));
        component = component.append(textComponent);
        return this;
    }

    public TextBuilder add(final String text, final String hoverValue) {
        if (text == null || hoverValue == null) {
            return this;
        }

        String translatedText = CC.translate(text);
        String translatedHover = CC.translate(hoverValue);
        Component textComponent = LegacyComponentSerializer.legacySection().deserialize(translatedText)
                .hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(translatedHover)));
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

        String translatedText = CC.translate(text);
        Component textComponent = LegacyComponentSerializer.legacySection().deserialize(translatedText);
        
        if (clickAction != null && clickValue != null) {
            textComponent = textComponent.clickEvent(createClickEvent(clickAction, clickValue));
        }

        if (hoverValue != null) {
            String translatedHover = CC.translate(hoverValue);
            textComponent = textComponent.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(translatedHover)));
        }

        component = component.append(textComponent);
        return this;
    }

    public void setClickEvent(final ClickEvent.Action action, final String value) {
        if (action == null || value == null) {
            return;
        }

        component = component.clickEvent(createClickEvent(action, value));
    }

    public TextBuilder setHoverEvent(final String value) {
        if (value == null) {
            return this;
        }

        String translatedValue = CC.translate(value);
        component = component.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(translatedValue)));
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
