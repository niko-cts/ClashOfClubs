package net.fununity.clashofclans.buildings.interfaces;

import net.fununity.clashofclans.buildings.interfaces.data.DefenseLevelData;
import net.fununity.clashofclans.troops.ITroop;

/**
 * Interface class for defense buildings.
 * @see net.fununity.clashofclans.buildings.list.DefenseBuildings
 * @author Niko
 * @since 0.0.
 */
public interface IDefenseBuilding extends IBuilding {

    /**
     * Get the troop the defense building priorities.
     * @return {@link ITroop} - the prioritised troop.
     * @since 0.0.1
     */
    ITroop getPrioritizeTroop();

    /**
     * If flying enemies can be attacked.
     * @return boolean - can also attack flying enemies.
     * @since 0.0.1
     */
    boolean canAttackFlying();

    /**
     * Get the radius of the defense building.
     * @return double - the radius.
     * @since 0.0.1
     */
    double getRadius();

    /**
     * Get the speed the defense building attacks in ticks.
     * @return long - the attack speed in ticks.
     * @since 0.0.1
     */
    long getAttackSpeed();

    /**
     * Get the building data each level of the building for defense buildings.
     * @return {@link DefenseLevelData}[] - Array for each level data.
     * @since 0.0.1
     */
    @Override
    DefenseLevelData[] getBuildingLevelData();

}
