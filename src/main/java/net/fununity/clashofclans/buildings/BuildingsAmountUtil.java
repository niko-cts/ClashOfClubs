package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.list.Buildings;
import net.fununity.clashofclans.buildings.list.ResourceContainerBuildings;
import net.fununity.clashofclans.buildings.list.ResourceGathererBuildings;

public class BuildingsAmountUtil {

    private BuildingsAmountUtil() {
        throw new UnsupportedOperationException("BuildingsAmountUtil is a utility class.");
    }

    public static int getAmountOfBuilding(IBuilding building, int townHallLevel) {
        if (building == Buildings.TOWN_HALL) {
            return 1;
        } else if (building == ResourceContainerBuildings.GOLD_STOCK || building == ResourceGathererBuildings.GOLD_MINER) {
            switch (townHallLevel) {
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                case 4:
                    return 4;
                default:
                    return 5;
            }
        }
        return 0;
    }

}
