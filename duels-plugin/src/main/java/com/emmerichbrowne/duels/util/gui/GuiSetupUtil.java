package com.emmerichbrowne.duels.util.gui;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.config.Config;
import com.emmerichbrowne.duels.config.Lang;
import com.emmerichbrowne.duels.util.CommonItems;
import com.emmerichbrowne.duels.util.inventory.ItemBuilder;
import org.bukkit.Material;

public final class GuiSetupUtil {

    private GuiSetupUtil() {
    }

    public static <T extends DuelsPlugin> void setupCommonGuiElements(
            MultiPageGui<T> gui, Lang lang,
            String fillerType, short fillerData,
            String prevPageKey, String nextPageKey, String emptyKey) {
        
        gui.setSpaceFiller(CommonItems.from(fillerType));
        gui.setPrevButton(ItemBuilder.of(Material.PAPER).name(lang.getMessage(prevPageKey)).build());
        gui.setNextButton(ItemBuilder.of(Material.PAPER).name(lang.getMessage(nextPageKey)).build());
        gui.setEmptyIndicator(ItemBuilder.of(Material.PAPER).name(lang.getMessage(emptyKey)).build());
    }

    public static <T extends DuelsPlugin> void setupKitSelectorGui(MultiPageGui<T> gui, Config config, Lang lang) {
        setupCommonGuiElements(gui, lang,
            config.getKitSelectorFillerType(), config.getKitSelectorFillerData(),
            "GUI.kit-selector.buttons.previous-page.name",
            "GUI.kit-selector.buttons.next-page.name",
            "GUI.kit-selector.buttons.empty.name");
    }

    public static <T extends DuelsPlugin> void setupArenaSelectorGui(MultiPageGui<T> gui, Config config, Lang lang) {
        setupCommonGuiElements(gui, lang,
            config.getArenaSelectorFillerType(), config.getArenaSelectorFillerData(),
            "GUI.kit-selector.buttons.previous-page.name",
            "GUI.kit-selector.buttons.next-page.name", 
            "GUI.kit-selector.buttons.empty.name");
    }


}
