package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceContainerBuilding;
import net.fununity.clashofclans.buildings.interfaces.IResourceContainerBuilding;
import net.fununity.clashofclans.buildings.interfaces.IUpgradeDetails;
import net.fununity.clashofclans.buildings.interfaces.data.BuildingLevelData;
import net.fununity.clashofclans.buildings.interfaces.data.ResourceContainerLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum ResourceContainerBuildings implements IResourceContainerBuilding {
    GOLD_STOCK(TranslationKeys.COC_BUILDING_CONTAINER_GOLD_STOCK_NAME, TranslationKeys.COC_BUILDING_CONTAINER_GOLD_STOCK_DESCRIPTION, new int[]{4, 4}, ResourceTypes.ELIXIR, Material.GOLD_INGOT, new ResourceContainerLevelData[]{new ResourceContainerLevelData(250, 1, 50, 10, ResourceTypes.GOLD, 500)});

    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final ResourceTypes resourceType;
    private final ResourceTypes containingResourceType;
    private final ResourceContainerLevelData[] buildingLevelData;

    ResourceContainerBuildings(String nameKey, String descriptionKey, int[] size, ResourceTypes resourceType, ResourceTypes containingResourceType, ResourceContainerLevelData[] buildingLevelData) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.size = size;
        this.resourceType = resourceType;
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
    public ResourceTypes getBuildingCostType() {
        return resourceType;
    }

    /**
     * Get the building class.
     *
     * @return Class<? extends GeneralBuilding> - A class which extends the general building class.
     * @since 0.0.1
     */
    @Override
    public Class<? extends GeneralBuilding> getBuildingClass() {
        return ResourceContainerBuilding.class;
    }

    /**
     * Get the material of the building.
     * @return Material - the displaying material.
     * @since 0.0.1
     */
    @Override
    public Material getMaterial() {
        return getContainingResourceType().getRepresentativeMaterial();
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
        return Arrays.asList(language.getTranslation(TranslationKeys.COC_BUILDING_CONTAINER_LOREDETAILS, Arrays.asList("${type}", "${max}", "${color}"),
                Arrays.asList(getContainingResourceType().getColoredName(language), ((ResourceContainerLevelData)buildingLevelData).getMaximumResource()+"", getContainingResourceType().getChatColor() + "")).split(";"));
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
    public ResourceContainerLevelData[] getBuildingLevelData() {
        return buildingLevelData;
    }

}
