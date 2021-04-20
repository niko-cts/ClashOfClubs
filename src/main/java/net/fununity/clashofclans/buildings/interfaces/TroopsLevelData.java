package net.fununity.clashofclans.buildings.interfaces;

/**
 * The troop level data class.
 * Each building data per level of the building.
 * @author Niko
 * @since 0.0.1
 */
public class TroopsLevelData extends BuildingLevelData {

    private final int maxAmountOfTroops;

    /**
     * Instantiates the class.
     * @param maxHP int - the maximum hp.
     * @param minTownHall int - the minimum town hall level.
     * @param upgradeCost int - the upgrade cost.
     * @param maxAmountOfTroops int - the maximum amount of troops
     * @since 0.0.1
     */
    public TroopsLevelData(int maxHP, int minTownHall, int upgradeCost, int buildSeconds, int maxAmountOfTroops) {
        super(maxHP, minTownHall, upgradeCost, buildSeconds);
        this.maxAmountOfTroops = maxAmountOfTroops;
    }

    /**
     * The maximum amount of troops.
     * @return int - max amount of troops.
     * @since 0.0.
     */
    public int getMaxAmountOfTroops() {
        return maxAmountOfTroops;
    }
}
