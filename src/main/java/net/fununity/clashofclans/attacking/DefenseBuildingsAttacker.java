package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.buildings.classes.DefenseBuilding;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefenseBuildingsAttacker {

    private final AttackingManager attackingManager;
    private final Map<DefenseBuilding, BukkitTask> defenseBuildings;
    private final Map<DefenseBuilding, Troop> attackingBuilding;

    public DefenseBuildingsAttacker(AttackingManager attackingManager, List<DefenseBuilding> defenseBuildingList) {
        this.attackingManager = attackingManager;
        this.defenseBuildings = new HashMap<>();
        this.attackingBuilding = new HashMap<>();
        for (DefenseBuilding defenseBuilding : defenseBuildingList) {
            BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(ClashOfClans.getInstance(), () -> checkBuildingAttack(defenseBuilding), 20L, 20L);
            defenseBuildings.put(defenseBuilding, bukkitTask);
        }
    }

    private void checkBuildingAttack(DefenseBuilding defenseBuilding) {
        if (attackingBuilding.containsKey(defenseBuilding)) {
            Troop troop = attackingBuilding.get(defenseBuilding);
            if (troop.isAlive())
                attack(defenseBuilding, troop);
            return;
        }
        Location centerCoordinate = defenseBuilding.getCenterCoordinate();
        double radius = defenseBuilding.getBuilding().getRadius();
        attackingManager.getTroopsOnField().stream()
                .filter(t -> defenseBuilding.getBuilding().attackFlying() || !t.getTroop().isFlying())
                .filter(t -> t.getBukkitEntity().getLocation().distance(centerCoordinate) <= radius)
                .min(Comparator.comparing(t -> t.getBukkitEntity().getLocation().distance(centerCoordinate) <= radius))
                .ifPresent(b -> attack(defenseBuilding, b));
    }

    private void attack(DefenseBuilding defenseBuilding, Troop troop) {
        troop.setHealth((float) (troop.getHealth() - defenseBuilding.getDamage()));
        if (troop.getHealth() <= 0) {
            troop.die();
            attackingBuilding.remove(defenseBuilding);
        }
    }

    public void removeBuilding(DefenseBuilding building) {
        defenseBuildings.get(building).cancel();
        defenseBuildings.remove(building);
        attackingBuilding.remove(building);
    }


}
