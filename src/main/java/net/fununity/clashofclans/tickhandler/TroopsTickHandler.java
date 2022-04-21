package net.fununity.clashofclans.tickhandler;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.TroopsCreateBuilding;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.buildings.TroopsBuildingManager;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class TroopsTickHandler {

    private TroopsTickHandler() {
        throw new UnsupportedOperationException("ResourceTickHandler is a handler class");
    }

    private static List<TroopsCreateBuilding> troopsCreateBuildingList;

    /**
     * Starts the timer.
     * @since 0.0.1
     */
    public static void startTimer() {
        troopsCreateBuildingList = new ArrayList<>();
        Bukkit.getScheduler().runTaskTimer(ClashOfClubs.getInstance(), () -> {
            for (TroopsCreateBuilding troopsCreateBuilding : getTroopsCreationBuildings()) {
                if (troopsCreateBuilding.getTroopsQueue().isEmpty()) {
                    troopsCreateBuildingList.remove(troopsCreateBuilding);
                    continue;
                }

                troopsCreateBuilding.setTrainSecondsLeft(troopsCreateBuilding.getTrainSecondsLeft() - 1);
                if (troopsCreateBuilding.getTrainSecondsLeft() <= 0)
                    Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () ->
                            TroopsBuildingManager.getInstance().troopEducated(troopsCreateBuilding));
            }
        }, 20, 20);
    }

    /**
     * Get the cached and the current player resource gather buildings.
     * @return Set<ResourceGatherBuilding> - all current resource buildings.
     * @since 0.0.1
     */
    public static Set<TroopsCreateBuilding> getTroopsCreationBuildings() {
        Set<TroopsCreateBuilding> troopsCreateBuilding = new HashSet<>(troopsCreateBuildingList);
        for (Map.Entry<UUID, CoCPlayer> entry : PlayerManager.getInstance().getPlayers().entrySet()) {
            troopsCreateBuilding.addAll(entry.getValue().getBuildings().stream().filter(b -> b instanceof TroopsCreateBuilding)
                    .map(list->(TroopsCreateBuilding) list).filter(t->!t.getTroopsQueue().isEmpty()).collect(Collectors.toList()));
        }
        return troopsCreateBuilding;
    }

    /**
     * Adds the list to the cache.
     * @param list List<TroopsCreateBuilding> - The list to add.
     * @since 0.0.1
     */
    public static void addToCache(List<TroopsCreateBuilding> list) {
        troopsCreateBuildingList.addAll(list);
    }

    /**
     * Removes all player buildings from the cache and sets the cached amount to the building player.
     * @param player {@link CoCPlayer} - the player
     * @since 0.0.1
     */
    public static void removeFromCache(CoCPlayer player) {
        for (GeneralBuilding building : player.getBuildings()) {
            if (building instanceof TroopsCreateBuilding) {
                TroopsCreateBuilding cachedBuilding = troopsCreateBuildingList.stream().filter(b -> b.getCoordinate().equals(building.getCoordinate())).findFirst().orElse(null);
                if (cachedBuilding != null) {
                    ((TroopsCreateBuilding) building).setTroopsQueue(cachedBuilding.getTroopsQueue());
                    ((TroopsCreateBuilding) building).setTrainSecondsLeft(cachedBuilding.getTrainSecondsLeft());
                    troopsCreateBuildingList.remove(cachedBuilding);
                }
            }
        }
    }

}
