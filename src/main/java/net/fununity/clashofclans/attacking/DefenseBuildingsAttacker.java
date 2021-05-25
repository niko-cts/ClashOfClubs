package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.classes.DefenseBuilding;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Attacking system for defense buildings.
 * @see PlayerAttackingManager
 * @author Niko
 * @since 0.0.1
 */
public class DefenseBuildingsAttacker {

    private final PlayerAttackingManager attackingManager;
    private final Map<DefenseBuilding, BukkitTask> defenseBuildings;
    private final Map<DefenseBuilding, Troop> attackingTroop;

    /**
     * Instantiates the class.
     * @param attackingManager {@link PlayerAttackingManager} - the attacking instance.
     * @param defenseBuildingList List<DefenseBuilding> - all defense buildings.
     * @since 0.0.1
     */
    public DefenseBuildingsAttacker(PlayerAttackingManager attackingManager, List<DefenseBuilding> defenseBuildingList) {
        this.attackingManager = attackingManager;
        this.defenseBuildings = new HashMap<>();
        this.attackingTroop = new HashMap<>();
        System.out.println(defenseBuildingList);
        for (DefenseBuilding defenseBuilding : defenseBuildingList) {
            BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(ClashOfClubs.getInstance(), () ->
                    checkBuildingAttack(defenseBuilding), defenseBuilding.getBuilding().getAttackSpeed(), defenseBuilding.getBuilding().getAttackSpeed());
            defenseBuildings.put(defenseBuilding, bukkitTask);
        }
    }

    /**
     * Will be called by every defense building in the list.
     * @param defenseBuilding {@link DefenseBuilding} - the defense building.
     * @since 0.0.1
     */
    private void checkBuildingAttack(DefenseBuilding defenseBuilding) {
        if (attackingTroop.containsKey(defenseBuilding)) {
            Troop troop = attackingTroop.get(defenseBuilding);
            if (troop.isAlive() && troop.getBukkitEntity().getLocation().distance(defenseBuilding.getCenterCoordinate()) <= defenseBuilding.getBuilding().getRadius()) {
                attack(defenseBuilding, troop);
                return;
            }
        }
        Location centerCoordinate = defenseBuilding.getCenterCoordinate();
        double radius = defenseBuilding.getBuilding().getRadius();
        this.attackingManager.getTroopsOnField().stream()
                .filter(t -> defenseBuilding.getBuilding().canAttackFlying() || !t.getTroop().isFlying())
                .filter(t -> t.getBukkitEntity().getLocation().distance(centerCoordinate) <= radius)
                .min(Comparator.comparing(t -> t.getBukkitEntity().getLocation().distance(centerCoordinate)))
                .ifPresent(t -> attack(defenseBuilding, t));
    }

    /**
     * A building attacks a troop.
     * @param defenseBuilding {@link DefenseBuilding} - the attacking building.
     * @param troop {@link Troop} - the attacked troop instance.
     * @since 0.0.1
     */
    private void attack(DefenseBuilding defenseBuilding, Troop troop) {
        troop.setHealth((float) (troop.getHealth() - defenseBuilding.getDamage() * defenseBuilding.getBuilding().getAttackSpeed() / 20.0));
        if (troop.getHealth() <= 0) {
            this.attackingManager.removeTroop(troop);
            this.attackingTroop.remove(defenseBuilding);
        } else {
            attackingTroop.put(defenseBuilding, troop);
        }
    }

    /**
     * Will be called, when a defense building was destroyed.
     * @param building {@link DefenseBuilding} - building to remove.
     * @see PlayerAttackingManager#attackBuilding(Troop, GeneralBuilding)
     * @since 0.0.1
     */
    public void removeBuilding(DefenseBuilding building) {
        this.defenseBuildings.get(building).cancel();
        this.defenseBuildings.remove(building);
        this.attackingTroop.remove(building);
    }


}
