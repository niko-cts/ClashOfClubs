package net.fununity.clashofclans;

import net.fununity.clashofclans.buildings.list.ResourceContainerBuildings;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public enum ResourceTypes {

    GEMS(TranslationKeys.COC_RESOURCE_GEMS, ChatColor.GREEN, UsefulItems.BACKGROUND_GREEN),
    FOOD(TranslationKeys.COC_RESOURCE_FOOD, ChatColor.RED, UsefulItems.BACKGROUND_RED),
    GOLD(TranslationKeys.COC_RESOURCE_GOLD, ChatColor.YELLOW, UsefulItems.BACKGROUND_YELLOW),
    ELECTRIC(TranslationKeys.COC_RESOURCE_ELECTRIC, ChatColor.GOLD, UsefulItems.BACKGROUND_ORANGE);

    private static final ResourceTypes[] EARLY = new ResourceTypes[]{GEMS, FOOD, GOLD};

    public static ResourceTypes[] canReachWithTownHall(int townHallLevel) {
        if (townHallLevel < ResourceContainerBuildings.GENERATOR.getBuildingLevelData()[0].getMinTownHall())
            return EARLY;
        return values();
    }

    private final String nameKey;
    private final ChatColor chatColor;
    private final ItemStack glass;

    ResourceTypes(String nameKey, ChatColor chatColor, ItemStack glass) {
        this.nameKey = nameKey;
        this.chatColor = chatColor;
        this.glass = glass;
    }

    public String getNameKey() {
        return nameKey;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public ItemStack getGlass() {
        return glass;
    }

    public String getColoredName(Language language) {
        return chatColor + language.getTranslation(nameKey);
    }
}
