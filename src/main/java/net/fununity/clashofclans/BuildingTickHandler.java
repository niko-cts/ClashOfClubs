package net.fununity.clashofclans;

import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BuildingTickHandler {

    private BuildingTickHandler() {
        throw new UnsupportedOperationException("BuildingTickHandler is a handler class");
    }

    public static void startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ClashOfClans.getInstance(), () -> {
            for (Map.Entry<GeneralBuilding, Integer> entry : new ConcurrentHashMap<>(BuildingsManager.getInstance().getBuildingTime()).entrySet()) {
                if (entry.getValue() > 0)
                    BuildingsManager.getInstance().getBuildingTime().put(entry.getKey(), entry.getValue()-1);
                else {
                    BuildingsManager.getInstance().getBuildingTime().remove(entry.getKey());
                    BuildingsManager.getInstance().finishedBuilding(entry.getKey());
                }
            }
        }, 20L, 20L);
    }
}
