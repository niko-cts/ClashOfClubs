package net.fununity.clashofclans.buildings.interfaces.data;

import net.fununity.clashofclans.troops.TroopType;

/**
 * The troop creation level data class.
 * Each building data per level of the building.
 * @author Niko
 * @since 0.0.1
 */
public class TroopsCreateLevelData extends TroopsLevelData {

    private final TroopType troopType;

    /**
     * Instantiates the class.
     * @param maxHP             int - the maximum hp.
     * @param minTownHall       int - the minimum town hall level.
     * @param upgradeCost       int - the upgrade cost.
     * @param maxAmountOfTroops int - the maximum amount of troops
     * @since 0.0.1
     */
    public TroopsCreateLevelData(int maxHP, int minTownHall, int upgradeCost, int buildSeconds, int maxAmountOfTroops, TroopType troopType) {
        super(maxHP, minTownHall, upgradeCost, buildSeconds, maxAmountOfTroops);
        this.troopType = troopType;
    }

    /**
     * The type of troops which can be created in here.
     * @return TroopType - the type of troop
     * @since 0.0.
     */
    public TroopType getTroopType() {
        return troopType;
    }
}
