package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.TroopsCreateBuilding;
import net.fununity.clashofclans.buildings.interfaces.ITroopCreateBuilding;
import net.fununity.clashofclans.buildings.interfaces.TroopsCreateLevelData;
import net.fununity.clashofclans.buildings.interfaces.TroopsLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.troops.TroopType;
import org.bukkit.Material;

public enum TroopCreationBuildings implements ITroopCreateBuilding {
    BARRACKS(TranslationKeys.COC_BUILDING_TROOPS_CREATION_BARRACKS_NORMAL_NAME, TranslationKeys.COC_BUILDING_TROOPS_CREATION_BARRACKS_NORMAL_DESCRIPTION, new int[]{16, 14}, ResourceTypes.FOOD, Material.IRON_HELMET,
            new TroopsCreateLevelData[]{new TroopsCreateLevelData(250, 1, 50, 10, 10, TroopType.LAND)});

    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final ResourceTypes resourceType;
    private final Material material;
    private final TroopsCreateLevelData[] buildingLevelData;

    TroopCreationBuildings(String nameKey, String descriptionKey, int[] size, ResourceTypes resourceType, Material material, TroopsCreateLevelData[] buildingLevelData) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.size = size;
        this.resourceType = resourceType;
        this.material = material;
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
     * Get the material of the building.
     * @return Material - the displaying material.
     * @since 0.0.1
     */
    @Override
    public Material getMaterial() {
        return material;
    }
    /**
     * Get the building class.
     * @return Class<? extends GeneralBuilding> - A class which extends the general building class.
     * @since 0.0.1
     */
    @Override
    public Class<? extends GeneralBuilding> getBuildingClass() {
        return TroopsCreateBuilding.class;
    }

    /**
     * Get the building data each level of the building for troop buildings.
     * @return {@link TroopsLevelData}[] - Array for each level data.
     * @since 0.0.1
     */
    @Override
    public TroopsCreateLevelData[] getBuildingLevelData() {
        return buildingLevelData;
    }
}
