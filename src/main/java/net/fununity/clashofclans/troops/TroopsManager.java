package net.fununity.clashofclans.troops;

import net.fununity.clashofclans.buildings.DatabaseBuildings;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.TroopsBuilding;
import net.fununity.clashofclans.buildings.classes.TroopsCreateBuilding;
import net.fununity.clashofclans.buildings.list.TroopBuildings;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manager class for the troops.
 * @author Niko
 * @since 0.0.1
 */
public class TroopsManager {

    private static TroopsManager instance;

    /**
     * Get the singleton instance of this class.
     * @return {@link TroopsManager} - singleton instance of this class.
     * @since 0.0.1
     */
    public static TroopsManager getInstance() {
        if(instance == null)
            instance = new TroopsManager();
        return instance;
    }

    private TroopsManager() {
        // not needed atm
    }

    /**
     * Will be called when a troop finished in {@link TroopsCreateBuilding}.
     * Will try to move the troop to an {@link TroopsBuilding}.
     * @param building {@link TroopsCreateBuilding} - the building the troop finished.
     * @since 0.0.1
     */
    public void troopEducated(TroopsCreateBuilding building) {
        Troop troop = building.getTroopsQueue().poll();
        if (troop == null) return;

        List<TroopsBuilding> buildings = PlayerManager.getInstance().getTroopBuildings(building.getUuid());
        buildings.removeIf(list -> list instanceof TroopsCreateBuilding);
        TroopsBuilding troopsBuilding = getBuildingWhichFitTroop(buildings, troop.getTroop());
        if (troopsBuilding == null)
            troopsBuilding = building;

        troopsBuilding.getTroopAmount().put(troop.getTroop(), building.getTroopAmount().getOrDefault(troop.getTroop(), 0) + 1);
        DatabaseBuildings.getInstance().updateTroopsData(troopsBuilding.getCoordinate(), troop.getTroop(), building.getTroopAmount().get(troop.getTroop()));
    }

    /**
     * Tries to clear all {@link net.fununity.clashofclans.buildings.list.TroopCreationBuildings} and move them to {@link TroopBuildings}.
     * @param cocPlayer CoCPlayer - the player to move the buildings.
     * @since 0.0.1
     */
    public void moveTroopsToField(CoCPlayer cocPlayer) {
        List<TroopsBuilding> troopsBuildings = cocPlayer.getBuildings().stream().filter(b -> b instanceof TroopsBuilding && !(b instanceof TroopsCreateBuilding)).map(list -> (TroopsBuilding) list).collect(Collectors.toList());
        if(troopsBuildings.isEmpty()) return;
        Set<TroopsBuilding> changedBuildings = new HashSet<>();

        for (GeneralBuilding building : cocPlayer.getBuildings()) {
            if (building instanceof TroopsCreateBuilding && ((TroopsCreateBuilding) building).getCurrentSizeOfTroops() > 0) {
                for (Map.Entry<ITroop, Integer> entry : ((TroopsCreateBuilding) building).getTroopAmount().entrySet()) {
                    int removed = 0;
                    for (int i = 0; i < entry.getValue(); i++) {
                        TroopsBuilding fitAbleTroop = getBuildingWhichFitTroop(troopsBuildings, entry.getKey());
                        if (fitAbleTroop == null)
                            break;
                        changedBuildings.add((TroopsBuilding) building);
                        changedBuildings.add(fitAbleTroop);
                        fitAbleTroop.getTroopAmount().put(entry.getKey(), fitAbleTroop.getTroopAmount().getOrDefault(entry.getKey(), 0) + 1);
                        removed++;
                    }
                    if(removed != 0)
                        ((TroopsCreateBuilding) building).getTroopAmount().put(entry.getKey(), ((TroopsCreateBuilding) building).getTroopAmount().get(entry.getKey()) - removed);
                }
            }
        }
        for (TroopsBuilding changedBuilding : changedBuildings) {
            DatabaseBuildings.getInstance().updateTroopsData(changedBuilding);
        }
    }

    /**
     * Get the first building, which can fit the given troop.
     * @param buildingsList List<TroopsBuilding> - A list with all buildings to check.
     * @param troop {@link ITroop} - the troop to move.
     * @return {@link TroopsBuilding} - the building the troop can be moved.
     * @since 0.0.1
     */
    private TroopsBuilding getBuildingWhichFitTroop(List<TroopsBuilding> buildingsList, ITroop troop) {
        for (TroopsBuilding playerBuilding : buildingsList) {
            if (playerBuilding.getCurrentSizeOfTroops() + troop.getSize() <= playerBuilding.getMaxAmountOfTroops())
                return playerBuilding;
        }
        return null;
    }
}
