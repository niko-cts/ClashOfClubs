package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.instances.ConstructionBuilding;
import net.fununity.clashofclans.buildings.instances.DefenseBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralHologramBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceContainerBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsCreateBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IResourceContainerBuilding;
import net.fununity.clashofclans.buildings.interfaces.ITroopBuilding;
import net.fununity.clashofclans.buildings.list.*;
import net.fununity.clashofclans.player.buildingmode.ConstructionMode;
import net.fununity.clashofclans.player.buildingmode.IBuildingMode;
import net.fununity.clashofclans.player.buildingmode.MovingMode;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.clashofclans.util.BuildingsAmountUtil;
import net.fununity.clashofclans.util.CircleParticleUtil;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The player class of clash of clans.
 * This class stores buildings, visitors and building mode according to the owner.
 * @author Niko
 * @since 0.0.1
 */
public class CoCPlayer {

    private final UUID uuid;
    private final Location baseLocation;
    private int xp;
    private int elo;
    private int gems;
    private final String lastServer;
    private final long lastJoin;

    private final List<GeneralBuilding> normalBuildings;
    private final Map<ResourceTypes, List<ResourceContainerBuilding>> resourceBuildings;
    private final List<TroopsBuilding> troopsBuildings;
    private final List<DefenseBuilding> defenseBuildings;
    private final List<ConstructionBuilding> constructionBuildings;
    private final List<UUID> visitors;

    private IBuildingMode buildingMode;

    /**
     * Instantiates the class.
     * @param uuid UUID - uuid of the player.
     * @param baseLocation Location - player base location.
     * @param xp int - the players xp
     * @param elo int - the players elo
     * @param gems int - the players gems
     * @param lastJoin long - the last join milli secs.
     * @param lastServer String - the last server.
     * @param allBuildings List<GeneralBuilding> - all buildings the player has.
     * @since 0.0.1
     */
    public CoCPlayer(UUID uuid, Location baseLocation, int xp, int elo, int gems, String lastServer, long lastJoin, List<GeneralBuilding> allBuildings) {
        this.uuid = uuid;
        this.baseLocation = baseLocation;
        this.xp = xp;
        this.elo = elo;
        this.gems = gems;
        this.lastServer = lastServer;
        this.lastJoin = lastJoin;
        this.visitors = new ArrayList<>();
        this.buildingMode = null;

        this.normalBuildings = new ArrayList<>();
        this.resourceBuildings = new HashMap<>();
        for (ResourceTypes type : ResourceTypes.values()) {
            resourceBuildings.put(type, new ArrayList<>());
        }
        this.troopsBuildings = new ArrayList<>();
        this.defenseBuildings = new ArrayList<>();
        this.constructionBuildings = new ArrayList<>();

        for (GeneralBuilding generalBuilding : allBuildings) {
            addBuilding(generalBuilding);
        }

     }

    /**
     * A player visits the base.
     * @param apiPlayer APIPlayer - the player who visits.
     * @param teleport boolean - player teleports
     * @since 0.0.1
     */
    public void visit(APIPlayer apiPlayer, boolean teleport) {
        this.visitors.add(apiPlayer.getUniqueId());
        if (teleport) {
            Location visitorLoc = getBaseStartLocation().add(ClashOfClubs.getBaseSize() / 2.0, 0, ClashOfClubs.getBaseSize() / 2.0);
            visitorLoc.setY(BuildingLocationUtil.getHighestYCoordinate(visitorLoc) + 1);
            apiPlayer.getPlayer().teleport(visitorLoc);
        }

        apiPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
        apiPlayer.getPlayer().setAllowFlight(true);
        apiPlayer.getPlayer().getInventory().clear();
        apiPlayer.getPlayer().setLevel(0);
        apiPlayer.getPlayer().setExp(0);
        apiPlayer.getPlayer().giveExp(getExp());

        if (apiPlayer.getUniqueId().equals(uuid)) {
            getAllBuildings().stream().filter(b -> b instanceof GeneralHologramBuilding).forEach(b -> ((GeneralHologramBuilding) b).updateHologram(((GeneralHologramBuilding) b).getShowText()));
            ClashOfClubs.getInstance().getPlayerManager().giveDefaultItems(this, apiPlayer);
        }
    }


    /**
     * A player quits the base.
     * @param apiPlayer APIPlayer - the player who leaves.
     * @since 0.0.1
     */
    public void leave(APIPlayer apiPlayer) {
        this.visitors.remove(apiPlayer.getUniqueId());
        getAllBuildings().stream().filter(b -> b instanceof GeneralHologramBuilding).forEach(b -> ((GeneralHologramBuilding) b).hideHologram(apiPlayer));
    }

    /**
     * Get a copied list of all visitors.
     * @return List<UUID> - all visitors.
     * @since 0.0.1
     */
    public List<UUID> getVisitors() {
        return new ArrayList<>(visitors);
    }

    /**
     * Get the owner of the base.
     * @return APIPlayer - the player.
     * @since 0.0.1
     */
    public APIPlayer getOwner() {
        return FunUnityAPI.getInstance().getPlayerHandler().getPlayer(uuid);
    }

    /**
     * Get the town hall level of the player.
     * @return int - the town hall level.
     * @since 0.0.1
     */
    public int getTownHallLevel() {
        List<GeneralBuilding> buildings = getNormalBuildings();
        buildings.addAll(getConstructionBuildings());

        GeneralBuilding townHall = buildings.stream().filter(b -> b.getBuilding() == Buildings.TOWN_HALL).findFirst().orElse(null);

        return townHall != null ? townHall.getLevel() : 0;
    }


    /**
     * Gets the resource amount the player currently has.
     * @param type ResourceType - type of resource.
     * @return double - amount of resource
     */
    public double getResourceAmount(ResourceTypes type) {
        if (type == ResourceTypes.GEMS)
           return getGems();
        int amount = 0;
        for (ResourceContainerBuilding building : getResourceContainerBuildings(type)) {
            amount += building.getAmount();
        }
        return amount;
    }

    /**
     * Remove resource from the player.
     * @param type ResourceTypes - the type to remove.
     * @param remove int - the amount to remove.
     * @since 0.0.1
     */
    public void removeResource(ResourceTypes type, int remove) {
        if (remove == 0) return;
        List<ResourceContainerBuilding> containerBuildings = getResourceContainerBuildings(type);
        if (containerBuildings.isEmpty()) return;

        containerBuildings.sort(Comparator.comparingInt(ResourceContainerBuilding::getMaximumResource));

        int needToRemovePerBuilding = remove / containerBuildings.size();

        for (int i = 0; i < containerBuildings.size(); i++) {
            if (remove <= 0)
                break;

            ResourceContainerBuilding resourceContainerBuilding = containerBuildings.get(i);
            if (resourceContainerBuilding.getMaximumResource() == resourceContainerBuilding.getAmount())
                continue;

            int newAmount;
            int summedAmount = (int) resourceContainerBuilding.getAmount() - needToRemovePerBuilding;

            if (summedAmount >= 0) {
                newAmount = summedAmount;
                remove -= needToRemovePerBuilding;
            } else {
                newAmount = 0;
                remove -= resourceContainerBuilding.getAmount();
                needToRemovePerBuilding = remove / (containerBuildings.size() - i);
            }

            resourceContainerBuilding.setAmount(newAmount);
        }
    }

    /**
     * Add resource to the player.
     * @param type ResourceTypes - the resource type.
     * @param add double - the amount of resource
     * @return List<ResourceContainerBuilding> - buildings that need a rebuild
     * @since 0.0.1
     */
    public List<ResourceContainerBuilding> fillResourceToContainer(ResourceTypes type, double add) {
        List<ResourceContainerBuilding> toRebuildBuildings = new ArrayList<>();

        List<ResourceContainerBuilding> containerBuildings = getResourceContainerBuildings(type);
        if (containerBuildings.isEmpty()) return toRebuildBuildings;

        containerBuildings.sort(Comparator.comparingInt(ResourceContainerBuilding::getMaximumResource));

        double needToAddPerBuilding = add / containerBuildings.size();

        for (int i = 0; i < containerBuildings.size(); i++) {
            if (add <= 0)
                break;

            ResourceContainerBuilding resourceContainerBuilding = containerBuildings.get(i);
            if (resourceContainerBuilding.getMaximumResource() == resourceContainerBuilding.getAmount())
                continue;

            double newAmount;
            double summedAmount = needToAddPerBuilding + resourceContainerBuilding.getAmount();

            if (summedAmount <= resourceContainerBuilding.getMaximumResource()) {
                newAmount = summedAmount;
                add -= needToAddPerBuilding;
            } else {
                newAmount = resourceContainerBuilding.getMaximumResource();
                add -= summedAmount - resourceContainerBuilding.getMaximumResource();
                needToAddPerBuilding = add / (containerBuildings.size() - i);
            }

            if (resourceContainerBuilding.setAmount(newAmount))
                toRebuildBuildings.add(resourceContainerBuilding);
        }

        ScoreboardMenu.show(this);
        return toRebuildBuildings;
    }


    /**
     * All container buildings from a type.
     * @param type ResourceType - type of container
     * @return List<ResourceContainerBuilding> - all resource container buildings.
     * @since 0.0.1
     */
    public List<ResourceContainerBuilding> getResourceContainerBuildings(ResourceTypes type) {
        return resourceBuildings.get(type).stream().filter(r -> !(r instanceof ResourceGatherBuilding)).collect(Collectors.toList());
    }

    /**
     * All container buildings from a type.
     * @param type ResourceType - type of container
     * @return List<ResourceContainerBuilding> - all resource container buildings.
     * @since 0.0.1
     */
    public List<ResourceGatherBuilding> getResourceGatherBuildings(ResourceTypes type) {
        return resourceBuildings.get(type).stream().filter(r -> (r instanceof ResourceGatherBuilding)).map(r -> (ResourceGatherBuilding) r).collect(Collectors.toList());
    }

    /**
     * Get all troop buildings the player has.
     * @return List<TroopsBuilding> - all buildings.
     * @since 0.0.1
     */
    public List<TroopsCreateBuilding> getTroopsCreateBuildings() {
        return troopsBuildings.stream().filter(t -> t instanceof TroopsCreateBuilding).map(t -> (TroopsCreateBuilding) t).collect(Collectors.toList());
    }

    /**
     * Get all troop buildings the player has.
     * @return List<TroopsBuilding> - all buildings.
     * @since 0.0.1
     */
    public List<TroopsBuilding> getTroopsCampBuildings() {
        return troopsBuildings.stream().filter(t -> !(t instanceof TroopsCreateBuilding)).sorted(Comparator.comparingInt(TroopsBuilding::getCurrentSizeOfTroops)).collect(Collectors.toList());
    }

    /**
     * Get all defense buildings the player has.
     * @return List<DefenseBuilding> - all defense buildings.
     * @since 0.0.2
     */
    public List<DefenseBuilding> getDefenseBuildings() {
        return new ArrayList<>(defenseBuildings);
    }

    /**
     * Get all construction buildings the player has.
     * @return List<ConstructionBuilding> - all buildings.
     * @since 0.0.1
     */
    public List<ConstructionBuilding> getConstructionBuildings() {
        return new ArrayList<>(constructionBuildings);
    }

    /**
     * Get all normal buildings the player has.
     * @return List<GeneralBuilding> - all normal buildings.
     * @since 0.0.1
     */
    public List<GeneralBuilding> getNormalBuildings() {
        return new ArrayList<>(normalBuildings);
    }

    /**
     * Get all buildings the player has.
     * @return List<GeneralBuilding> - all buildings.
     * @since 0.0.1
     */
    public List<GeneralBuilding> getAllBuildings() {
        List<GeneralBuilding> allBuildings = new ArrayList<>();
        allBuildings.addAll(normalBuildings);
        allBuildings.addAll(troopsBuildings);
        allBuildings.addAll(defenseBuildings);
        allBuildings.addAll(constructionBuildings);
        for (ResourceTypes type : ResourceTypes.values()) {
            allBuildings.addAll(resourceBuildings.get(type));
        }
        return allBuildings;
    }


    /**
     * Removes a building from the list
     * @param generalBuilding GeneralBuilding - the building
     */
    public void removeBuilding(GeneralBuilding generalBuilding) {
        if(generalBuilding instanceof ConstructionBuilding)
            constructionBuildings.remove((ConstructionBuilding) generalBuilding);
        else if (generalBuilding instanceof ResourceContainerBuilding) {
            List<ResourceContainerBuilding> list = resourceBuildings.get(((ResourceContainerBuilding) generalBuilding).getContainingResourceType());
            list.remove((ResourceContainerBuilding) generalBuilding);
        } else if (generalBuilding instanceof TroopsBuilding)
            troopsBuildings.remove((TroopsBuilding) generalBuilding);
        else
            normalBuildings.remove(generalBuilding);
    }

    /**
     * Adds a building to the list
     * @param generalBuilding GeneralBuilding - the building
     */
    public void addBuilding(GeneralBuilding generalBuilding) {
        if (generalBuilding instanceof ConstructionBuilding)
            constructionBuildings.add((ConstructionBuilding) generalBuilding);
        else if (generalBuilding instanceof ResourceContainerBuilding) {
            List<ResourceContainerBuilding> list = resourceBuildings.get(((ResourceContainerBuilding) generalBuilding).getContainingResourceType());
            list.add((ResourceContainerBuilding) generalBuilding);
        } else if (generalBuilding instanceof TroopsBuilding)
            troopsBuildings.add((TroopsBuilding) generalBuilding);
        else if(generalBuilding instanceof DefenseBuilding)
            defenseBuildings.add((DefenseBuilding) generalBuilding);
        else
            normalBuildings.add(generalBuilding);
    }

    public void removeConstructions(List<ConstructionBuilding> constructions) {
        constructionBuildings.removeAll(constructions);
    }

    public void addConstructions(List<ConstructionBuilding> constructions) {
        constructionBuildings.addAll(constructions);
    }

    /**
     * Get the maximum amount of containable resource from a type.
     * @param resourceType ResourceTypes - the type of resource.
     * @return int - maximum containable amount.
     * @since 0.0.1
     */
    public int getMaxResourceContainable(ResourceTypes resourceType) {
        int max = 0;
        for (ResourceContainerBuilding containerBuilding : getResourceContainerBuildings(resourceType))
            max += containerBuilding.getMaximumResource();
        return max;
    }

    /**
     * Get the amount of all troops the player has.
     * @return Map<ITroop, Integer> - Map with troops and amounts.
     * @since 0.0.1
     */
    public Map<ITroop, Integer> getTroops() {
        Map<ITroop, Integer> troopsAmount = new HashMap<>();
        for (TroopsBuilding troopBuilding : getTroopsCampBuildings()) {
            for (Map.Entry<ITroop, Integer> entry : troopBuilding.getTroopAmount().entrySet()) {
                troopsAmount.put(entry.getKey(), troopsAmount.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return troopsAmount;
    }


    /**
     * Get the player base location.
     * @return Location - the player base location.
     * @since 0.0.1
     */
    public Location getBaseStartLocation() {
        return baseLocation.clone();
    }

    /**
     * Get the player base location.
     * @return Location - the player base location.
     * @since 0.0.1
     */
    public Location getBaseEndLocation() {
        return getBaseStartLocation().add(ClashOfClubs.getBaseSize(), 300, ClashOfClubs.getBaseSize());
    }



    /**
     * Set the amount of gems for the player.
     * @param gems int - amount of gems.
     * @since 0.0.1
     */
    public void setGems(int gems) {
        this.gems = gems;
    }

    /**
     * Get the amount of gems for the player.
     * @return int - all gems.
     * @since 0.0.1
     */
    public int getGems() {
        return gems;
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
        return this.elo += elo;
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
     * The uuid of the player.
     * @return UUID - owners uuid.
     * @since 0.0.1
     */
    public UUID getUniqueId() {
        return uuid;
    }

    public void setBuildingMode(IBuildingMode buildingMode) {
        this.buildingMode = buildingMode;

        if (buildingMode == null) {
            getDefenseBuildings().forEach(d -> CircleParticleUtil.hideRadius(d.getBuildingUUID()));
        } else {
            getDefenseBuildings().stream().filter(d -> !d.getBuildingUUID().equals(buildingMode.getBuildingUUID()))
                    .forEach(d -> CircleParticleUtil.displayRadius(d.getBuildingUUID(), d.getCenterCoordinate(), d.getRadius()));
        }
    }

    /**
     * Get the building mode interface of either {@link ConstructionMode} or {@link MovingMode}.
     * @return {@link IBuilding} - the building mode.
     * @since 0.0.2
     */
    public IBuildingMode getBuildingMode() {
        return buildingMode;
    }

    public long getLastJoinMillis() {
        return lastJoin;
    }

    public String getLastServer() {
        return lastServer;
    }

    /**
     * Returns a list of all buildings the player can still build with their current town hall.
     * @return List<IBuilding> - all buildings the player can build.
     * @since 1.0.0
     */
    public List<IBuilding> buildableBuildings() {
        List<IBuilding> buildings = new ArrayList<>();
        int townHallLevel = getTownHallLevel();

        for (IResourceContainerBuilding building : ResourceContainerBuildings.values()) {
            if (BuildingsAmountUtil.getAmountOfBuilding(building, townHallLevel) - getResourceContainerBuildings(building.getContainingResourceType()).size() > 0)
                buildings.add(building);
        }
        for (IResourceContainerBuilding building : ResourceGathererBuildings.values()) {
            if (BuildingsAmountUtil.getAmountOfBuilding(building, townHallLevel) - getResourceGatherBuildings(building.getContainingResourceType()).size() > 0)
                buildings.add(building);
        }
        for (ITroopBuilding building : TroopBuildings.values()) {
            if (BuildingsAmountUtil.getAmountOfBuilding(building, townHallLevel) - getTroopsCampBuildings().size() > 0)
                buildings.add(building);
        }
        for (ITroopBuilding building : TroopCreationBuildings.values()) {
            if (BuildingsAmountUtil.getAmountOfBuilding(building, townHallLevel) - getTroopsCreateBuildings().size() > 0)
                buildings.add(building);
        }
        return buildings;
    }

    /**
     * Check if the player has troops in his inventory.
     * @return boolean - player has troops.
     */
    public boolean hasTroops() {
        return getTroops().values().stream().anyMatch(t -> t > 0);
    }
}
