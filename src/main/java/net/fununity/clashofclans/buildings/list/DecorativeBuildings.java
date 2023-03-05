package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.destroyables.DecorativeBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDestroyableBuilding;
import net.fununity.clashofclans.buildings.interfaces.data.BuildingLevelData;
import org.bukkit.Material;

public enum DecorativeBuildings implements IDestroyableBuilding {

    ;

    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final ResourceTypes resourceType;
    private final Material material;
    private final int gems;
    private final BuildingLevelData[] buildingLevelData;

    DecorativeBuildings(String nameKey, String descriptionKey, int[] size, ResourceTypes resourceType, Material material, int gems, BuildingLevelData[] buildingLevelData) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.size = size;
        this.resourceType = resourceType;
        this.material = material;
        this.gems = gems;
        this.buildingLevelData = buildingLevelData;
    }

    /**
     * Get the translation key of the building name.
     * @return String - the name key.
     * @since 0.0.1
     */
    @Override
    public String getNameKey() {
        return nameKey;
    }

    /**
     * Get the translation key of the building description.
     * @return String - the description key.
     * @since 0.0.1
     */
    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    /**
     * Get the size of the building. (x=size[0], z=size[1])
     * @return int[] - the size of the building.
     * @since 0.0.1
     */
    @Override
    public int[] getSize() {
        return size;
    }

    /**
     * The resource type of the building.
     * Building cost type.
     * @return {@link ResourceTypes} - the type of resource for the building.
     * @since 0.0.1
     */
    @Override
    public ResourceTypes getResourceType() {
        return resourceType;
    }

    /**
     * A list of the building level steps.
     * @return {@link BuildingLevelData}[] - Each level step for upgrade stuff.
     * @since 0.0.1
     */
    @Override
    public BuildingLevelData[] getBuildingLevelData() {
        return buildingLevelData;
    }

    /**
     * Get the building class.
     * @return Class<? extends GeneralBuilding> - A class which extends the general building class.
     * @since 0.0.1
     */
    @Override
    public Class<? extends GeneralBuilding> getBuildingClass() {
        return DecorativeBuilding.class;
    }

    /**
     * Get the material of the building.
     * @return Material - the displaying material.
     * @since 0.0.1
     */
    @Override
    public Material getMaterial() {
        return material;
    }

    /**
     * Random amount how much gems a player gets from destroying the building
     * @return int - the amount of gems received.
     * @since 0.0.1
     */
    @Override
    public int getGems() {
        return gems;
    }

    /**
     * Amount of exp per destroy
     *
     * @return int - the amount of exp received.
     * @since 0.0.1
     */
    @Override
    public int getExp() {
        return 0;
    }

    /**
     * When the building is destroyed the player gets back the full money.
     * @return boolean - receive full money back.
     * @since 0.0.1
     */
    @Override
    public boolean receiveFullPayPrice() {
        return true;
    }
}
