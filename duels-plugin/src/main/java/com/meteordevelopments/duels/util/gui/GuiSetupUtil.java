package com.meteordevelopments.duels.util.gui;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.config.Config;
import com.meteordevelopments.duels.config.Lang;
import com.meteordevelopments.duels.util.compat.Items;
import com.meteordevelopments.duels.util.inventory.ItemBuilder;
import org.bukkit.Material;

public final class GuiSetupUtil {

    private GuiSetupUtil() {
    }

    /**
     * Sets up common GUI elements (space filler, navigation buttons)
     * @param gui The GUI to set up
     * @param lang Language instance
     * @param fillerType Filler material type
     * @param fillerData Filler material data
     * @param prevPageKey Language key for previous page button
     * @param nextPageKey Language key for next page button
     * @param emptyKey Language key for empty indicator
     */
    public static <T extends DuelsPlugin> void setupCommonGuiElements(
            MultiPageGui<T> gui, Lang lang,
            String fillerType, short fillerData,
            String prevPageKey, String nextPageKey, String emptyKey) {
        
        gui.setSpaceFiller(Items.from(fillerType, fillerData));
        gui.setPrevButton(ItemBuilder.of(Material.PAPER).name(lang.getMessage(prevPageKey)).build());
        gui.setNextButton(ItemBuilder.of(Material.PAPER).name(lang.getMessage(nextPageKey)).build());
        gui.setEmptyIndicator(ItemBuilder.of(Material.PAPER).name(lang.getMessage(emptyKey)).build());
    }

    /**
     * Sets up kit selector GUI elements
     * @param gui The GUI to set up
     * @param config Configuration instance
     * @param lang Language instance
     */
    public static <T extends DuelsPlugin> void setupKitSelectorGui(MultiPageGui<T> gui, Config config, Lang lang) {
        setupCommonGuiElements(gui, lang,
            config.getKitSelectorFillerType(), config.getKitSelectorFillerData(),
            "GUI.kit-selector.buttons.previous-page.name",
            "GUI.kit-selector.buttons.next-page.name",
            "GUI.kit-selector.buttons.empty.name");
    }

    /**
     * Sets up arena selector GUI elements
     * @param gui The GUI to set up
     * @param config Configuration instance
     * @param lang Language instance
     */
    public static <T extends DuelsPlugin> void setupArenaSelectorGui(MultiPageGui<T> gui, Config config, Lang lang) {
        setupCommonGuiElements(gui, lang,
            config.getArenaSelectorFillerType(), config.getArenaSelectorFillerData(),
            "GUI.kit-selector.buttons.previous-page.name",
            "GUI.kit-selector.buttons.next-page.name", 
            "GUI.kit-selector.buttons.empty.name");
    }


}
