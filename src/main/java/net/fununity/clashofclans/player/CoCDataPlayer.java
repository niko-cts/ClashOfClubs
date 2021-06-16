package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

/**
 * The data player class of clash of clans.
 * This class stores uuid, base location, resources xp and elo.
 * @author Niko
 * @since 0.0.1
 */
public class CoCDataPlayer {

    protected final UUID uuid;
    protected final Location location;
    protected final Map<ResourceTypes, Integer> resourceMap;
    private int xp;
    private int elo;

    /**
     * Instantiates the class.
     * @param uuid UUID - uuid of the player.
     * @param location Location - player base location.
     * @param resourceMap Map<ResourceTypes, Integer> - the resource types with their amount.
     * @param xp int - the players xp
     * @param elo int - the players elo
     * @since 0.0.1
     */
    public CoCDataPlayer(UUID uuid, Location location, Map<ResourceTypes, Integer> resourceMap, int xp, int elo) {
        this.uuid = uuid;
        this.location = location;
        this.resourceMap = resourceMap;
        this.xp = xp;
        this.elo = elo;
    }

    /**
     * Get the player base location.
     * @return Location - the player base location.
     * @since 0.0.1
     */
    public Location getLocation() {
        return location.clone();
    }

    /**
     * Get the player base location.
     * @return Location - the player base location.
     * @since 0.0.1
     */
    public Location getEndLocation() {
        return getLocation().add(ClashOfClubs.getBaseSize(), 300, ClashOfClubs.getBaseSize());
    }

    /**
     * The uuid of the player.
     * @return UUID - owners uuid.
     * @since 0.0.1
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Add xp to the player's base.
     * @param xp int - xp to add.
     * @return int - the summed xp.
     * @since 0.0.1
     */
    public int addExp(int xp) {
        return this.xp += xp;
    }

    /**
     * Get the xp from the player.
     * @return int - the players xp
     * @since 0.0.1
     */
    public int getExp() {
        return xp;
    }

    /**
     * Add elo to the player.
     * @param elo int - elo to add.
     * @return int - the summed elo.
     * @since 0.0.1
     */
    public int addElo(int elo) {
        this.elo += elo;
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> DatabasePlayer.getInstance().setElo(getUniqueId(), this.elo));
        return this.elo;
    }

    /**
     * Get the elo from the player.
     * @return int - the players elo
     * @since 0.0.1
     */
    public int getElo() {
        return elo;
    }

    /**
     * Get the amount of current gems the player has.
     * @return int - amount of gems.
     * @since 0.0.1
     */
    public int getGems() {
        return this.resourceMap.get(ResourceTypes.GEMS);
    }

    /**
     * Set the amount of gems for the player.
     * @param amount int - amount of gems.
     * @since 0.0.1
     */
    public void setGems(int amount) {
        this.resourceMap.put(ResourceTypes.GEMS, amount);
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> DatabasePlayer.getInstance().setGems(getUniqueId(), amount));
    }

    /**
     * Get the amount of a resource.
     * @param resourceTypes ResourceTypes - the type of resource.
     * @return int - amount of resource
     * @since 0.0.1
     */
    public int getResource(ResourceTypes resourceTypes) {
        return resourceMap.get(resourceTypes);
    }
}
