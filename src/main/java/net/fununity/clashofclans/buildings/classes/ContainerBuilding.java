package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

/**
 * The container building class.
 * @see ResourceContainerBuilding
 * @see TroopsBuilding
 * @author Niko
 * @since 0.0.1
 */
public class ContainerBuilding extends GeneralBuilding {

    private double currentAmount;

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public ContainerBuilding(UUID uuid, IBuilding building, Location coordinate, int level) {
        super(uuid, building, coordinate, level);
        this.currentAmount = 0;
    }

    /**
     * Set the amount of the building.
     * @param currentAmount int - the amount of troops.
     * @since 0.0.1
     */
    public void setAmount(double currentAmount) {
        boolean change = (int) this.currentAmount != (int) currentAmount;
        this.currentAmount = currentAmount;
        if (change)
            Bukkit.getScheduler().runTask(ClashOfClans.getInstance(), () -> PlayerManager.getInstance().forceUpdateInventory(this));
    }

    /**
     * Get the amount of the building.
     * @return int - the amount of thing
     * @since 0.0.1
     */
    public double getAmount() {
        return currentAmount;
    }
}
