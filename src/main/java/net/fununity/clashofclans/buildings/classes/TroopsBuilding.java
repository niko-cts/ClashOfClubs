package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.ITroopBuilding;
import net.fununity.clashofclans.troops.ITroop;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The troop building class.
 * @see ContainerBuilding
 * @author Niko
 * @since 0.0.1
 */
public class TroopsBuilding extends GeneralBuilding {

    private final ConcurrentMap<ITroop, Integer> troopAmount;

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public TroopsBuilding(UUID uuid, IBuilding building, Location coordinate, int level) {
        super(uuid, building, coordinate, level);
        this.troopAmount = new ConcurrentHashMap<>();
    }

    /**
     * Get the amount of troop.
     * @return Map<ITroop, Integer> - Each troop with their amount
     * @since 0.0.1
     */
    public ConcurrentMap<ITroop, Integer> getTroopAmount() {
        return troopAmount;
    }

    @Override
    public ITroopBuilding getBuilding() {
        return (ITroopBuilding) super.getBuilding();
    }

    /**
     * Get the max amount of troops that fit in this building.
     * @return int - the troop size.
     * @since 0.0.1
     */
    public int getMaxAmountOfTroops() {
        return getBuilding().getBuildingLevelData()[getLevel() - 1].getMaxAmountOfTroops();
    }

    /**
     * Get the current amount * size of all troops in the building.
     * @return int - all troops
     * @since 0.0.1
     */
    public int getCurrentSizeOfTroops() {
        int amount = 0;
        for (Map.Entry<ITroop, Integer> entry : getTroopAmount().entrySet())
            amount += entry.getValue() * entry.getKey().getSize();
        return amount;
    }
}
