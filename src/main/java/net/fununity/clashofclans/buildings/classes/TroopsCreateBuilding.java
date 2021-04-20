package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.ITroopCreateBuilding;
import net.fununity.clashofclans.troops.Troop;
import org.bukkit.Location;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

/**
 * The troop creation building class.
 * @see TroopsBuilding
 * @author Niko
 * @since 0.0.1
 */
public class TroopsCreateBuilding extends TroopsBuilding {

    private Queue<Troop> troopsQueue;
    private int secondsForNextTroop;

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public TroopsCreateBuilding(UUID uuid, IBuilding building, Location coordinate, int level) {
        super(uuid, building, coordinate, level);
        this.troopsQueue = new LinkedList<>();
        this.secondsForNextTroop = 0;
    }

    public void setSecondsForNextTroop(int secondsForNextTroop) {
        this.secondsForNextTroop = secondsForNextTroop;
    }

    public int getSecondsForNextTroop() {
        return secondsForNextTroop;
    }

    public void setTroopsQueue(Queue<Troop> troopsQueue) {
        this.troopsQueue = troopsQueue;
    }

    /**
     * Get the queue of troops, which will be created.
     * @return Queue<Troop> - The troop creation queue.
     * @since 0.0.1
     */
    public Queue<Troop> getTroopsQueue() {
        return troopsQueue;
    }

    @Override
    public ITroopCreateBuilding getBuilding() {
        return (ITroopCreateBuilding) super.getBuilding();
    }
}
