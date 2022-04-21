package net.fununity.clashofclans.buildings.interfaces;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.interfaces.data.ResourceContainerLevelData;

/**
 * Interface class for resource containing buildings.
 * @see net.fununity.clashofclans.buildings.list.ResourceContainerBuildings
 * @author Niko
 * @since 0.0.
 */
public interface IResourceContainerBuilding extends IBuilding {


    /**
     * Get the containing type of resource.
     * @return {@link ResourceTypes} - the containing resource.
     * @since 0.0.1
     */
    ResourceTypes getContainingResourceType();

    /**
     * Get the building data each level of the building for resource container buildings.
     * @return {@link ResourceContainerLevelData}[] - Array for each level data.
     * @since 0.0.1
     */
    @Override
    ResourceContainerLevelData[] getBuildingLevelData();

}
