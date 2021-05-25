package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.list.WallBuildings;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

public class PathfinderGoalMoveToNextBuilding extends PathfinderGoal {

    private static final double STANDARD_SPEED = 1F;
    private final Troop troop;

    public PathfinderGoalMoveToNextBuilding(Troop troop) {
        this.a(EnumSet.of(Type.MOVE));
        this.troop = troop;
    }

    @Override
    public boolean a() {
        if (troop.isMoving() || troop.isAttack())
            return false;

        if (troop.getAttackBuilding() != null) {
            if (!TroopsUtil.canTroopAttackBuilding(troop, troop.getAttackBuilding())) {
                GeneralBuilding blockingBuilding = TroopsUtil.getBlockingBuilding(troop);
                troop.setAttackBuilding(blockingBuilding);
                return blockingBuilding != null;
            }
            troop.setAttack(true);
            return false;
        }

        GeneralBuilding nearestBuilding = TroopsUtil.getNearestBuilding(troop, troop.getTroop().getPrioritizedBuilding() != null ?
                Collections.singletonList(troop.getTroop().getPrioritizedBuilding()) : Collections.emptyList(), Arrays.asList(WallBuildings.values()));
        if (nearestBuilding != null) {
            troop.setAttackBuilding(nearestBuilding);
            troop.setMoving(true);
            return true;
        }
        return false;
    }

    /**
     * Will be called, when a returns true.
     * Will call b afterwards.
     * @since 0.0.1
     */
    @Override
    public void c() {
        Location location = TroopsUtil.getAttackBuildingLocation(troop);
        System.out.println("Walking to " + location + " (" + troop.getAttackBuilding().getBuilding() + ")");
        this.troop.getNavigation().a(location.getX(), location.getY(), location.getZ(), STANDARD_SPEED);
    }

    /**
     * Will be called, when b returns true.
     * @return boolean - !finish method, call d().
     * @since 0.0.1
     */
    @Override
    public boolean b() {
        return !this.troop.getNavigation().m();
    }


    /**
     * Will be called, when b returns true.
     * @since 0.0.1
     */
    @Override
    public void d() {
        troop.setMoving(false);
    }
}
