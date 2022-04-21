package net.fununity.clashofclans.buildings.interfaces.data;

/**
 * Level data class for resource containers.
 * @see BuildingLevelData
 * @author Niko
 * @since 0.0.1
 */
public class ResourceContainerLevelData extends BuildingLevelData {

    private final int maximumResource;

    /**
     * Instantiates the class.
     * @param maxHP int - the max hp of the building.
     * @param minTownHall int - the minimum town hall level.
     * @param upgradeCost int - the upgrade cost.
     * @param maximumResource int - the maximum amount of resource.
     * @since 0.0.1
     */
    public ResourceContainerLevelData(int maxHP, int minTownHall, int upgradeCost, int buildSeconds, int maximumResource) {
        super(maxHP, minTownHall, upgradeCost, buildSeconds);
        this.maximumResource = maximumResource;
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
