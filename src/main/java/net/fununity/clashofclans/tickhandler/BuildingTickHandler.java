package net.fununity.clashofclans.tickhandler;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.buildings.instances.ConstructionBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
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
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            try (ResultSet set = DatabaseBuildings.getInstance().getConstructionBuildings()) {
                while (set != null && set.next()) {
                    Location location = new Location(ClashOfClubs.getInstance().getWorld(), set.getInt("x"), 100, set.getInt("z"));
                    constructionBuildingList.add(new ConstructionBuilding(new GeneralBuilding(UUID.fromString(set.getString("uuid")),
                            BuildingsManager.getInstance().getBuildingById(set.getString("buildingID")), location, set.getByte("rotation"), set.getInt("level")),
                            (int) ChronoUnit.SECONDS.between(OffsetDateTime.now(), OffsetDateTime.parse(set.getString("date")))));
                }
            } catch (SQLException exception) {
                ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
            }

            Bukkit.getScheduler().runTaskTimerAsynchronously(ClashOfClubs.getInstance(), () -> {
                for (ConstructionBuilding constructionBuilding : getConstructionBuildings()) {
                    constructionBuilding.setBuildingDuration(constructionBuilding.getBuildingDuration() - 1);
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
        Set<ConstructionBuilding> constructionBuildings = new HashSet<>(constructionBuildingList);
        for (Map.Entry<UUID, CoCPlayer> entry : PlayerManager.getInstance().getPlayers().entrySet())
            constructionBuildings.addAll(entry.getValue().getBuildings().stream().filter(b -> b instanceof ConstructionBuilding).map(list->(ConstructionBuilding) list).collect(Collectors.toList()));
        return constructionBuildings;
    }

    /**
     * Removes the building from the cached list.
     * @param building {@link ConstructionBuilding} - building
     * @since 1.0
     */
    public static void removeBuilding(ConstructionBuilding building) {
        constructionBuildingList.remove(building);
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
        player.getBuildings().stream().filter(b -> b instanceof ConstructionBuilding).forEach(building -> {
            ConstructionBuilding cachedBuilding = constructionBuildingList.stream().filter(b -> b.getCoordinate().equals(building.getCoordinate())).findFirst().orElse(null);
            if (cachedBuilding != null) {
                ((ConstructionBuilding) building).setBuildingDuration(cachedBuilding.getBuildingDuration());
                constructionBuildingList.remove(cachedBuilding);
            }
        });
    }
}
