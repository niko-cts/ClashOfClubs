package net.fununity.clashofclans.buildings.interfaces;
/**
 * Interface class for troop buildings.
 * @see net.fununity.clashofclans.buildings.list.TroopBuildings
 * @author Niko
 * @since 0.0.
 */
public interface ITroopCreateBuilding extends ITroopBuilding {

    /**
     * Get the building data each level of the building for troop buildings.
     * @return {@link TroopsLevelData}[] - Array for each level data.
     * @since 0.0.1
     */
    @Override
    TroopsCreateLevelData[] getBuildingLevelData();

}
