package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.List;

/**
 * Utility class for {@link Troop} calculations.
 * @author Niko
 * @since 0.0.1
 */
public class TroopsUtil {

    private TroopsUtil() {
        throw new UnsupportedOperationException("TroopsUtil is a utility class.");
    }

    /**
     * Get if the troop is able to attack the building.
     * @param troop Troop - the troop, which attacks.
     * @return boolean - troop can attack building.
     * @since 0.0.1
     */
    public static boolean canTroopAttackBuilding(Troop troop) {
        return troop.getBukkitEntity().getLocation().distance(getAttackBuildingLocation(troop)) <= 1.5;
    }

    /**
     * Get the nearest building.
     * @param troop Troop - the troop to calculate from.
     * @param whitelist List<IBuilding> - the whitelisted building types.
     * @param blacklist List<IBuilding> - the blacklisted building types.
     * @return {@link GeneralBuilding} - the nearest building.
     * @since 0.0.1
     */
    public static GeneralBuilding getNearestBuilding(Troop troop, List<IBuilding> whitelist, List<IBuilding> blacklist) {
        Location location = troop.getBukkitEntity().getLocation();
        return troop.getAttackingManager().getBuildingsOnField().stream()
                .filter(b -> !blacklist.contains(b.getBuilding()) && (whitelist.isEmpty() || whitelist.contains(b.getBuilding())))
                .min(Comparator.comparing(b -> b.getCenterCoordinate().distance(location))).orElse(null);
    }

    /**
     * Calculates the location, where the troop should walk to attack the building.
     * @param troop Troop - the troop to move.
     * @return Location - the location to walk to
     * @since 0.0.1
     */
    public static Location getAttackBuildingLocation(Troop troop) {
        GeneralBuilding attackBuilding = troop.getAttackBuilding();

        Location coordinate = attackBuilding.getCoordinate();
        double halfRange = troop.getTroop().getRange() / 2.0;
        double minX = coordinate.getBlockX() - halfRange;
        double minZ = coordinate.getBlockZ() - halfRange;
        double maxX = minX + attackBuilding.getBuilding().getSize()[0] + troop.getTroop().getRange();
        double maxZ = minZ + attackBuilding.getBuilding().getSize()[1] + troop.getTroop().getRange();

        Location troopLocation = troop.getBukkitEntity().getLocation();
        double troopX = troopLocation.getX();
        double troopZ = troopLocation.getZ();

        double northEast = Math.sqrt(NumberConversions.square(troopX - maxX) + NumberConversions.square(troopZ - maxZ));
        double northWest = Math.sqrt(NumberConversions.square(troopX - minX) + NumberConversions.square(troopZ - maxZ));
        double southEast = Math.sqrt(NumberConversions.square(troopX - maxX) + NumberConversions.square(troopZ - minZ));
        double southWest = Math.sqrt(NumberConversions.square(troopX - minX) + NumberConversions.square(troopZ - minZ));

        double min = Math.min(Math.min(southEast, southWest), Math.min(northEast, northWest));

        if ((min == southEast && northEast > southWest) ||
                (min == southWest && northWest > southEast) ||
                (min == northEast && southEast > northWest) ||
                (min == northWest && southWest > northEast)) {
            // minZ == maxZ

            if (troopLocation.getX() >= maxX)
                return new Location(coordinate.getWorld(), maxX, troopLocation.getY(), minZ);
            if (troopLocation.getX() <= minX)
                return new Location(coordinate.getWorld(), minX, troopLocation.getY(), minZ);

            return new Location(coordinate.getWorld(), troopLocation.getX(), troopLocation.getY(), minZ);
        } else {
            // minX == maxX

            if (troopLocation.getZ() >= maxZ)
                return new Location(coordinate.getWorld(), minX, troopLocation.getY(), maxZ);
            if (troopLocation.getZ() <= minZ)
                return new Location(coordinate.getWorld(), minX, troopLocation.getY(), minZ);

            return new Location(coordinate.getWorld(), minX, troopLocation.getY(), troopLocation.getZ());
        }
    }

    /**
     * Get the blocking building between the troop and the attacking one.
     * @see Troop#getAttackBuilding()
     * @param troop {@link Troop} - the troop.
     * @return {@link GeneralBuilding} - the building, which blocks.
     * @since 0.0.1
     */
    public static GeneralBuilding getBlockingBuilding(Troop troop) {
        Location troopLocation = troop.getBukkitEntity().getLocation().clone();
        Vector vec = getAttackBuildingLocation(troop).toVector().subtract(troopLocation.toVector());
        vec.multiply(0.1);
        for (int i = 0; i < 10; i++) {
            troopLocation.add(vec);
            GeneralBuilding building = troop.getAttackingManager().getBuildingsOnField().stream().filter(b -> LocationUtil.isBetween(b.getCoordinate(), troopLocation, b.getCoordinate().add(b.getBuilding().getSize()[0], 5, b.getBuilding().getSize()[1]))).findFirst().orElse(null);
            if (building != null)
                return building;
        }
        return null;
    }
}
