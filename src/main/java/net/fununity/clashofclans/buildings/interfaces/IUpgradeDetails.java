package net.fununity.clashofclans.buildings.interfaces;

import net.fununity.misc.translationhandler.translations.Language;

import java.util.List;

/**
 * Interface class for building lists,
 * which include further details about there upgrade/construction.
 * @author Niko
 * @since 0.0.1
 */
public interface IUpgradeDetails {

    /**
     * Get the lore details for upgrade and build.
     * @param buildingLevelData {@link BuildingLevelData} - the level data instance.
     * @param language Language - the language to translate to.
     * @return List<String> - Further lore details.
     * @since 0.0.1
     */
    List<String> getLoreDetails(BuildingLevelData buildingLevelData, Language language);

}
