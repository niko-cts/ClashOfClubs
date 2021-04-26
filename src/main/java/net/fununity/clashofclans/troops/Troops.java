package net.fununity.clashofclans.troops;

import net.fununity.clashofclans.buildings.interfaces.BuildingLevelData;
import net.fununity.clashofclans.buildings.interfaces.IDefenseBuilding;
import net.fununity.clashofclans.buildings.interfaces.IUpgradeDetails;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;

public enum Troops implements ITroop, IUpgradeDetails {
    BARBARIAN(TranslationKeys.COC_TROOPS_BARBARIAN_NAME, TranslationKeys.COC_TROOPS_BARBARIAN_DESCRIPTION, TroopType.LAND, 100, 5.0, EntityType.ZOMBIE, 1, null);

    private final String nameKey;
    private final String descriptionKey;
    private final TroopType troopType;
    private final int maxHP;
    private final double damage;
    private final EntityType entityType;
    private final int size;
    private final IDefenseBuilding prioritizedDefenseBuilding;

    Troops(String nameKey, String descriptionKey, TroopType troopType, int maxHP, double damage, EntityType entityType, int size, IDefenseBuilding defenseBuilding) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.troopType = troopType;
        this.maxHP = maxHP;
        this.damage = damage;
        this.entityType = entityType;
        this.size = size;
        this.prioritizedDefenseBuilding = defenseBuilding;
    }

    /**
     * Get the name key of the troop.
     * @return String - the name key
     * @since 0.0.1
     */
    @Override
    public String getNameKey() {
        return nameKey;
    }

    /**
     * Get the description key of the troop.
     * @return String - the name key
     * @since 0.0.1
     */
    @Override
    public String descriptionKey() {
        return descriptionKey;
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
     * Get the entity type of the troop.
     * @return EntityType - the entity type.
     * @since 0.0.1
     */
    @Override
    public EntityType getEntityType() {
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
     * The building that will be attacked prioritized by the troop.
     * @return IDefenseBuilding - prioritized defense building.
     * @since 0.0.1
     */
    @Override
    public IDefenseBuilding getPrioritizedDefense() {
        return prioritizedDefenseBuilding;
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
                Arrays.asList("${hp}", "${damage}", "${prioritize}", "${size}", "${type}"),
                Arrays.asList(getMaxHP()+"", getDamage()+"", getPrioritizedDefense() == null ? "None" : language.getTranslation(getPrioritizedDefense().getNameKey()), getSize() + "", getTroopType().getName(language))).split(";"));
    }
}
