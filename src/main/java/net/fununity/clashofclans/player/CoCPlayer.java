package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.DatabaseBuildings;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceContainerBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.list.Buildings;
import net.fununity.main.api.FunUnityAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

public class CoCPlayer {

    private final UUID uuid;
    private final Location location;
    private final Map<ResourceTypes, Integer> resourceMap;
    private final List<GeneralBuilding> buildings;
    private int xp;

    public CoCPlayer(UUID uuid, Location location, Map<ResourceTypes, Integer> resourceMap, int xp) {
        this.uuid = uuid;
        this.location = location;
        this.resourceMap = resourceMap;
        this.buildings = new ArrayList<>();
        this.xp = xp;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getTownHallLevel() {
        GeneralBuilding townHall = getBuildings().stream().filter(b -> b.getBuilding() == Buildings.TOWN_HALL).findFirst().orElse(null);
        return townHall != null ? townHall.getLevel() : 0;
    }

    public int getResource(ResourceTypes resourceTypes) {
        return resourceMap.get(resourceTypes);
    }

    public void removeResource(ResourceTypes type, int remove) {
        if(remove == 0) return;
        int newResource = getResource(type) - remove;
        resourceMap.put(type, newResource);
        ScoreboardMenu.show(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(uuid), this);

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

    public void addResource(ResourceTypes type, int add) {
        if (add == 0) return;
        int newResource = getResource(type) + add;
        resourceMap.put(type, newResource);
        ScoreboardMenu.show(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(uuid), this);

        List<ResourceContainerBuilding> containerBuildings = getContainerBuildings(type);

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

    public void updateResources() {
        for (ResourceContainerBuilding containerBuilding : getContainerBuildings()) {
            resourceMap.put(containerBuilding.getResourceContaining(), (int) (resourceMap.getOrDefault(containerBuilding.getResourceContaining(), 0) + containerBuilding.getAmount()));
        }
    }

    private List<ResourceContainerBuilding> getContainerBuildings(ResourceTypes type) {
        return getBuildings().stream().filter(b -> b instanceof ResourceContainerBuilding && !(b instanceof ResourceGatherBuilding)).map(list -> (ResourceContainerBuilding) list).filter(b -> b.getResourceContaining() == type).sorted(Comparator.comparingInt(ResourceContainerBuilding::getMaximumResource)).collect(Collectors.toList());
    }
    private List<ResourceContainerBuilding> getContainerBuildings() {
        return getBuildings().stream().filter(b -> b instanceof ResourceContainerBuilding && !(b instanceof ResourceGatherBuilding)).map(list -> (ResourceContainerBuilding) list).sorted(Comparator.comparingInt(ResourceContainerBuilding::getMaximumResource)).collect(Collectors.toList());
    }
    public List<GeneralBuilding> getBuildings() {
        return buildings;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getXp() {
        return xp;
    }

    public int getMaxResourceToHave(ResourceTypes resourceType) {
        int max = 0;
        for (GeneralBuilding building : getBuildings()) {
            if (building instanceof ResourceContainerBuilding && !(building instanceof ResourceGatherBuilding) && ((ResourceContainerBuilding) building).getResourceContaining() == resourceType)
               max += ((ResourceContainerBuilding) building).getMaximumResource();
        }
        return max;
    }
}
