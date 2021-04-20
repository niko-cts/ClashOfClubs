package net.fununity.clashofclans.troops;

import org.bukkit.entity.LivingEntity;

public class Troop {

    private final ITroop troop;
    private final LivingEntity livingEntity;

    public Troop(ITroop troop, LivingEntity entity) {
        this.troop = troop;
        this.livingEntity = entity;
    }

    public ITroop getTroop() {
        return troop;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }
}
