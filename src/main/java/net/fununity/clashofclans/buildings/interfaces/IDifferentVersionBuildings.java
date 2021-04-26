package net.fununity.clashofclans.buildings.interfaces;

/**
 * Interface for buildings that have different building versions for one level.
 * @author Niko
 * @since 0.0.1
 */
public interface IDifferentVersionBuildings {

    /**
     * Called when the version was updated.
     * @param schematic boolean - schematic change
     * @since 0.0.1
     */
    void updateVersion(boolean schematic);

    /**
     * Gets the current version of the building.
     * E.g. percentage of fill.
     * @return int - building version
     * @since 0.0.1
     */
    int getCurrentBuildingVersion();

}
