package net.fununity.clashofclans.values;

import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * This class holds methods for all coc values.
 * @see ResourceTypes
 * @see PlayerValues
 * @author Niko
 * @since 1.0.2
 */
public interface ICoCValue {

    /**
     * Return the name translation key of the value.
     * @return String - translation name key.
     * @since 1.0.2
     */
    String getNameKey();

    /**
     * Return the chat color of the value.
     * @return ChatColor - the chat color.
     * @since 1.0.2
     */
    ChatColor getChatColor();

    /**
     * Return the Material of the value.
     * @return Material - representative material.
     * @since 1.0.2
     */
    Material getRepresentativeMaterial();

    /**
     * Returns the colored name of the value.
     * @param language Language - the language to translate.
     * @return String - colored translated name.
     * @since 1.0.2
     */
    String getColoredName(Language language);

    String name();
}
