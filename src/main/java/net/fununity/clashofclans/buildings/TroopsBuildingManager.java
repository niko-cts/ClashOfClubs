package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.instances.troops.TroopsBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsCreateBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;

import java.util.*;

/**
 * Manager class for the troops.
 * @author Niko
 * @since 0.0.1
 */
public class TroopsBuildingManager {

    private static TroopsBuildingManager instance;

    /**
     * Get the singleton instance of this class.
     * @return {@link TroopsBuildingManager} - singleton instance of this class.
     * @since 0.0.1
     */
    public static TroopsBuildingManager getInstance() {
        if(instance == null)
            instance = new TroopsBuildingManager();
        return instance;
    }

    private TroopsBuildingManager() {
        // not needed atm
    }

    /**
     * Troop was educated and removed from the queue.
     * @param createBuilding TroopsCreateBuilding - the building which finished education
     */
    public void troopEducated(TroopsCreateBuilding createBuilding) {
        CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(createBuilding.getOwnerUUID());
        List<TroopsBuilding> buildings = coCPlayer.getTroopsCampBuildings();
        buildings.sort(Comparator.comparingInt(TroopsBuilding::getCurrentSizeOfTroops));

        ITroop troop = createBuilding.getTroopsQueue().poll();

        if (troop == null) return;

        TroopsBuilding troopsBuilding = getBuildingWhichFitTroop(buildings, troop);
        if (troopsBuilding != null) {
            troopsBuilding.addTroopAmount(troop, 1);
        }

        if (createBuilding.getCurrentSizeOfTroops() + troop.getSize() <= createBuilding.getMaxAmountOfTroops()) {
            createBuilding.addTroopAmount(troop, 1);
        }
    }

    /**
     * Tries to move all educated troops from the creation buildings to the camps.
     * @param coCPlayer CoCPlayer - player to move.
     * @since 0.0.2
     */
    public void moveTroopsFromCreationToCamp(CoCPlayer coCPlayer) {
        List<TroopsBuilding> troopsCampBuildings = coCPlayer.getTroopsCampBuildings();
        for (TroopsCreateBuilding troopsCreateBuilding : coCPlayer.getTroopsCreateBuildings()) {
            for (Map.Entry<ITroop, Integer> entry : troopsCreateBuilding.getTroopAmount().entrySet()) {
                TroopsBuilding troopsBuilding = getBuildingWhichFitTroop(troopsCampBuildings, entry.getKey());
                if (troopsBuilding == null)
                    break;
                for (int i = 0; i < entry.getValue(); i++) {
                    troopsBuilding.addTroopAmount(entry.getKey(), 1);
                    troopsCreateBuilding.removeTroopAmount(entry.getKey(), 1);
                    troopsBuilding = getBuildingWhichFitTroop(troopsCampBuildings, entry.getKey());
                    if (troopsBuilding == null)
                        break;
                }
            }
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

    /**
     * Starts the education from a troop.
     * @param building {@link TroopsCreateBuilding} - the building to train.
     * @param troop {@link ITroop} - the troop to educated.
     * @since 0.0.1
     */
    public void startEducation(TroopsCreateBuilding building, ITroop troop) {
        CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(building.getOwnerUUID());
        if (player.getResourceAmount(ResourceTypes.FOOD) < troop.getCostAmount()) {
            APIPlayer apiPlayer = player.getOwner();
            apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE, "${type}", ResourceTypes.FOOD.getColoredName(apiPlayer.getLanguage()));
            return;
        }

        player.removeResource(ResourceTypes.FOOD, troop.getCostAmount());
        building.getTroopsQueue().add(troop);
    }
}
