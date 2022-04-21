package net.fununity.clashofclans.tickhandler;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.data.ResourceGatherLevelData;
import net.fununity.clashofclans.buildings.list.ResourceGathererBuildings;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceTickHandler {

    private ResourceTickHandler() {
        throw new UnsupportedOperationException("ResourceTickHandler is a handler class");
    }

    private static List<ResourceGatherBuilding> resourceGatherBuildingList;

    /**
     * Starts the timer.
     * @since 0.0.1
     */
    public static void startTimer() {
        resourceGatherBuildingList = new ArrayList<>();
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            try (ResultSet set = DatabaseBuildings.getInstance().getSpecifiedBuilding(ResourceGathererBuildings.values())) {
                while (set != null && set.next()) {
                    IBuilding buildingID = BuildingsManager.getInstance().getBuildingById(set.getString("buildingID"));

                    resourceGatherBuildingList.add(new ResourceGatherBuilding(UUID.fromString(set.getString("uuid")), buildingID,
                            new Location(ClashOfClubs.getInstance().getWorld(), set.getInt("x"), ClashOfClubs.getBaseYCoordinate(), set.getInt("z")),
                            set.getByte("rotation"), set.getInt("level"), set.getDouble("amount")));
                }
            } catch (SQLException exception) {
                ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
            }

            Bukkit.getScheduler().runTaskTimerAsynchronously(ClashOfClubs.getInstance(), () -> {
                for (ResourceGatherBuilding resourceGatherBuilding : getResourceGatherBuildingList()) {
                    ResourceGatherLevelData levelData = resourceGatherBuilding.getBuilding().getBuildingLevelData()[resourceGatherBuilding.getLevel() - 1];
                    if (resourceGatherBuilding.getAmount() < levelData.getMaximumResource())
                        resourceGatherBuilding.setAmount(resourceGatherBuilding.getAmount() + levelData.getResourceGatheringPerHour() / 720.0); // each 5s
                }
            }, 100, 100);
            Bukkit.getScheduler().runTaskTimerAsynchronously(ClashOfClubs.getInstance(), ResourceTickHandler::syncResources, 20 * 60 * 10, 20 * 60 * 10);
        });
    }

    /**
     * Syncs all resource buildings.
     * @since 0.0.1
     */
    public static void syncResources() {
        for (ResourceGatherBuilding resourceGatherBuilding : getResourceGatherBuildingList()) {
            DatabaseBuildings.getInstance().updateData(resourceGatherBuilding.getCoordinate(), (int) resourceGatherBuilding.getAmount());
        }
    }

    /**
     * Get the cached and the current player resource gather buildings.
     * @return Set<ResourceGatherBuilding> - all current resource buildings.
     * @since 0.0.1
     */
    public static Set<ResourceGatherBuilding> getResourceGatherBuildingList() {
        Set<ResourceGatherBuilding> resourceGatherBuildings = new HashSet<>(resourceGatherBuildingList);
        for (Map.Entry<UUID, CoCPlayer> entry : PlayerManager.getInstance().getPlayers().entrySet()) {
            resourceGatherBuildings.addAll(entry.getValue().getBuildings().stream().filter(b -> b instanceof ResourceGatherBuilding)
                    .map(list->(ResourceGatherBuilding) list).collect(Collectors.toList()));
        }
        return resourceGatherBuildings;
    }

    /**
     * Adds the list to the cache.
     * @param list List<ResourceGatherBuilding> - The list to add.
     * @since 0.0.1
     */
    public static void addToCache(List<ResourceGatherBuilding> list) {
        resourceGatherBuildingList.addAll(list);
    }

    /**
     * Removes all player buildings from the cache and sets the cached amount to the building player.
     * @param player {@link CoCPlayer} - the player
     * @since 0.0.1
     */
    public static void removeFromCache(CoCPlayer player) {
        for (GeneralBuilding building : player.getBuildings()) {
            if (building instanceof ResourceGatherBuilding) {
                ResourceGatherBuilding cachedBuilding = resourceGatherBuildingList.stream().filter(b -> b.getCoordinate().equals(building.getCoordinate())).findFirst().orElse(null);
                if (cachedBuilding != null) {
                    ((ResourceGatherBuilding) building).setAmount(cachedBuilding.getAmount());
                    resourceGatherBuildingList.remove(cachedBuilding);
                }
            }
        }
    }

}
