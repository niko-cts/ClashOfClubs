package net.fununity.clashofclans.values;

import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Enum for Gems and Elo.
 * @author Niko
 * @since 1.0.2
 */
public enum PlayerValues implements ICoCValue {

    GEMS (200, TranslationKeys.COC_VALUES_GEMS, ChatColor.GREEN, Material.EMERALD),
    ELO (0, TranslationKeys.COC_VALUES_ELO, ChatColor.YELLOW, Material.GOLD_NUGGET),
    XP (0, TranslationKeys.COC_VALUES_XP, ChatColor.AQUA, Material.EXPERIENCE_BOTTLE);


    private final int defaultValue;
    private final String nameKey;
    private final ChatColor chatColor;
    private final Material representativeItem;

    PlayerValues(int defaultValue, String nameKey, ChatColor chatColor, Material representativeItem) {
        this.defaultValue = defaultValue;
        this.nameKey = nameKey;
        this.chatColor = chatColor;
        this.representativeItem = representativeItem;
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

    public int getDefaultValue() {
        return defaultValue;
    }
}
