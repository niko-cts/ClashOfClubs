package net.fununity.clashofclans.troops;

import net.md_5.bungee.api.event.PreLoginEvent;
import org.bukkit.entity.EntityType;

public enum Troops implements ITroop {
    ;

    private final String nameKey;
    private final String descriptionKey;
    private final TroopType troopType;
    private final int maxHP;
    private final EntityType entityType;
    private final int size;

    Troops(String nameKey, String descriptionKey, TroopType troopType, int maxHP, EntityType entityType, int size) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.troopType = troopType;
        this.maxHP = maxHP;
        this.entityType = entityType;
        this.size = size;
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
}
