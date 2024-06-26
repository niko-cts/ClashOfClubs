package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.data.BuildingLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.values.ICoCValue;
import net.fununity.clashofclans.values.PlayerValues;
import net.fununity.clashofclans.values.ResourceTypes;
import org.bukkit.Material;

public enum Buildings implements IBuilding {
    TOWN_HALL(TranslationKeys.COC_BUILDING_GENERAL_TOWN_HALL_NAME, TranslationKeys.COC_BUILDING_GENERAL_TOWN_HALL_DESCRIPTION, new int[]{28, 27}, ResourceTypes.GOLD, Material.AIR,
            new BuildingLevelData[]{new BuildingLevelData(500, 0, 50, 15),
                                    new BuildingLevelData(1000, 1, 500, 5 * 60),
                                    new BuildingLevelData(1500, 2, 2000, 3 * 60 * 60),
                                    new BuildingLevelData(2500, 3, 12500, 2 * 24 * 60 * 60)}),
    BUILDER (TranslationKeys.COC_BUILDING_GENERAL_BUILDER_NAME, TranslationKeys.COC_BUILDING_GENERAL_BUILDER_DESCRIPTION, new int[]{7, 7}, PlayerValues.GEMS, Material.WOODEN_AXE,
            new BuildingLevelData[]{new BuildingLevelData(250, 1, 500, 0)}),
    CLUB_TOWER (TranslationKeys.COC_BUILDING_GENERAL_CLUB_TOWER_NAME, TranslationKeys.COC_BUILDING_GENERAL_CLUB_TOWER_DESCRIPTION, new int[]{5, 5}, ResourceTypes.GOLD, Material.AIR,
            new BuildingLevelData[]{new BuildingLevelData(1500, 5, 250000, 3600*3)});

    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final ICoCValue cocValue;
    private final Material material;
    private final BuildingLevelData[] buildingLevelData;

    Buildings(String nameKey, String descriptionKey, int[] size, ICoCValue cocValue, Material material, BuildingLevelData[] buildingLevelData) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.size = size;
        this.cocValue = cocValue;
        this.material = material;
        this.buildingLevelData = buildingLevelData;
    }

    /**
     * Get the translation key of the building name.
     *
     * @return String - the name key.
     * @since 0.0.1
     */
    @Override
    public String getNameKey() {
        return nameKey;
    }

    /**
     * Get the translation key of the building description.
     *
     * @return String - the description key.
     * @since 0.0.1
     */
    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    /**
     * Get the size of the building. (x=size[0], z=size[1])
     *
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
     *
     * @return {@link ResourceTypes} - the type of resource for the building.
     * @since 0.0.1
     */
    @Override
    public ICoCValue getBuildingCostType() {
        return cocValue;
    }

    /**
     * A list of the building level steps.
     *
     * @return {@link BuildingLevelData}[] - Each level step for upgrade stuff.
     * @since 0.0.1
     */
    @Override
    public BuildingLevelData[] getBuildingLevelData() {
        return buildingLevelData;
    }

    /**
     * Get the building class.
     *
     * @return Class<? extends GeneralBuilding> - A class which extends the general building class.
     * @since 0.0.1
     */
    @Override
    public Class<? extends GeneralBuilding> getBuildingClass() {
        return GeneralBuilding.class;
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
}
