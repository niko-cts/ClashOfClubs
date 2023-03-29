package net.fununity.clashofclans.troops;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IUpgradeDetails;
import net.fununity.clashofclans.buildings.interfaces.data.BuildingLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.misc.translationhandler.translations.Language;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Troops implements ITroop, IUpgradeDetails {
    BARBARIAN(TranslationKeys.COC_TROOPS_BARBARIAN_NAME, TranslationKeys.COC_TROOPS_BARBARIAN_DESCRIPTION, TroopType.LAND, 50, 10.0, 0.3, false, EntityTypes.ZOMBIE, Material.STONE_SWORD,1, 1, 10, 10,null);

    private final String nameKey;
    private final String descriptionKey;
    private final TroopType troopType;
    private final int maxHP;
    private final double damage;
    private final double range;
    private final boolean flying;
    private final EntityTypes<? extends EntityCreature> entityType;
    private final Material representativeItem;
    private final int minBarracks;
    private final int size;
    private final int trainDuration;
    private final int amountOfCost;
    private final IBuilding prioritizedBuilding;

    Troops (String nameKey, String descriptionKey, TroopType troopType, int maxHP, double damage, double range, boolean flying, EntityTypes<? extends EntityCreature> entityType, Material representativeItem, int minBarrackLevel, int size, int trainDuration, int amountOfCost, IBuilding prioritizedBuilding) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.troopType = troopType;
        this.maxHP = maxHP;
        this.damage = damage;
        this.range = range;
        this.flying = flying;
        this.entityType = entityType;
        this.representativeItem = representativeItem;
        this.minBarracks = minBarrackLevel;
        this.size = size;
        this.trainDuration = trainDuration;
        this.amountOfCost = amountOfCost;
        this.prioritizedBuilding = prioritizedBuilding;
    }

    /**
     * Gets the troop by its material.
     * @param material Material - the representative material.
     * @return {@link ITroop} - the troop.
     * @since 0.0.1
     */
    public static ITroop getByMaterial(Material material) {
        return Arrays.stream(values()).filter(t -> t.getRepresentativeItem() == material).findFirst().orElse(null);
    }


    /**
     * Get the name of the troop.
     * @param language Language - the language to translate.
     * @return String - the name
     * @since 0.0.1
     */
    @Override
    public String getName(Language language) {
        return language.getTranslation(nameKey);
    }

    /**
     * Get the description key of the troop.
     * @param language Language - the language to translate.
     * @return String - the description.
     * @since 0.0.1
     */
    @Override
    public String[] getDescription(Language language) {
        List<String> lore = new ArrayList<>(Arrays.asList(language.getTranslation(descriptionKey).split(";")));
        lore.addAll(getLoreDetails(null, language));
        return lore.toArray(new String[0]);
    }

    /**
     * Get the type of troop.
     * @return TroopType - The type of troop
     * @since 0.0.1
     */
    @Override
    public TroopType getTroopType() {
        return troopType;
    }

    /**
     * Get the max hp of the troop.
     * @return int - the max hp.
     * @since 0.0.1
     */
    @Override
    public int getMaxHP() {
        return maxHP;
    }

    /**
     * Get the damage of the troop.
     * @return double - the damage.
     * @since 0.0.1
     */
    @Override
    public double getDamage() {
        return damage;
    }

    /**
     * Get the range the entity can attack.
     * @return double - attack range.
     * @since 0.0.1
     */
    @Override
    public double getRange() {
        return range;
    }

    /**
     * Get the entity type of the troop.
     * @return EntityTypes<? extends EntityCreature> - the entity type.
     * @since 0.0.1
     */
    @Override
    public EntityTypes<? extends EntityCreature> getEntityType() {
        return entityType;
    }

    /**
     * The size the troop needs.
     * @return int - needed space.
     * @since 0.0.1
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Get if the troop is flying.
     * @return boolean - is flying.
     * @since 0.0.1
     */
    @Override
    public boolean isFlying() {
        return flying;
    }

    /**
     * Get the minimum level of the barrack to train this troop.
     * @return int - minimum barrack level.
     * @since 0.0.1
     */
    @Override
    public int getMinBarracksLevel() {
        return minBarracks;
    }

    /**
     * Get the amount of seconds to train this troop.
     * @return int - train duration in seconds.
     * @since 0.0.1
     */
    @Override
    public int getTrainDuration() {
        return trainDuration;
    }

    /**
     * Get the price amount per unit.
     * @return int - amount of resource to train.
     * @since 0.0.1
     */
    @Override
    public int getCostAmount() {
        return amountOfCost;
    }

    /**
     * Get the representative item for this entity.
     * @return Material - the material.
     * @since 0.0.1
     */
    @Override
    public Material getRepresentativeItem() {
        return representativeItem;
    }

    /**
     * The building that will be attacked prioritized by the troop.
     * @return {@link IBuilding} - prioritized building.
     * @since 0.0.1
     */
    @Override
    public IBuilding getPrioritizedBuilding() {
        return prioritizedBuilding;
    }

    /**
     * Get the lore details for upgrade and build.
     * @param buildingLevelData {@link BuildingLevelData} - the level data instance.
     * @param language          Language - the language to translate to.
     * @return List<String> - Further lore details.
     * @since 0.0.1
     */
    @Override
    public List<String> getLoreDetails(BuildingLevelData buildingLevelData, Language language) {
        return Arrays.asList(language.getTranslation(TranslationKeys.COC_TROOPS_LOREDETAILS,
                Arrays.asList("${hp}", "${damage}", "${prioritize}", "${size}", "${type}", "${cost}"),
                Arrays.asList(getMaxHP()+"", getDamage()+"", getPrioritizedBuilding() == null ? "None" : language.getTranslation(getPrioritizedBuilding().getNameKey()), getSize() + "", getTroopType().getName(language), ResourceTypes.FOOD.getChatColor() + " " +
                        getCostAmount() + " " + ResourceTypes.FOOD.getColoredName(language))).split(";"));
    }
}
