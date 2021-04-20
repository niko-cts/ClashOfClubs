package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.interfaces.IResourceGatherBuilding;
import net.fununity.clashofclans.buildings.interfaces.ResourceGatherLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import org.bukkit.Material;

public enum ResourceGathererBuildings implements IResourceGatherBuilding {
    GOLD_MINER(TranslationKeys.COC_BUILDING_GATHER_GOLD_MINER_NAME, TranslationKeys.COC_BUILDING_GATHER_GOLD_MINER_DESCRIPTION, new int[]{5, 4}, ResourceTypes.ELIXIR, Material.GOLD_ORE, new ResourceGatherLevelData[]{new ResourceGatherLevelData(250, 1, 50, 10, ResourceTypes.GOLD, 50, 200)});


    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final ResourceTypes resourceType;
    private final Material material;
    private final ResourceGatherLevelData[] buildingLevelData;

    ResourceGathererBuildings(String nameKey, String descriptionKey, int[] size, ResourceTypes resourceType, Material material, ResourceGatherLevelData[] buildingLevelData) {
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
     * Get the building class.
     * @return Class<? extends GeneralBuilding> - A class which extends the general building class.
     * @since 0.0.1
     */
    @Override
    public Class<? extends GeneralBuilding> getBuildingClass() {
        return ResourceGatherBuilding.class;
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

    @Override
    public ResourceGatherLevelData[] getBuildingLevelData() {
        return buildingLevelData;
    }
}
