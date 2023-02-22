package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsBuilding;
import net.fununity.clashofclans.buildings.interfaces.ITroopBuilding;
import net.fununity.clashofclans.buildings.interfaces.data.TroopsLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import org.bukkit.Material;

public enum TroopBuildings implements ITroopBuilding {
    ARMY_CAMP(TranslationKeys.COC_BUILDING_TROOPS_ARMYCAMP_NAME, TranslationKeys.COC_BUILDING_TROOPS_ARMYCAMP_DESCRIPTION, new int[]{17, 17}, ResourceTypes.FOOD, Material.CAMPFIRE,
            new TroopsLevelData[]{new TroopsLevelData(100, 1, 150, 3 * 60, 10),
                        new TroopsLevelData(200, 1, 350, 10 * 60, 15),});

    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final ResourceTypes resourceType;
    private final Material material;
    private final TroopsLevelData[] buildingLevelData;

    TroopBuildings(String nameKey, String descriptionKey, int[] size, ResourceTypes resourceType, Material material, TroopsLevelData[] buildingLevelData) {
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
        return TroopsBuilding.class;
    }

    /**
     * Get the building data each level of the building for troop buildings.
     * @return {@link TroopsLevelData}[] - Array for each level data.
     * @since 0.0.1
     */
    @Override
    public TroopsLevelData[] getBuildingLevelData() {
        return buildingLevelData;
    }
}
