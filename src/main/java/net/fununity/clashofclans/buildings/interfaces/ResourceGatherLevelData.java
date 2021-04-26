package net.fununity.clashofclans.buildings.interfaces;

/**
 * Level data class for resource gatherings.
 * @see ResourceContainerLevelData
 * @author Niko
 * @since 0.0.1
 */
public class ResourceGatherLevelData extends ResourceContainerLevelData {

    private final int resourceGathering;

    /**
     * Instantiates the class.
     * @param maxHP int - the max hp of the building.
     * @param minTownHall int - the minimum town hall level.
     * @param upgradeCost int - the upgrade cost.
     * @param resourceType ResourceTypes - the container resource type.
     * @param maximumAmount int - the maximum amount of resource.
     * @param resourceGatheringPerHour int - the amount of resource per minute
     * @since 0.0.1
     */
    public ResourceGatherLevelData(int maxHP, int minTownHall, int upgradeCost, int buildSeconds, int maximumAmount, int resourceGatheringPerHour) {
        super(maxHP, minTownHall, upgradeCost, buildSeconds, maximumAmount);
        this.resourceGathering = resourceGatheringPerHour;
    }

    /**
     * Get the amount of resource gathered per minute.
     * @return int - the amount of resource per min.
     * @since 0.0.1
     */
    public int getResourceGatheringPerHour() {
        return resourceGathering;
    }
}
