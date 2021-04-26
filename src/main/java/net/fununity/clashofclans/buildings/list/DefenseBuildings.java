package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.classes.DefenseBuilding;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.*;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.troops.TroopType;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum DefenseBuildings implements IDefenseBuilding, IUpgradeDetails {
    CANNON (TranslationKeys.COC_BUILDING_DEFENSE_CANNON_NAME, TranslationKeys.COC_BUILDING_DEFENSE_CANNON_DESCRIPTION, new int[]{5, 5}, ResourceTypes.GOLD, Material.FIRE_CHARGE, false, 10,null,
                    new DefenseLevelData[]{new DefenseLevelData(300, 1, 50, 15, 20),
                            new DefenseLevelData(400, 2, 100, 60, 30),
                            new DefenseLevelData(500, 2, 150, 300, 50)}),
    ARCHER_TOWER (TranslationKeys.COC_BUILDING_DEFENSE_ARCHERTOWER_NAME, TranslationKeys.COC_BUILDING_DEFENSE_ARCHERTOWER_DESCRIPTION, new int[]{5, 5}, ResourceTypes.GOLD, Material.BOW, true, 15,null,
            new DefenseLevelData[]{new DefenseLevelData(400, 1, 80, 25, 30),
                    new DefenseLevelData(500, 2, 500, 240, 50),
                    new DefenseLevelData(600, 3, 750, 1800, 70)});

    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final ResourceTypes resourceType;
    private final Material material;
    private final boolean attackFlying;
    private final int radius;
    private final TroopType prioritizedType;
    private final DefenseLevelData[] buildingLevelData;

    DefenseBuildings(String nameKey, String descriptionKey, int[] size, ResourceTypes resourceType, Material material, boolean attackFlying, int radius, TroopType prioritizedType, DefenseLevelData[] buildingLevelData) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.size = size;
        this.resourceType = resourceType;
        this.material = material;
        this.attackFlying = attackFlying;
        this.radius = radius;
        this.prioritizedType = prioritizedType;
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
     * Get the type the defense building priorities.
     * @return {@link TroopType} - the prioritised troop type.
     * @since 0.0.1
     */
    @Override
    public TroopType getPrioritizeType() {
        return prioritizedType;
    }

    /**
     * If flying enemies can be attacked.
     *
     * @return boolean - can also attack flying enemies.
     * @since 0.0.1
     */
    @Override
    public boolean attackFlying() {
        return attackFlying;
    }

    /**
     * Get the radius of the defense building.
     * @return int - the radius.
     * @since 0.0.1
     */
    @Override
    public int getRadius() {
        return radius;
    }

    /**
     * A list of the building level steps.
     * @return {@link BuildingLevelData}[] - Each level step for upgrade stuff.
     * @since 0.0.1
     */
    @Override
    public DefenseLevelData[] getBuildingLevelData() {
        return buildingLevelData;
    }

    /**
     * Get the building class.
     * @return Class<? extends GeneralBuilding> - A class which extends the general building class.
     * @since 0.0.1
     */
    @Override
    public Class<? extends GeneralBuilding> getBuildingClass() {
        return DefenseBuilding.class;
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
        return Arrays.asList(language.getTranslation(TranslationKeys.COC_BUILDING_DEFENSE_LOREDETAILS, Arrays.asList("${hp}", "${damage}", "${flying}", "${prioritize}", "${radius}"),
                Arrays.asList(buildingLevelData.getMaxHP()+"", ((DefenseLevelData)buildingLevelData).getDamage()+"", attackFlying+"", getPrioritizeType() == null ? "None" : getPrioritizeType().getName(language), getRadius()+"")).split(";"));
    }
}
