package net.fununity.clashofclans.troops;

import net.fununity.clashofclans.buildings.interfaces.IDefenseBuilding;
import org.bukkit.entity.EntityType;

/**
 * The troop interface class.
 * @author Niko
 * @since 0.0.1
 */
public interface ITroop {

    /**
     * Get the name key of the troop.
     * @return String - the name key
     * @since 0.0.1
     */
    String getNameKey();

    /**
     * Get the description key of the troop.
     * @return String - the name key
     * @since 0.0.1
     */
    String descriptionKey();

    /**
     * Get the type of troop.
     * @return TroopType - The type of troop
     * @since 0.0.1
     */
    TroopType getTroopType();

    /**
     * Get the max hp of the troop.
     * @return int - the max hp.
     * @since 0.0.1
     */
    int getMaxHP();

    /**
     * Get the damage of the troop.
     * @return double - the damage.
     * @since 0.0.1
     */
    double getDamage();

    /**
     * Get the entity type of the troop.
     * @return EntityType - the entity type.
     * @since 0.0.1
     */
    EntityType getEntityType();

    /**
     * Get the enum name of the troop.
     * @return String - enum name.
     * @since 0.0.1
     */
    String name();

    /**
     * The size the troop needs.
     * @return int - needed space.
     * @since 0.0.1
     */
    int getSize();

    /**
     * The building that will be attacked prioritized by the troop.
     * @return IDefenseBuilding - prioritized defense building.
     * @since 0.0.1
     */
    IDefenseBuilding getPrioritizedDefense();
}
