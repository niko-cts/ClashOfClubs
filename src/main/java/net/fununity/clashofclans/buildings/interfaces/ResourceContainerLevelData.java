package net.fununity.clashofclans.buildings.interfaces;

import net.fununity.clashofclans.ResourceTypes;

/**
 * Level data class for resource containers.
 * @see BuildingLevelData
 * @author Niko
 * @since 0.0.1
 */
public class ResourceContainerLevelData extends BuildingLevelData {

    private final ResourceTypes resourceTypes;
    private final int maximumResource;

    /**
     * Instantiates the class.
     * @param maxHP int - the max hp of the building.
     * @param minTownHall int - the minimum town hall level.
     * @param upgradeCost int - the upgrade cost.
     * @param resourceTypes ResourceTypes - the container resource type.
     * @param maximumResource int - the maximum amount of resource.
     * @since 0.0.1
     */
    public ResourceContainerLevelData(int maxHP, int minTownHall, int upgradeCost, int buildSeconds, ResourceTypes resourceTypes, int maximumResource) {
        super(maxHP, minTownHall, upgradeCost, buildSeconds);
        this.resourceTypes = resourceTypes;
        this.maximumResource = maximumResource;
    }

    /**
     * Get the resource type containing.
     * @return ResourceTypes - the type of resource.
     * @since 0.0.1
     */
    public ResourceTypes getResourceTypes() {
        return resourceTypes;
    }

    /**
     * The maximum amount of resource in the container.
     * @return int - maximum amount of resource
     * @since 0.0.1
     */
    public int getMaximumResource() {
        return maximumResource;
    }
}
