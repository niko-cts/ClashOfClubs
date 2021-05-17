package net.fununity.clashofclans.troops;

import net.minecraft.server.v1_16_R3.EntityCreature;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;

public class Troop extends EntityCreature {

    private final ITroop troop;
    private final LivingEntity livingEntity;

    public Troop(ITroop troop, Location location) {
        super(troop.getEntityType(), ((CraftWorld)location.getWorld()).getHandle());
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
