package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.buildings.instances.DefenseBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDefenseBuilding;
import net.fununity.clashofclans.buildings.interfaces.IUpgradeDetails;
import net.fununity.clashofclans.buildings.interfaces.data.BuildingLevelData;
import net.fununity.clashofclans.buildings.interfaces.data.DefenseLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum DefenseBuildings implements IDefenseBuilding, IUpgradeDetails {
    CANNON (TranslationKeys.COC_BUILDING_DEFENSE_CANNON_NAME, TranslationKeys.COC_BUILDING_DEFENSE_CANNON_DESCRIPTION, new int[]{7, 7}, ResourceTypes.GOLD, Material.FIRE_CHARGE, false, 25, 20, null,
                    new DefenseLevelData[]{new DefenseLevelData(300, 1, 50, 15, 7),
                            new DefenseLevelData(400, 2, 100, 3 * 60, 15),
                            new DefenseLevelData(500, 3, 150, 60 * 60, 22)}),
    ARCHER_TOWER (TranslationKeys.COC_BUILDING_DEFENSE_ARCHERTOWER_NAME, TranslationKeys.COC_BUILDING_DEFENSE_ARCHERTOWER_DESCRIPTION, new int[]{7, 7}, ResourceTypes.GOLD, Material.BOW, true, 30, 15,null,
            new DefenseLevelData[]{new DefenseLevelData(400, 2, 100, 60, 10),
                    new DefenseLevelData(500, 2, 500, 10 * 60, 20),
                    new DefenseLevelData(600, 3, 750, 3 * 60 * 60, 30)});

    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final ResourceTypes resourceType;
    private final Material material;
    private final boolean attackFlying;
    private final double radius;
    private final long speed;
    private final ITroop prioritizedTroop;
    private final DefenseLevelData[] buildingLevelData;

    DefenseBuildings(String nameKey, String descriptionKey, int[] size, ResourceTypes resourceType, Material material, boolean attackFlying, double radius, long speed, ITroop prioritizedTroop, DefenseLevelData[] buildingLevelData) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.size = size;
        this.resourceType = resourceType;
        this.material = material;
        this.attackFlying = attackFlying;
        this.radius = radius;
        this.speed = speed;
        this.prioritizedTroop = prioritizedTroop;
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
     * Get the troop the defense building priorities.
     * @return {@link ITroop} - the prioritised troop.
     * @since 0.0.1
     */
    @Override
    public ITroop getPrioritizeTroop() {
        return prioritizedTroop;
    }

    /**
     * If flying enemies can be attacked.
     *
     * @return boolean - can also attack flying enemies.
     * @since 0.0.1
     */
    @Override
    public boolean canAttackFlying() {
        return attackFlying;
    }

    /**
     * Get the radius of the defense building.
     * @return double - the radius.
     * @since 0.0.1
     */
    @Override
    public double getRadius() {
        return radius;
    }

    /**
     * Get the speed the defense building attacks in ticks.
     * @return long - the attack speed in ticks.
     * @since 0.0.1
     */
    @Override
    public long getAttackSpeed() {
        return speed;
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
                Arrays.asList(buildingLevelData.getMaxHP()+"", ((DefenseLevelData)buildingLevelData).getDamage()+"", attackFlying+"", getPrioritizeTroop() == null ? "None" : getPrioritizeTroop().getName(language), getRadius()+"")).split(";"));
    }
}
