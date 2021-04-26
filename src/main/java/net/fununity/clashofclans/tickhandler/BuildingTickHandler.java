package net.fununity.clashofclans.tickhandler;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.DatabaseBuildings;
import net.fununity.clashofclans.buildings.classes.ConstructionBuilding;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class BuildingTickHandler {

    private BuildingTickHandler() {
        throw new UnsupportedOperationException("BuildingTickHandler is a handler class");
    }

    private static List<ConstructionBuilding> constructionBuildingList;

    public static void startTimer() {
        constructionBuildingList = new ArrayList<>();
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> {
            try (ResultSet set = DatabaseBuildings.getInstance().getConstructionBuildings()) {
                while (set != null && set.next()) {
                    Location location = new Location(ClashOfClans.getInstance().getPlayWorld(), set.getInt("x"), 100, set.getInt("z"));
                    try (ResultSet building = DatabaseBuildings.getInstance().getBuilding(location)) {
                        if  (building != null && building.next()) {
                            GeneralBuilding generalBuilding = new GeneralBuilding(UUID.fromString(set.getString("uuid")), BuildingsManager.getInstance().getBuildingById(building.getString("buildingID")), location, building.getByte("rotation"), building.getInt("level"));
                            constructionBuildingList.add(new ConstructionBuilding(generalBuilding, (int) ChronoUnit.SECONDS.between(OffsetDateTime.now(), OffsetDateTime.parse(set.getString("date")))));
                        }
                    } catch (SQLException exception) {
                        ClashOfClans.getInstance().getLogger().warning(exception.getMessage());
                    }
                }
            } catch (SQLException exception) {
                ClashOfClans.getInstance().getLogger().warning(exception.getMessage());
            }

            Bukkit.getScheduler().runTaskTimerAsynchronously(ClashOfClans.getInstance(), () -> {
                for (ConstructionBuilding constructionBuilding : getConstructionBuildings()) {
                    if (constructionBuilding.getBuildingDuration() > 0)
                        constructionBuilding.setBuildingDuration(constructionBuilding.getBuildingDuration() - 1);
                    else
                        BuildingsManager.getInstance().finishedBuilding(constructionBuilding);
                }
            }, 20L, 20L);
        });
    }

    /**
     * Get the cached and the current player resource gather buildings.
     * @return Set<ResourceGatherBuilding> - all current resource buildings.
     * @since 0.0.1
     */
    public static Set<ConstructionBuilding> getConstructionBuildings() {
        Set<ConstructionBuilding> resourceGatherBuildings = new HashSet<>(constructionBuildingList);
        for (Map.Entry<UUID, CoCPlayer> entry : PlayerManager.getInstance().getPlayers().entrySet())
            resourceGatherBuildings.addAll(entry.getValue().getBuildings().stream().filter(b -> b instanceof ConstructionBuilding).map(list->(ConstructionBuilding) list).collect(Collectors.toList()));
        return resourceGatherBuildings;
    }

    /**
     * Adds the list to the cache.
     * @param list List<ResourceGatherBuilding> - The list to add.
     * @since 0.0.1
     */
    public static void addToCache(List<ConstructionBuilding> list) {
        constructionBuildingList.addAll(list);
    }

    /**
     * Removes all player buildings from the cache and sets the cached amount to the building player.
     * @param player {@link CoCPlayer} - the player
     * @since 0.0.1
     */
    public static void removeFromCache(CoCPlayer player) {
        for (GeneralBuilding building : player.getBuildings()) {
            if (building instanceof ConstructionBuilding) {
                ConstructionBuilding cachedBuilding = constructionBuildingList.stream().filter(b -> b.getCoordinate().equals(building.getCoordinate())).findFirst().orElse(null);
                if (cachedBuilding != null) {
                    ((ConstructionBuilding) building).setBuildingDuration(cachedBuilding.getMaxBuildingDuration());
                    constructionBuildingList.remove(cachedBuilding);
                }
            }
        }
    }
}
