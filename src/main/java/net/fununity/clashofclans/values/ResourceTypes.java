package net.fununity.clashofclans.values;

import net.fununity.clashofclans.buildings.list.ResourceContainerBuildings;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public enum ResourceTypes implements ICoCValue {

    FOOD(TranslationKeys.COC_RESOURCE_FOOD, ChatColor.RED, UsefulItems.BACKGROUND_RED, Material.CARROT),
    GOLD(TranslationKeys.COC_RESOURCE_GOLD, ChatColor.YELLOW, UsefulItems.BACKGROUND_YELLOW, Material.GOLD_INGOT),
    ELECTRIC(TranslationKeys.COC_RESOURCE_ELECTRIC, ChatColor.GOLD, UsefulItems.BACKGROUND_ORANGE, Material.END_ROD);

    public static List<ResourceTypes> canReachWithTownHall(int townHallLevel) {
        if (townHallLevel < ResourceContainerBuildings.GENERATOR.getBuildingLevelData()[0].getMinTownHall())
            return List.of(FOOD, GOLD);
        return List.of(values());
    }

    private final String nameKey;
    private final ChatColor chatColor;
    private final ItemStack glass;
    private final Material representativeItem;

    ResourceTypes(String nameKey, ChatColor chatColor, ItemStack glass, Material representativeItem) {
        this.nameKey = nameKey;
        this.chatColor = chatColor;
        this.glass = glass;
        this.representativeItem = representativeItem;
    }

    public ItemStack getGlass() {
        return glass;
    }

    /**
     * Return the name translation key of the value.
     *
     * @return String - translation name key.
     * @since 1.0.2
     */
    @Override
    public String getNameKey() {
        return nameKey;
    }

    /**
     * Return the chat color of the value.
     *
     * @return ChatColor - the chat color.
     * @since 1.0.2
     */
    @Override
    public ChatColor getChatColor() {
        return chatColor;
    }

    /**
     * Return the Material of the value.
     *
     * @return Material - representative material.
     * @since 1.0.2
     */
    @Override
    public Material getRepresentativeMaterial() {
        return representativeItem;
    }

    /**
     * Returns the colored name of the value.
     *
     * @param language Language - the language to translate.
     * @return String - colored translated name.
     * @since 1.0.2
     */
    @Override
    public String getColoredName(Language language) {
        return this.chatColor + language.getTranslation(this.nameKey);
    }
}
