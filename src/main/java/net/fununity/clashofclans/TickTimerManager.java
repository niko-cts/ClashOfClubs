package net.fununity.clashofclans;

import net.fununity.clashofclans.buildings.ConstructionManager;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.buildings.instances.ConstructionBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsCreateBuilding;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.values.ResourceTypes;
import org.bukkit.Bukkit;

import java.util.*;

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
        Bukkit.getScheduler().runTaskTimer(ClashOfClubs.getInstance(), this::start, 20L, 20L);
    }

    /**
     * Will go through every online player and checks resources, constructions, troops.
     * Collects every building which needs a rebuild and calls {@link Schematics#createBuildings(List)} async.
     * @since 0.0.2
     */
    private void start() {
        List<GeneralBuilding> rebuildBuildings = new ArrayList<>();
        Map<UUID, List<ConstructionBuilding>> finishedConstructions = new HashMap<>();

        for (Map.Entry<UUID, CoCPlayer> entry : ClashOfClubs.getInstance().getPlayerManager().getPlayers().entrySet()) {
            CoCPlayer player = entry.getValue();
            for (ResourceTypes type : ResourceTypes.values()) {
                rebuildBuildings.addAll(player.getResourceGatherBuildings(type).stream().filter(ResourceGatherBuilding::addAmountPerSecond).toList());
            }

            player.getTroopsCreateBuildings().forEach(TroopsCreateBuilding::checkQueue);
            List<ConstructionBuilding> buildings = player.getConstructionBuildings().stream().filter(ConstructionBuilding::updateBuildingDuration).toList();
            if (!buildings.isEmpty())
                finishedConstructions.put(entry.getKey(), buildings);
        }

        if (!rebuildBuildings.isEmpty())
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> Schematics.createBuildings(rebuildBuildings));

        if (!finishedConstructions.isEmpty())
            ConstructionManager.getInstance().finishedConstruction(finishedConstructions);
    }
}
