package net.fununity.clashofclans.buildings.interfaces.data;

/**
 * Each defense building data per level of the building.
 * @author Niko
 * @since 0.0.1
 */
public class DefenseLevelData extends BuildingLevelData {

    private final double damage;

    /**
     * Instantiates the class.
     * @param maxHP        int - the maximum hp.
     * @param minTownHall  int - the minimum town hall level.
     * @param upgradeCost  int - the upgrade cost.
     * @param buildSeconds int - the seconds to build the building.
     * @since 0.0.1
     */
    public DefenseLevelData(int maxHP, int minTownHall, int upgradeCost, int buildSeconds, double damage) {
        super(maxHP, minTownHall, upgradeCost, buildSeconds);
        this.damage = damage;
    }

    /**
     * Get the damage the building does.
     * @return double - damage of defense.
     * @since 0.0.1
     */
    public double getDamage() {
        return damage;
    }
}
