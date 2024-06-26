package net.fununity.clashofclans.troops;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.misc.translationhandler.translations.Language;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Material;

/**
 * The troop interface class.
 * @author Niko
 * @since 0.0.1
 */
public interface ITroop {

    /**
     * Get the name of the troop.
     * @param language Language - the language to translate.
     * @return String - the name
     * @since 0.0.1
     */
    String getName(Language language);

    /**
     * Get the description key of the troop.
     * @param language Language - the language to translate.
     * @return String - the description.
     * @since 0.0.1
     */
    String[] getDescription(Language language);

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
     * Get the damage to the troop.
     * @return double - the damage.
     * @since 0.0.1
     */
    double getDamage();

    /**
     * Get the range the entity can attack.
     * @return double - attack range.
     * @since 0.0.1
     */
    double getRange();

    /**
     * Get the entity type of the troop.
     * @return EntityTypes<? extends EntityCreature> - the entity type.
     * @since 0.0.1
     */
    EntityTypes<? extends EntityCreature> getEntityType();

    /**
     * Get the enum name of the troop.
     * @return String - enum name.
     * @since 0.0.1
     */
    String name();

    /**
     * Get the enum ordinal of the troop.
     * @return int - the ordinal.
     * @since 0.0.1
     */
    int ordinal();

    /**
     * The size the troop needs.
     * @return int - needed space.
     * @since 0.0.1
     */
    int getSize();

    /**
     * Get if the troop is flying.
     * @return boolean - is flying.
     * @since 0.0.1
     */
    boolean isFlying();

    /**
     * Get the minimum level of the barrack to train this troop.
     * @return int - minimum barrack level.
     * @since 0.0.1
     */
    int getMinBarracksLevel();

    /**
     * Get the amount of seconds to train this troop.
     * @return int - train duration in seconds.
     * @since 0.0.1
     */
    int getTrainDuration();

    /**
     * Get the price amount per unit.
     * @return int - amount of resource to train.
     * @since 0.0.1
     */
    int getCostAmount();

    /**
     * Get the representative item for this entity.
     * @return Material - the material.
     * @since 0.0.1
     */
    Material getRepresentativeItem();

    /**
     * The building that will be attacked prioritized by the troop.
     * @return {@link IBuilding} - prioritized building.
     * @since 0.0.1
     */
    IBuilding getPrioritizedBuilding();


}
