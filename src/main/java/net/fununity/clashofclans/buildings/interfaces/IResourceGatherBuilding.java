package net.fununity.clashofclans.buildings.interfaces;
/**
 * Interface class for resource gathering buildings.
 * @see net.fununity.clashofclans.buildings.list.ResourceGathererBuildings
 * @author Niko
 * @since 0.0.
 */
public interface IResourceGatherBuilding extends IResourceContainerBuilding {

    /**
     * Get the building data each level of the building for resource gathering buildings.
     * @return {@link ResourceGatherLevelData}[] - Array for each level data.
     * @since 0.0.1
     */
    @Override
    ResourceGatherLevelData[] getBuildingLevelData();

}
