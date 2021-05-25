package net.fununity.clashofclans.util;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.list.*;

/**
 * Utility class to get the amount of each building per town hall level.
 * @author Niko
 * @since 0.0.1
 */
public class BuildingsAmountUtil {

    private BuildingsAmountUtil() {
        throw new UnsupportedOperationException("BuildingsAmountUtil is a utility class.");
    }

    /**
     * Get the amount of building per town hall level.
     * @param building {@link IBuilding} - the building.
     * @param townHallLevel int - the town hall level.
     * @return int - the amount of buildings.
     * @since 0.0.1
     */
    public static int getAmountOfBuilding(IBuilding building, int townHallLevel) {
        if (building == Buildings.TOWN_HALL) {
            return 1;
        } else if (building == Buildings.BUILDER) {
            return 4;
        } else if(building == ResourceContainerBuildings.GOLD_STOCK || building == ResourceGathererBuildings.GOLD_MINER ||
                    building == ResourceContainerBuildings.BARN_STOCK || building == ResourceGathererBuildings.FARM) {
            switch (townHallLevel) {
                case 1:
                case 2:
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
        } else if(building == TroopBuildings.ARMY_CAMP) {
            switch (townHallLevel) {
                case 1:
                case 2:
                    return 1;
                case 3:
                case 4:
                    return 2;
                default:
                    return 4;
            }
        } else if(building == DefenseBuildings.ARCHER_TOWER || building == DefenseBuildings.CANNON) {
            switch (townHallLevel) {
                case 1:
                    return 2;
                case 2:
                    return 3;
                default:
                    return 4;
            }
        }
        return 0;
    }

}
