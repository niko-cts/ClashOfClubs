package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.interfaces.BuildingLevelData;
import net.fununity.clashofclans.buildings.interfaces.IResourceGatherBuilding;
import net.fununity.clashofclans.buildings.interfaces.IUpgradeDetails;
import net.fununity.clashofclans.buildings.interfaces.ResourceGatherLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum ResourceGathererBuildings implements IResourceGatherBuilding, IUpgradeDetails {
    GOLD_MINER(TranslationKeys.COC_BUILDING_GATHER_GOLD_MINER_NAME, TranslationKeys.COC_BUILDING_GATHER_GOLD_MINER_DESCRIPTION, new int[]{5, 4}, ResourceTypes.FOOD, Material.GOLD_ORE, ResourceTypes.GOLD,
            new ResourceGatherLevelData[]{new ResourceGatherLevelData(250, 1, 75, 10, 50, 150),
                                          new ResourceGatherLevelData(300, 1, 150, 30, 75, 300),
                                          new ResourceGatherLevelData(350, 2, 350, 60, 100, 450),
                                          new ResourceGatherLevelData(400, 2, 700, 120, 125, 600)}),

    FARM(TranslationKeys.COC_BUILDING_GATHER_FARM_NAME, TranslationKeys.COC_BUILDING_GATHER_FARM_DESCRIPTION, new int[]{5, 4}, ResourceTypes.GOLD, Material.WHEAT_SEEDS, ResourceTypes.FOOD,
            new ResourceGatherLevelData[]{new ResourceGatherLevelData(250, 1, 75, 10, 50, 150),
                                          new ResourceGatherLevelData(300, 1, 150, 30, 75, 300),
                                          new ResourceGatherLevelData(350, 2, 350, 60, 100, 450),
                                          new ResourceGatherLevelData(400, 2, 700, 120, 125, 600)}),

    COAL_MINER(TranslationKeys.COC_BUILDING_GATHER_COAL_DRILL_NAME, TranslationKeys.COC_BUILDING_GATHER_COAL_DRILL_DESCRIPTION, new int[]{5, 4}, ResourceTypes.GOLD, Material.COAL, ResourceTypes.ELECTRIC,
            new ResourceGatherLevelData[]{new ResourceGatherLevelData(250, 6, 250000, 3600 * 4,  15, 15),
                                          new ResourceGatherLevelData(300, 6, 350000, 3600 * 5, 20, 20),
                                          new ResourceGatherLevelData(350, 7, 450000, 3600 * 6, 30, 30),
                                          new ResourceGatherLevelData(400, 7, 6000000, 3600 * 7, 40, 40)}),;


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
