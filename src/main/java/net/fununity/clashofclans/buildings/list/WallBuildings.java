package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.WallBuilding;
import net.fununity.clashofclans.buildings.interfaces.data.BuildingLevelData;
import net.fununity.clashofclans.buildings.interfaces.IWall;
import net.fununity.clashofclans.language.TranslationKeys;
import org.bukkit.Material;

public enum WallBuildings implements IWall {
    STRAIGHT_WALL(TranslationKeys.COC_WALLS_STRAIGHT_WALL_NAME, TranslationKeys.COC_WALLS_STRAIGHT_WALL_DESCRIPTION, new int[]{9, 3}, ResourceTypes.GOLD, Material.OAK_FENCE,
            new BuildingLevelData[]{new BuildingLevelData(300, 1, 10, 0),
                    new BuildingLevelData(500, 1, 25, 0),
                    new BuildingLevelData(700, 2, 50, 0)}),
    CORNER_WALL(TranslationKeys.COC_WALLS_CORNER_WALL_NAME, TranslationKeys.COC_WALLS_CORNER_WALL_DESCRIPTION, new int[]{3, 3}, ResourceTypes.GOLD, Material.OAK_FENCE,
            new BuildingLevelData[]{new BuildingLevelData(300, 1, 5, 0),
                    new BuildingLevelData(500, 1, 25, 0),
                    new BuildingLevelData(700, 2, 50, 0)}),
    CROSS_WALL(TranslationKeys.COC_WALLS_CROSS_WALL_NAME, TranslationKeys.COC_WALLS_CROSS_WALL_DESCRIPTION, new int[]{3, 3}, ResourceTypes.GOLD, Material.OAK_FENCE,
            new BuildingLevelData[]{new BuildingLevelData(300, 1, 5, 0),
                    new BuildingLevelData(500, 1, 25, 0),
                    new BuildingLevelData(700, 2, 50, 0)}),
    HALFCROSS_WALL(TranslationKeys.COC_WALLS_HALFCROSS_WALL_NAME, TranslationKeys.COC_WALLS_HALFCROSS_WALL_DESCRIPTION, new int[]{3, 3}, ResourceTypes.GOLD, Material.OAK_FENCE,
            new BuildingLevelData[]{new BuildingLevelData(300, 1, 5, 0),
                    new BuildingLevelData(500, 1, 25, 0),
                    new BuildingLevelData(700, 2, 50, 0)}),
    GATE(TranslationKeys.COC_WALLS_GATE_NAME, TranslationKeys.COC_WALLS_GATE_DESCRIPTION, new int[]{5, 3}, ResourceTypes.GOLD, Material.OAK_FENCE_GATE,
            new BuildingLevelData[]{new BuildingLevelData(300, 1, 10, 0),
                    new BuildingLevelData(500, 1, 75, 0),
                    new BuildingLevelData(700, 2, 100, 0)});


    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final ResourceTypes resourceType;
    private final Material material;
    private final BuildingLevelData[] buildingLevelData;

    WallBuildings(String nameKey, String descriptionKey, int[] size, ResourceTypes resourceType, Material material, BuildingLevelData[] buildingLevelData) {
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
        return WallBuilding.class;
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
