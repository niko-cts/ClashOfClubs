package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.DatabaseBuildings;
import net.fununity.clashofclans.buildings.classes.*;
import net.fununity.clashofclans.buildings.interfaces.IBuildingWithHologram;
import net.fununity.clashofclans.buildings.list.Buildings;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * The player class of clash of clans.
 * @author Niko
 * @since 0.0.1
 */
public class CoCPlayer {

    private final UUID uuid;
    private final List<APIPlayer> visitors;
    private final Location location;
    private final Map<ResourceTypes, Integer> resourceMap;
    private final List<GeneralBuilding> buildings;
    private final Object[] buildingMode;
    private int xp;

    /**
     * Instantiates the class.
     * @param uuid UUID - uuid of the player.
     * @param location Location - player base location.
     * @param resourceMap Map<ResourceTypes, Integer> - the resource types with their amount.
     * @param xp int - the players xp
     * @since 0.0.1
     */
    public CoCPlayer(UUID uuid, Location location, Map<ResourceTypes, Integer> resourceMap, int xp) {
        this.uuid = uuid;
        this.visitors = new ArrayList<>();
        this.location = location;
        this.resourceMap = resourceMap;
        this.buildings = new ArrayList<>();
        this.buildingMode = new Object[3];
        this.xp = xp;
    }

    /**
     * A player visits the base.
     * @param apiPlayer APIPlayer - the player who visits.
     * @param teleport boolean - player teleports
     * @since 0.0.1
     */
    public void visit(APIPlayer apiPlayer, boolean teleport) {
        this.visitors.add(apiPlayer);
        apiPlayer.getPlayer().getInventory().clear();
        if (apiPlayer.getUniqueId().equals(uuid)) {
            apiPlayer.getPlayer().getInventory().setItem(8, new ItemBuilder(Material.CLOCK).setName(apiPlayer.getLanguage().getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_NAME)).craft());
            getBuildings().stream().filter(b -> b instanceof IBuildingWithHologram).forEach(b -> ((IBuildingWithHologram) b).getHolograms().forEach(apiPlayer::showHologram));
        }
        if (teleport) {
            Location visitorLoc = getLocation().add(20, 0, 20);
            visitorLoc.setY(LocationUtil.getBlockHeight(location.getWorld(), visitorLoc.getBlockX(), visitorLoc.getBlockZ()) + 1);
            apiPlayer.getPlayer().teleport(visitorLoc);
        }

        apiPlayer.getPlayer().setAllowFlight(true);
    }

    /**
     * A player quits the base.
     * @param apiPlayer APIPlayer - the player who leaves.
     * @since 0.0.1
     */
    public void leave(APIPlayer apiPlayer) {
        this.visitors.remove(apiPlayer);
        getBuildings().stream().filter(b -> b instanceof IBuildingWithHologram).forEach(b -> ((IBuildingWithHologram) b).getHolograms().forEach(apiPlayer::hideHologram));
    }

    /**
     * Get a copied list of all visitors.
     * @return List<APIPlayer> - all visitors.
     * @since 0.0.1
     */
    public List<APIPlayer> getVisitors() {
        return new ArrayList<>(visitors);
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
        return getLocation().add(ClashOfClans.getBaseSize(), 300, ClashOfClans.getBaseSize());
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
     * Get the owner of the base.
     * @return APIPlayer - the player.
     * @since 0.0.1
     */
    public APIPlayer getOwner() {
        return this.visitors.stream().filter(v -> v.getUniqueId().equals(uuid)).findFirst().orElse(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(uuid));
    }

    /**
     * Get the town hall level of the player.
     * @return int - the town hall level.
     * @since 0.0.1
     */
    public int getTownHallLevel() {
        GeneralBuilding townHall = getBuildings().stream().filter(b -> b.getBuilding() == Buildings.TOWN_HALL).findFirst().orElse(null);
        return townHall != null ? townHall.getLevel() : 0;
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

    /**
     * Remove resource from the player.
     * @param type ResourceTypes - the type to remove.
     * @param remove int - the amount to remove.
     * @since 0.0.1
     */
    public void removeResource(ResourceTypes type, int remove) {
        if(remove == 0) return;
        int newResource = getResource(type) - remove;
        resourceMap.put(type, newResource);
        ScoreboardMenu.show(this);

        List<ResourceContainerBuilding> containerBuildings = getContainerBuildings(type);

        int needToRemovePerBuilding = remove / containerBuildings.size();

        for (ResourceContainerBuilding resourceContainerBuilding : containerBuildings) {
            int removing = (int) resourceContainerBuilding.getAmount() - needToRemovePerBuilding;
            int max = Math.max(removing, 0);
            needToRemovePerBuilding += removing - max;
            if (resourceContainerBuilding.getAmount() == 0)
                continue;
            resourceContainerBuilding.setAmount(removing);
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> DatabaseBuildings.getInstance().updateData(resourceContainerBuilding.getCoordinate(), (int) resourceContainerBuilding.getAmount()));
        }
    }

    /**
     * Add resource to the player.
     * @param type ResourceTypes - the resource type.
     * @param add int - the amount of resource
     * @since 0.0.1
     */
    public void addResource(ResourceTypes type, int add) {
        if (add == 0) return;
        List<ResourceContainerBuilding> containerBuildings = getContainerBuildings(type);
        if (containerBuildings.isEmpty()) return;

        resourceMap.put(type, getResource(type) + add);
        ScoreboardMenu.show(this);

        int needToAddPerBuilding = add / containerBuildings.size();

        for (ResourceContainerBuilding resourceContainerBuilding : containerBuildings) {
            int adding = (int) resourceContainerBuilding.getAmount() + needToAddPerBuilding;
            int min = Math.min(adding, resourceContainerBuilding.getMaximumResource());
            needToAddPerBuilding += adding - min;
            if (resourceContainerBuilding.getMaximumResource() == resourceContainerBuilding.getAmount())
                continue;
            resourceContainerBuilding.setAmount(min);
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> DatabaseBuildings.getInstance().updateData(resourceContainerBuilding.getCoordinate(), (int) resourceContainerBuilding.getAmount()));
        }
    }

    /**
     * Set the amount of gems for the player.
     * @param amount int - amount of gems.
     * @since 0.0.1
     */
    public void setGems(int amount) {
        this.resourceMap.put(ResourceTypes.GEMS, amount);
        ScoreboardMenu.show(this);
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> DatabasePlayer.getInstance().setGems(getUniqueId(), amount));
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
     * Updates the resource map from the {@link ResourceContainerBuilding}s.
     * @since 0.0.1
     */
    public void updateResources() {
        for (ResourceContainerBuilding containerBuilding : getContainerBuildings())
            resourceMap.put(containerBuilding.getContainingResourceType(),
                    (int) (resourceMap.getOrDefault(containerBuilding.getContainingResourceType(), 0) + containerBuilding.getAmount()));
    }

    /**
     * All container buildings with the given resource type.
     * @param type ResourceType - the type of resource.
     * @return List<ResourceContainerBuilding> - all resource container buildings with the given type
     * @since 0.0.1
     */
    private List<ResourceContainerBuilding> getContainerBuildings(ResourceTypes type) {
        return getBuildings().stream().filter(b -> b instanceof ResourceContainerBuilding && !(b instanceof ResourceGatherBuilding)).map(list -> (ResourceContainerBuilding) list).filter(b -> b.getContainingResourceType() == type).sorted(Comparator.comparingInt(ResourceContainerBuilding::getMaximumResource)).collect(Collectors.toList());
    }

    /**
     * All container buildings.
     * @return List<ResourceContainerBuilding> - all resource container buildings.
     * @since 0.0.1
     */
    private List<ResourceContainerBuilding> getContainerBuildings() {
        return getBuildings().stream().filter(b -> b instanceof ResourceContainerBuilding && !(b instanceof ResourceGatherBuilding)).map(list -> (ResourceContainerBuilding) list).sorted(Comparator.comparingInt(ResourceContainerBuilding::getMaximumResource)).collect(Collectors.toList());
    }

    /**
     * Get the buildings the player has.
     * @return List<GeneralBuilding> - all buildings.
     * @since 0.0.1
     */
    public List<GeneralBuilding> getBuildings() {
        return buildings;
    }

    /**
     * Add xp to the player's base.
     * @param xp int - xp to add.
     * @since 0.0.1
     */
    public int addXp(int xp) {
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
     * Get the building mode data from the player.
     * @return Object[] - IBuilding/GeneralBuilding, Location, byte rotation
     * @since 0.0.1
     */
    public Object[] getBuildingMode() {
        return buildingMode;
    }

    /**
     * Sets the given objects to the building mode array.
     * @param objects Object... - the objects.
     * @since 0.0.1
     */
    public void setBuildingMode(Object... objects) {
        for (int i = 0; i < objects.length; i++)
            this.buildingMode[i] = objects[i];
    }

    /**
     * Get the maximum amount of containable resource from a type.
     * @param resourceType ResourceTypes - the type of resource.
     * @return int - maximum containable amount.
     * @since 0.0.1
     */
    public int getMaxResourceContainable(ResourceTypes resourceType) {
        int max = 0;
        for (GeneralBuilding building : getBuildings()) {
            if (building instanceof ResourceContainerBuilding && !(building instanceof ResourceGatherBuilding) && ((ResourceContainerBuilding) building).getContainingResourceType() == resourceType)
               max += ((ResourceContainerBuilding) building).getMaximumResource();
        }
        return max;
    }

    /**
     * Get the amount of all troops the player has.
     * @return ConcurrentMap<ITroop, Integer> - Map with troops and amounts.
     * @since 0.0.1
     */
    public ConcurrentMap<ITroop, Integer> getTroops() {
        List<TroopsBuilding> troopBuildings = getBuildings().stream().filter(b -> b instanceof TroopsBuilding && !(b instanceof TroopsCreateBuilding)).map(b -> (TroopsBuilding) b).collect(Collectors.toList());
        ConcurrentMap<ITroop, Integer> troopsAmount = new ConcurrentHashMap<>();
        for (TroopsBuilding troopBuilding : troopBuildings) {
            for (Map.Entry<ITroop, Integer> entry : troopBuilding.getTroopAmount().entrySet()) {
                troopsAmount.put(entry.getKey(), troopsAmount.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return troopsAmount;
    }
}
