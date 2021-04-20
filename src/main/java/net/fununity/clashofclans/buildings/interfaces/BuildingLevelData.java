package net.fununity.clashofclans.buildings.interfaces;

/**
 * Each building data per level of the building.
 * @author Niko
 * @since 0.0.1
 */
public class BuildingLevelData {

    private final int maxHP;
    private final int minTownHall;
    private final int upgradeCost;
    private int buildSeconds;
    private int xpGet;

    /**
     * Instantiates the class.
     * @param maxHP int - the maximum hp.
     * @param minTownHall int - the minimum town hall level.
     * @param upgradeCost int - the upgrade cost.
     * @since 0.0.1
     */
    public BuildingLevelData(int maxHP, int minTownHall, int upgradeCost, int buildSeconds) {
        this.maxHP = maxHP;
        this.minTownHall = minTownHall;
        this.upgradeCost = upgradeCost;
        this.buildSeconds = buildSeconds;
        this.xpGet = minTownHall + upgradeCost / 100;
    }

    /**
     * Get the max hp of the level.
     * @return int - the maximum amount of hp
     * @since 0.0.1
     */
    public int getMaxHP() {
        return maxHP;
    }

    /**
     * Get the minimum town hall.
     * @return int - the minimum town hall level.
     * @since 0.0.1
     */
    public int getMinTownHall() {
        return minTownHall;
    }

    /**
     * Get the upgrade cost.
     * @return int - the amount of cost for an upgrade
     * @since 0.0.1
     */
    public int getUpgradeCost() {
        return upgradeCost;
    }

    /**
     * Set the xp amount the user gets.
     * @param xpGet int - the amount of xp the user gets.
     * @return BuildingLevelData - instance of this class.
     * @since 0.0.1
     */
    public BuildingLevelData setXpGet(int xpGet) {
        this.xpGet = xpGet;
        return this;
    }

    /**
     * Get the xp amount of the user.
     * @return int - the xp the user gets.
     * @since 0.0.1
     */
    public int getXpGet() {
        return xpGet;
    }

    public int getBuildTime() {
        return this.buildSeconds;
    }
}
