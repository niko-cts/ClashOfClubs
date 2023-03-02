package net.fununity.clashofclans.util;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.BuildingsMoveManager;
import net.fununity.clashofclans.buildings.instances.DefenseBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDefenseBuilding;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class CircleParticleUtil {

    private CircleParticleUtil() {
        throw new UnsupportedOperationException("RadiusParticleUtil is a utility class");
    }

    private static final Map<Location, BukkitTask> TASK_MAP = new HashMap<>();


    /**
     * Displays particles in a circle with given radius.
     * @param location Location - Center location.
     * @param radius double - circle radius
     */
    public static void displayRadius(Location location, double radius) {
        if (TASK_MAP.containsKey(location)) return;
        TASK_MAP.put(location, Bukkit.getScheduler().runTaskTimer(ClashOfClubs.getInstance(), () -> spawnParticle(location, radius), 0, 20L));
    }

    /**
     * Displays particles in a circle with given radius in a defined timespan.
     * @param location Location - Center location.
     * @param radius double - circle radius
     * @param timeInSeconds int - seconds to display
     */
    public static void displayRadius(Location location, double radius, int timeInSeconds) {
        if (TASK_MAP.containsKey(location)) return;
        displayRadius(location, radius);
        Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () -> hideRadius(location), timeInSeconds * 20L);
    }

    /**
     * Spawns particles around a location in given radius.
     * @param location Location - the center location.
     * @param radius double - the radius of the circle.
     * @since 0.0.2
     */
    private static void spawnParticle(Location location, double radius) {
        for (int d = 0; d <= 90; d++) {
            Location particleLoc = location.clone();
            particleLoc.setY(ClashOfClubs.getBaseYCoordinate() + 2);
            particleLoc.setX(location.getX() + Math.cos(d) * radius);
            particleLoc.setZ(location.getZ() + Math.sin(d) * radius);
            location.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, new Particle.DustOptions(Color.BLUE, 5));
        }
    }

    /**
     * Hides particles.
     * @param location Location - Center location.
     */
    public static void hideRadius(Location location) {
        BukkitTask task = TASK_MAP.getOrDefault(location, null);
        if (task != null) {
            task.cancel();
            TASK_MAP.remove(location);
        }
    }


    /**
     * Creates the particle circle based of the building mode.
     * @param buildingMode Object[] - the players building mode data
     */
    public static void createParticleTask(Object[] buildingMode) {
        if (buildingMode[1] instanceof DefenseBuilding || buildingMode[1] instanceof IDefenseBuilding) {
            IDefenseBuilding defenseBuilding = buildingMode[1] instanceof IDefenseBuilding ? (IDefenseBuilding) buildingMode[1] : ((DefenseBuilding) buildingMode[1]).getBuilding();
            displayRadius(BuildingLocationUtil.getCenterLocation(buildingMode), defenseBuilding.getRadius());
        }
    }

    /**
     * Deletes the particle task based on the building mode.
     * @param buildingMode Object[] - the players building mode data
     */
    public static void deleteParticleTask(Object[] buildingMode) {
        if (buildingMode[1] instanceof DefenseBuilding || buildingMode[1] instanceof IDefenseBuilding) {
            hideRadius(BuildingLocationUtil.getCenterLocation(buildingMode));
        }
    }
}
