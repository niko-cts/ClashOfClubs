package net.fununity.clashofclans;

import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import javax.jws.soap.SOAPBinding;

public enum ResourceTypes {

    ELIXIR(TranslationKeys.COC_RESOURCE_ELIXIR, ChatColor.LIGHT_PURPLE, UsefulItems.BACKGROUND_MAGENTA),
    GOLD(TranslationKeys.COC_RESOURCE_GOLD, ChatColor.YELLOW, UsefulItems.BACKGROUND_YELLOW),
    DARK(TranslationKeys.COC_RESOURCE_DARK, ChatColor.GRAY, UsefulItems.BACKGROUND_RED);

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
