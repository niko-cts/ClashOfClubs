package net.fununity.clashofclans.buildings.interfaces;

import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.data.BuildingLevelData;
import net.fununity.clashofclans.values.ICoCValue;
import org.bukkit.Material;

/**
 * Interface class for buildings.
 * @see net.fununity.clashofclans.buildings.list.Buildings
 * @author Niko
 * @since 0.0.
 */
public interface IBuilding {

    /**
     * Get the enum name of the building.
     * @return String - the name of the building.
     * @since 0.0.1
     */
    String name();

    /**
     * Get the translation key of the building name.
     * @return String - the name key.
     * @since 0.0.1
     */
    String getNameKey();

    /**
     * Get the translation key of the building description.
     * @return String - the description key.
     * @since 0.0.1
     */
    String getDescriptionKey();

    /**
     * Get the size of the building. (x=size[0], z=size[1])
     * @return int[] - the size of the building.
     * @since 0.0.1
     */
    int[] getSize();

    /**
     * The resource type of the building.
     * Building cost type.
     * @return {@link ICoCValue} - the type of resource for the building.
     * @since 0.0.1
     */
    ICoCValue getBuildingCostType();

    /**
     * A list of the building level steps.
     * @return {@link BuildingLevelData}[] - Each level step for upgrade stuff.
     * @since 0.0.1
     */
    BuildingLevelData[] getBuildingLevelData();

    /**
     * Get the building class.
     * @return Class<? extends GeneralBuilding> - A class which extends the general building class.
     * @since 0.0.1
     */
    Class<? extends GeneralBuilding> getBuildingClass();

    /**
     * Get the material of the building.
     * @return Material - the displaying material.
     * @since 0.0.1
     */
    Material getMaterial();
}
