package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDifferentVersionBuildings;
import net.fununity.clashofclans.buildings.interfaces.ITroopBuilding;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.troops.ITroop;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The troop building class.
 * @see GeneralBuilding
 * @author Niko
 * @since 0.0.1
 */
public class TroopsBuilding extends GeneralBuilding implements IDifferentVersionBuildings {

    private final ConcurrentMap<ITroop, Integer> troopAmount;

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public TroopsBuilding(UUID uuid, IBuilding building, Location coordinate, byte rotation, int level) {
        this(uuid, building, coordinate, rotation, level, new ConcurrentHashMap<>());
    }

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public TroopsBuilding(UUID uuid, IBuilding building, Location coordinate, byte rotation, int level, ConcurrentMap<ITroop, Integer> troopAmount) {
        super(uuid, building, coordinate, rotation, level);
        this.troopAmount = troopAmount;
    }

    /**
     * Set the amount of troop.
     * @param troop ITroop - the troop.
     * @param amount int - the amount of troop.
     * @since 0.0.1
     */
    public void setTroopAmount(ITroop troop, int amount) {
        int oldVersion = getCurrentBuildingVersion();
        troopAmount.put(troop, amount);
        updateVersion(oldVersion != getCurrentBuildingVersion());
    }

    /**
     * Add the amount of troop.
     * @param troop ITroop - the troop.
     * @param amount int - the amount of troop.
     * @since 0.0.1
     */
    public void addTroopAmount(ITroop troop, int amount) {
        int oldVersion = getCurrentBuildingVersion();
        troopAmount.put(troop, this.troopAmount.getOrDefault(troop, 0) + amount);
        updateVersion(oldVersion != getCurrentBuildingVersion());
    }

    /**
     * Set many troops to the building.
     * @param troops ConcurrentMap<ITroop, Integer> - the troops.
     * @since 0.0.1
     */
    public void setTroopAmount(ConcurrentMap<ITroop, Integer> troops) {
        this.troopAmount.putAll(troops);
        updateVersion(false);
    }

    /**
     * Get the amount of troop.
     * @return Map<ITroop, Integer> - Each troop with their amount
     * @since 0.0.1
     */
    public ConcurrentMap<ITroop, Integer> getTroopAmount() {
        return new ConcurrentHashMap<>(troopAmount);
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

    /**
     * Called when the version was updated.
     * @param schematic boolean - schematic change
     * @since 0.0.1
     */
    @Override
    public void updateVersion(boolean schematic) {
        PlayerManager.getInstance().forceUpdateInventory(this);
        if (schematic)
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> Schematics.createBuilding(this));
    }

    /**
     * Gets the current version of the building.
     * E.g. percentage of fill.
     * @return int - building version
     * @since 0.0.1
     */
    @Override
    public int getCurrentBuildingVersion() {
        return 100 * getCurrentSizeOfTroops() / getMaxAmountOfTroops();
    }
}