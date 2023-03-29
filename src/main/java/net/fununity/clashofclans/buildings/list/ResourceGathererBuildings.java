package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.interfaces.IResourceGatherBuilding;
import net.fununity.clashofclans.buildings.interfaces.IUpgradeDetails;
import net.fununity.clashofclans.buildings.interfaces.data.BuildingLevelData;
import net.fununity.clashofclans.buildings.interfaces.data.ResourceGatherLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum ResourceGathererBuildings implements IResourceGatherBuilding, IUpgradeDetails {
    GOLD_MINER(TranslationKeys.COC_BUILDING_GATHER_GOLD_MINER_NAME, TranslationKeys.COC_BUILDING_GATHER_GOLD_MINER_DESCRIPTION, new int[]{19, 15}, ResourceTypes.FOOD, Material.GOLD_ORE, ResourceTypes.GOLD,
            new ResourceGatherLevelData[]{new ResourceGatherLevelData(250, 1, 75, 30, 1200, 300),
                                          new ResourceGatherLevelData(300, 1, 750, 3 * 60, 5000, 400),
                                          new ResourceGatherLevelData(350, 2, 10000, 10 * 60, 15000, 600),
                                          new ResourceGatherLevelData(400, 2, 50000, 2 * 60 * 60, 50000, 1200)}),

    FARM(TranslationKeys.COC_BUILDING_GATHER_FARM_NAME, TranslationKeys.COC_BUILDING_GATHER_FARM_DESCRIPTION, new int[]{19, 15}, ResourceTypes.GOLD, Material.WHEAT_SEEDS, ResourceTypes.FOOD,
            new ResourceGatherLevelData[]{new ResourceGatherLevelData(250, 1, 75, 10, 1200, 300),
                                          new ResourceGatherLevelData(300, 1, 750, 3 * 60, 5000, 400),
                                          new ResourceGatherLevelData(350, 2, 10000, 10 * 60, 15000, 600),
                                          new ResourceGatherLevelData(400, 2, 50000, 2 * 60 * 60, 50000, 1200)}),

    COAL_MINER(TranslationKeys.COC_BUILDING_GATHER_COAL_DRILL_NAME, TranslationKeys.COC_BUILDING_GATHER_COAL_DRILL_DESCRIPTION, new int[]{19, 15}, ResourceTypes.GOLD, Material.COAL_ORE, ResourceTypes.ELECTRIC,
            new ResourceGatherLevelData[]{new ResourceGatherLevelData(250, 6, 250000, 5 * 60,  15, 15),
                                          new ResourceGatherLevelData(300, 6, 350000, 60 * 60, 20, 20),
                                          new ResourceGatherLevelData(350, 7, 450000, 5 * 60 * 60, 30, 30),
                                          new ResourceGatherLevelData(400, 7, 6000000, 24 * 60 * 60, 40, 40)}),;


    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final ResourceTypes resourceType;
    private final Material material;
    private final ResourceTypes containingResourceType;
    private final ResourceGatherLevelData[] buildingLevelData;

    ResourceGathererBuildings(String nameKey, String descriptionKey, int[] size, ResourceTypes resourceType, Material material, ResourceTypes containingResourceType, ResourceGatherLevelData[] buildingLevelData) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.size = size;
        this.resourceType = resourceType;
        this.material = material;
        this.containingResourceType = containingResourceType;
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
    public ResourceTypes getBuildingCostType() {
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

    /**
     * Get the lore details for upgrade and build.
     * @param buildingLevelData {@link BuildingLevelData} - the level data instance.
     * @param language                   Language - the language to translate to.
     * @return List<String> - Further lore details.
     * @since 0.0.1
     */
    @Override
    public List<String> getLoreDetails(BuildingLevelData buildingLevelData, Language language) {
        return Arrays.asList(language.getTranslation(TranslationKeys.COC_BUILDING_GATHER_LOREDETAILS, Arrays.asList("${type}", "${hour}", "${max}", "${color}"),
                Arrays.asList(getContainingResourceType().getColoredName(language), ((ResourceGatherLevelData) buildingLevelData).getResourceGatheringPerHour()+"", ((ResourceGatherLevelData) buildingLevelData).getMaximumResource()+"", getContainingResourceType().getChatColor() + "")).split(";"));
    }

    /**
     * Get the containing type of resource.
     * @return {@link ResourceTypes} - the containing resource.
     * @since 0.0.1
     */
    @Override
    public ResourceTypes getContainingResourceType() {
        return containingResourceType;
    }

    @Override
    public ResourceGatherLevelData[] getBuildingLevelData() {
        return buildingLevelData;
    }
}
