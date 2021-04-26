package net.fununity.clashofclans.troops;

import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.misc.translationhandler.translations.Language;

public enum TroopType {

    LAND(TranslationKeys.COC_TROOPS_TYPE_LAND),
    FLYING(TranslationKeys.COC_TROOPS_TYPE_FLYING);

    private final String nameKey;

    TroopType(String nameKey) {
        this.nameKey = nameKey;
    }

    public String getName(Language language) {
        return language.getTranslation(nameKey);
    }

}
