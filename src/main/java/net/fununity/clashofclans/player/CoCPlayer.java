package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.Schematics;
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
import net.fununity.clashofclans.values.ICoCValue;
import net.fununity.clashofclans.values.PlayerValues;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    private final Map<PlayerValues, Integer> playerValues;
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
     * @param playerValues EnumMap<PlayerValues, Integer> - the player values
     * @param lastJoin long - the last join milli secs.
     * @param lastServer String - the last server.
     * @param allBuildings List<GeneralBuilding> - all buildings the player has.
     * @since 0.0.1
     */
    public CoCPlayer(UUID uuid, Location baseLocation, EnumMap<PlayerValues, Integer> playerValues, String lastServer, long lastJoin, List<GeneralBuilding> allBuildings) {
        this.uuid = uuid;
        this.baseLocation = baseLocation;
        this.playerValues = playerValues;
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
        Player player = apiPlayer.getPlayer();
        if (teleport) {
            player.teleport(getVisitorLocation());
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.getInventory().clear();
        player.setLevel(0);
        player.setExp(0);
        player.giveExp(getExp());

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
        if (this.visitors.contains(apiPlayer.getUniqueId())) {
            this.visitors.remove(apiPlayer.getUniqueId());
            getAllBuildings().stream().filter(b -> b instanceof GeneralHologramBuilding).forEach(b -> ((GeneralHologramBuilding) b).hideHologram(apiPlayer));
        }
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
     * Gets the value amount the player currently has.
     * @param value {@link ICoCValue} - value of coc data.
     * @return double - amount of resource
     * @since 0.0.1
     */
    public double getResourceAmount(ICoCValue value) {
        if (value instanceof PlayerValues)
           return this.playerValues.get((PlayerValues) value);

        int amount = 0;
        for (ResourceContainerBuilding building : getResourceContainerBuildings((ResourceTypes) value)) {
            amount += building.getAmount();
        }
        return amount;
    }



    /**
     * Adds resource to the players inventory and updates buildings and the scoreboard.
     * @param value {@link ICoCValue} - the value to update.
     * @param amount double - the amount to update.
     * @return List<GeneralBuilding> - buildings that need an update.
     * @since 0.0.2
     */
    public List<GeneralBuilding> addResourceWithoutUpdate(ICoCValue value, double amount) {
        if (value instanceof PlayerValues)
            addPlayerValue((PlayerValues) value, (int) amount);
        else
            return fillResourceToContainer((ResourceTypes) value, amount);
        return new ArrayList<>();
    }

    /**
     * Adds resource to the players inventory and updates buildings and the scoreboard.
     * @param value {@link ICoCValue} - the value to update.
     * @param amount double - the amount to update.
     * @since 0.0.2
     */
    public void addResourceWithUpdate(ICoCValue value, double amount) {
        List<GeneralBuilding> needsUpdate = addResourceWithoutUpdate(value, amount);
        if (!needsUpdate.isEmpty())
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> Schematics.createBuildings(needsUpdate));

        ScoreboardMenu.show(this);
    }


    /**
     * Remove resource from the player.
     * @param type ICoCValue - the type to remove.
     * @param remove int - the amount to remove.
     * @since 0.0.1
     */
    public void removeResourceWithUpdate(ICoCValue type, int remove) {
        if (type instanceof PlayerValues) {
            this.playerValues.put((PlayerValues) type, Math.max(0, this.playerValues.get((PlayerValues) type) - remove));
        } else {
            List<GeneralBuilding> needsUpdate = removeResourceWithoutUpdate((ResourceTypes) type, remove);
            if (!needsUpdate.isEmpty())
                Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> Schematics.createBuildings(needsUpdate));

            ScoreboardMenu.show(this);
        }
    }

    /**
     * Remove resource from the player without updating anything.
     * @param type ICoCValue - the type to remove.
     * @param remove int - the amount to remove.
     * @return List<GeneralBuilding> - buildings which need an update.
     * @since 1.0.2
     */
    public List<GeneralBuilding> removeResourceWithoutUpdate(ICoCValue type, int remove) {
        if (type instanceof PlayerValues) {
            this.playerValues.put((PlayerValues) type, Math.max(0, this.playerValues.get((PlayerValues) type) - remove));
            return new ArrayList<>();
        } else {
            return removeResourceFromContainer((ResourceTypes) type, remove);
        }
    }

    /**
     * Add resource to the player.
     * @param type ResourceTypes - the resource type.
     * @param add double - the amount of resource
     * @return List<GeneralBuilding> - buildings that need a rebuild
     * @since 0.0.1
     */
    public List<GeneralBuilding> fillResourceToContainer(ResourceTypes type, double add) {
        List<ResourceContainerBuilding> containerBuildings = getResourceContainerBuildings(type);
        if (containerBuildings.isEmpty()) return new ArrayList<>();

        containerBuildings.sort(Comparator.comparingInt(ResourceContainerBuilding::getMaximumResource));

        double needToAddPerBuilding = add / containerBuildings.size();

        List<GeneralBuilding> needsUpdate = new ArrayList<>();
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
                needsUpdate.add(resourceContainerBuilding);
        }

        return needsUpdate;
    }

    /**
     * Remove resource from the player.
     * @param type ResourceTypes - the type to remove.
     * @param remove int - the amount to remove.
     * @return List<GeneralBuilding> - buildings which need an update.
     * @since 0.0.1
     */
    public List<GeneralBuilding> removeResourceFromContainer(ResourceTypes type, int remove) {
        if (remove == 0) return new ArrayList<>();
        List<ResourceContainerBuilding> containerBuildings = getResourceContainerBuildings(type);
        if (containerBuildings.isEmpty()) return new ArrayList<>();

        containerBuildings.sort(Comparator.comparingInt(ResourceContainerBuilding::getMaximumResource));

        int needToRemovePerBuilding = remove / containerBuildings.size();

        List<GeneralBuilding> needsUpdate = new ArrayList<>();

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

            if (resourceContainerBuilding.setAmount(newAmount))
                needsUpdate.add(resourceContainerBuilding);
        }

        return needsUpdate;
    }

    /**
     * All container buildings from a type.
     * @param type ResourceType - type of container
     * @return List<ResourceContainerBuilding> - all resource container buildings.
     * @since 0.0.1
     */
    public List<ResourceContainerBuilding> getResourceContainerBuildings(ResourceTypes type) {
        return resourceBuildings.get(type).stream().filter(r -> !(r instanceof ResourceGatherBuilding)).toList();
    }

    /**
     * All container buildings from a type.
     * @param type ResourceType - type of container
     * @return List<ResourceContainerBuilding> - all resource container buildings.
     * @since 0.0.1
     */
    public List<ResourceGatherBuilding> getResourceGatherBuildings(ResourceTypes type) {
        return resourceBuildings.get(type).stream().filter(r -> r instanceof ResourceGatherBuilding).map(r -> (ResourceGatherBuilding) r).toList();
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

    /**
     * Removes a list of construction buildings from the player.
     * @param constructions List<ConstructionBuilding> - the list of constructions.
     * @since 0.0.1
     */
    public void removeConstructions(List<ConstructionBuilding> constructions) {
        constructionBuildings.removeAll(constructions);
    }

    /**
     * Adds a list of construction buildings to the player.
     * @param constructions List<ConstructionBuilding> - the list of constructions.
     * @since 0.0.1
     */
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
     * Removes the troop from a camp building.
     * @param troop ITroop - the troop.
     * @param amount int - the amount to remove.
     * @since 1.0.1
     */
    public void removeTroop(ITroop troop, int amount) {
        for (TroopsBuilding troopsCampBuilding : getTroopsCampBuildings()) {
            if (amount <= 0) {
                return;
            }
            int troopAmount = troopsCampBuilding.getTroopAmount(troop);
            if (troopAmount >= amount) {
                troopsCampBuilding.removeTroopAmount(troop, amount);
                return;
            }
            if (troopAmount > 0) {
                troopsCampBuilding.removeTroopAmount(troop, troopAmount);
                amount -= troopAmount;
            }
        }
    }


    /**
     * Get the player base location.
     * @return Location - the player base location.
     * @since 0.0.1
     */
    public Location getBaseStartLocation() {
        return baseLocation.clone();
    }

    public Location getBaseCenterLocation() {
        return getBaseStartLocation().add(ClashOfClubs.getBaseSize() / 2.0 + ClashOfClubs.getBaseBackground(), 0, ClashOfClubs.getBaseSize() / 2.0 + ClashOfClubs.getBaseBackground());
    }

    /**
     * Get the player base end location WITHOUT the background.
     * @return Location - the player base location.
     * @since 0.0.1
     */
    public Location getBaseEndLocation() {
        return getBaseStartLocation().add(ClashOfClubs.getBaseSize(), ClashOfClubs.getBaseYCoordinate() + 100, ClashOfClubs.getBaseSize());
    }

    public Location getVisitorLocation() {
        Location visitorLoc = getBaseCenterLocation();
        visitorLoc.setY(BuildingLocationUtil.getHighestYCoordinate(visitorLoc) + 1);
        return visitorLoc;
    }


    /**
     * Set the amount of gems for the player.
     * @param gems int - amount of gems.
     * @since 0.0.1
     */
    public void setGems(int gems) {
        this.playerValues.put(PlayerValues.GEMS, gems);
    }

    /**
     * Get the amount of gems for the player.
     * @return int - all gems.
     * @since 0.0.1
     */
    public int getGems() {
        return this.playerValues.get(PlayerValues.GEMS);
    }

    /**
     * Add xp to the player's base.
     * @param xp int - xp to add.
     * @return int - the summed xp.
     * @since 0.0.1
     */
    public int addExp(int xp) {
        return addPlayerValue(PlayerValues.XP, xp);
    }

    /**
     * Get the xp from the player.
     * @return int - the players xp
     * @since 0.0.1
     */
    public int getExp() {
        return this.playerValues.get(PlayerValues.XP);
    }

    /**
     * Add elo to the player.
     * @param elo int - elo to add.
     * @return int - the summed elo.
     * @since 0.0.1
     */
    public int addElo(int elo) {
        return addPlayerValue(PlayerValues.ELO, elo);
    }

    /**
     * Adds a specific amount to the player.
     * @param value PlayerValues - the player value.
     * @param amount int - amount to add.
     * @return int - new amount
     * @since 1.0.2
     */
    public int addPlayerValue(PlayerValues value, int amount) {
        int newAmount = this.playerValues.get(value) + amount;
        this.playerValues.put(value, newAmount);
        return newAmount;
    }

    /**
     * Get the elo from the player.
     * @return int - the players elo
     * @since 0.0.1
     */
    public int getElo() {
        return this.playerValues.get(PlayerValues.ELO);
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
