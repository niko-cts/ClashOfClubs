package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.troops.TroopsBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsCreateBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.ScoreboardMenu;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public void troopEducated(TroopsCreateBuilding createBuilding, ITroop troop) {
        CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(createBuilding.getOwnerUUID());
        TroopsBuilding troopsBuilding = getBuildingWhichFitTroop(coCPlayer.getTroopsCampBuildings(), troop);
        Objects.requireNonNullElse(troopsBuilding, createBuilding).addTroopAmount(troop, 1);
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
                int i = 0;
                do {
                    TroopsBuilding troopsBuilding = getBuildingWhichFitTroop(troopsCampBuildings, entry.getKey());
                    if (troopsBuilding == null)
                        break;

                    int space = troopsBuilding.getMaxAmountOfTroops() - troopsBuilding.getCurrentSizeOfTroops();
                    int movableTroops = Math.min(space, entry.getValue());

                    troopsBuilding.addTroopAmount(entry.getKey(), movableTroops);
                    troopsCreateBuilding.removeTroopAmount(entry.getKey(), movableTroops);
                    i += movableTroops;
                } while (i < entry.getValue());
            }
        }
    }

    /**
     * Get the first building, which can fit the given troop.
     * @param camps List<TroopsBuilding> - A list with all buildings to check.
     * @param troop {@link ITroop} - the troop to move.
     * @return {@link TroopsBuilding} - the building the troop can be moved.
     * @since 0.0.1
     */
    private TroopsBuilding getBuildingWhichFitTroop(List<TroopsBuilding> camps, ITroop troop) {
        for (TroopsBuilding playerBuilding : camps) {
            if (playerBuilding.getCurrentSizeOfTroops() + troop.getSize() < playerBuilding.getMaxAmountOfTroops())
                return playerBuilding;
        }
        return null;
    }

    /**
     * Starts the education from a troop.
     * @param apiPlayer APIPlayer - the apiplayer.
     * @param building {@link TroopsCreateBuilding} - the building to train.
     * @param troop {@link ITroop} - the troop to educated.
     * @return boolean - reload gui
     * @since 0.0.1
     */
    public boolean startEducation(APIPlayer apiPlayer, TroopsCreateBuilding building, ITroop troop) {
        CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(apiPlayer.getUniqueId());
        if (player.getResourceAmount(ResourceTypes.FOOD) < troop.getCostAmount()) {
            apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE, "${type}", ResourceTypes.FOOD.getColoredName(apiPlayer.getLanguage()));
            apiPlayer.playSound(Sound.ENTITY_VILLAGER_NO);
            return false;
        }

        boolean wasEmpty = building.getTroopsQueue().isEmpty();

        if (!building.addTroop(troop)) {
            apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_GUI_TRAIN_FULL, "${max}", ""+building.getMaxAmountOfTroops());
            apiPlayer.playSound(Sound.ENTITY_VILLAGER_NO);
            return false;
        }


        if (wasEmpty)
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> Schematics.createBuilding(building));

        player.removeResourceWithUpdate(ResourceTypes.FOOD, troop.getCostAmount());
        ScoreboardMenu.show(player);
        return true;
    }
}
