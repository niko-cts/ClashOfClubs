package net.fununity.clashofclans;

import net.fununity.clashofclans.buildings.instances.ConstructionBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsCreateBuilding;
import net.fununity.clashofclans.player.CoCPlayer;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;

/**
 * This class is used for task timing.
 * @author Niko
 * @since 0.0.2
 */
public class TickTimerManager {

    /**
     * Instantiates the class and starts async. timer.
     * @since 0.0.2
     */
    public TickTimerManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ClashOfClubs.getInstance(), this::start, 20L, 20L);
    }

    /**
     * Will go through every online player and checks resources, constructions, troops.
     * @since 0.0.2
     */
    private void start() {
        for (Map.Entry<UUID, CoCPlayer> entry : ClashOfClubs.getInstance().getPlayerManager().getPlayers().entrySet()) {
            CoCPlayer player = entry.getValue();
            for (ResourceTypes type : ResourceTypes.values()) {
                player.getResourceGatherBuildings(type).forEach(ResourceGatherBuilding::addAmountPerSecond);
            }
            player.getTroopsCreateBuildings().forEach(TroopsCreateBuilding::checkQueue);
            player.getConstructionBuildings().forEach(ConstructionBuilding::updateBuildingDuration);
        }
    }
}
