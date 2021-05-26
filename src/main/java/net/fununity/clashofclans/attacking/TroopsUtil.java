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
     * @param troop Troop - the troop to attack.
     * @param attackBuilding {@link GeneralBuilding} - the building to attack.
     * @return boolean - troop can attack building.
     * @since 0.0.1
     */
    public static boolean canTroopAttackBuilding(Troop troop, GeneralBuilding attackBuilding) {
        Location centerBuilding = attackBuilding.getCenterCoordinate();
        Location troopLoc = troop.getBukkitEntity().getLocation();
        Vector vec = troopLoc.toVector().subtract(centerBuilding.toVector());
        vec.multiply(Math.sqrt(NumberConversions.square(attackBuilding.getBuilding().getSize()[0] / 2.0) +
                NumberConversions.square(attackBuilding.getBuilding().getSize()[1] / 2.0)) / vec.length() -
                troop.getTroop().getRange() / vec.length());

        centerBuilding.add(vec);
        centerBuilding.setY(troopLoc.getY());

        return troopLoc.distance(centerBuilding) <= troop.getTroop().getRange() + 1.5;
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
     * Get the location, where the troop should walk to, to attack the building.
     * @param troop Troop - the troop to move.
     * @return Location - the location to walk to
     * @since 0.0.1
     */
    public static Location getAttackBuildingLocation(Troop troop) {
        GeneralBuilding attackBuilding = troop.getAttackBuilding();

        Locatioon coordinate attackBuilding.getCoordinate();
        int minX = coordinate.getBlockX();
        int minZ = coordinate.getBlockZ();
        int maxX = minX + attackBuilding.getBuilding().getSize()[0];
        int maxZ = minZ + attackBuilding.getBuilding().getSize()[1];

        Locatioon troopLocation = troop.getBukkitEntity().getLocation();
        double troopX = troopLocation.getX();
        double troopZ = troopLocation.getZ();

        int southEast = Math.sqrt(NumberConversions.square(troopX - maxX) + 
            NumberConversions.square(troopZ - minZ));
        int southWest = Math.sqrt(NumberConversions.square(troopX - minX) + 
            NumberConversions.square(troopZ - minZ));
        int northEast = Math.sqrt(NumberConversions.square(troopX - maxX) + 
            NumberConversions.square(troopZ - maxZ));
        int northWest = Math.sqrt(NumberConversions.square(troopX - minX) + 
            NumberConversions.square(troopZ - maxZ));
        
        int min = Math.min(Math.min(southEast, southWest), Math.min(northEast, northWest));
        if (min == southEast) {
            if (Math.min(southWest, northEast) == southWest) { // southEast, southWest
                Vector line = new Vector(maxX - minX, 0, maxZ - minZ);
            } else { // southEast, northEast
                Vector line = new Vector(0, 0, maxZ - minZ);
            }
        } else if(min == southWest) {
            
        }

        

        Vector vec = troop.getBukkitEntity().getLocation().toVector().subtract(attackBuilding.getCenterCoordinate().toVector());
        vec.multiply(Math.sqrt(NumberConversions.square(attackBuilding.getBuilding().getSize()[0] / 2.0) +
                NumberConversions.square(attackBuilding.getBuilding().getSize()[1] / 2.0)) / vec.length() -
                troop.getTroop().getRange() / vec.length() - 0.09);

        System.out.println("1 " + troop.getBukkitEntity().getLocation() + " <- " + attackBuilding.getCenterCoordinate() + " " +
                (Math.sqrt(NumberConversions.square(attackBuilding.getBuilding().getSize()[0] / 2.0) +
                        NumberConversions.square(attackBuilding.getBuilding().getSize()[1] / 2.0)) / vec.length()) +
                " " + (troop.getTroop().getRange() / vec.length()));

        Location walkLoc = attackBuilding.getCenterCoordinate().add(vec);
        walkLoc.setY(LocationUtil.getBlockHeight(walkLoc));
        return walkLoc;
    }

    /**
     * Get the blocking building between the troop and the attacking one.
     * @see Troop#getAttackBuilding()
     * @param troop {@link Troop} - the troop.
     * @return {@link GeneralBuilding} - the building, which blocks.
     * @since 0.0.1
     */
    public static GeneralBuilding getBlockingBuilding(Troop troop) {
        GeneralBuilding attackBuilding = troop.getAttackBuilding();
        Location troopLocation = troop.getBukkitEntity().getLocation().clone();
        Vector vec = attackBuilding.getCenterCoordinate().toVector().subtract(troopLocation.toVector());
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
