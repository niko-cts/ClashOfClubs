package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import org.bukkit.Location;
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
        Vector vec = centerBuilding.toVector().multiply(-1).add(troop.getBukkitEntity().getLocation().toVector())
                .add(new Vector(attackBuilding.getBuilding().getSize()[0] / 2.0 + troop.getTroop().getRange(), 0,
                        attackBuilding.getBuilding().getSize()[1] / 2.0 + troop.getTroop().getRange()));

        return troop.getBukkitEntity().getLocation().distance(centerBuilding.add(vec)) <= troop.getTroop().getRange() + 1;
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
        return troop.getAttackingManager().getBuildingsOnField().stream().filter(b ->
                        !blacklist.contains(b.getBuilding()) && (whitelist.isEmpty() || whitelist.contains(b.getBuilding())))
                .min(Comparator.comparing(o1 -> o1.getCenterCoordinate().distance(location))).orElse(null);
    }

    /**
     * Get the location, where the troop should walk to, to attack the building.
     * @param troop Troop - the troop to move.
     * @return Location - the location to walk to
     * @since 0.0.1
     */
    public static Location getAttackBuildingLocation(Troop troop) {
        GeneralBuilding attackBuilding = troop.getAttackBuilding();
        Vector vec = attackBuilding.getCenterCoordinate().toVector().multiply(-1).add(troop.getBukkitEntity().getLocation().toVector())
                .add(new Vector(attackBuilding.getBuilding().getSize()[0] / 2.0 + troop.getTroop().getRange(), 0,
                        attackBuilding.getBuilding().getSize()[1] / 2.0 + troop.getTroop().getRange()));
        return troop.getBukkitEntity().getLocation().clone().add(vec.multiply(-1));
    }
}
