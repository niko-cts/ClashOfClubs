package net.fununity.clashofclans.buildings.interfaces.data;

/**
 * Each defense building data per level of the building.
 * @author Niko
 * @since 0.0.1
 */
public class DefenseLevelData extends BuildingLevelData {

    private final float damage;

    /**
     * Instantiates the class.
     * @param maxHP        int - the maximum hp.
     * @param minTownHall  int - the minimum town hall level.
     * @param upgradeCost  int - the upgrade cost.
     * @param buildSeconds int - the seconds to build the building.
     * @param damage     float - the amount of damage per hit.
     * @since 0.0.1
     */
    public DefenseLevelData(int maxHP, int minTownHall, int upgradeCost, int buildSeconds, float damage) {
        super(maxHP, minTownHall, upgradeCost, buildSeconds);
        this.damage = damage;
    }

    /**
     * Get the damage the building does.
     * @return double - damage to defense.
     * @since 0.0.1
     */
    public float getDamage() {
        return damage;
    }
}
