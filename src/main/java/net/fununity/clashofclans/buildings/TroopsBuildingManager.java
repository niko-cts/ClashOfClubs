package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.TroopsBuilding;
import net.fununity.clashofclans.buildings.classes.TroopsCreateBuilding;
import net.fununity.clashofclans.buildings.list.TroopBuildings;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.troops.Troops;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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
     * Will be called when a troop finished in {@link TroopsCreateBuilding}.
     * Will try to move the troop to an {@link TroopsBuilding}.
     * @param building {@link TroopsCreateBuilding} - the building the troop finished.
     * @since 0.0.1
     */
    public void troopEducated(TroopsCreateBuilding building) {
        ITroop troop = building.getTroopsQueue().poll();
        if (troop == null) return;

        if (!building.getTroopsQueue().isEmpty())
            building.setTrainSecondsLeft(building.getTroopsQueue().peek().getTrainDuration());

        List<TroopsBuilding> buildings = getTroopBuildings(building.getUuid());
        buildings.removeIf(list -> list instanceof TroopsCreateBuilding);
        buildings.sort(Comparator.comparingInt(TroopsBuilding::getCurrentSizeOfTroops));

        TroopsBuilding troopsBuilding = getBuildingWhichFitTroop(buildings, troop);
        if (troopsBuilding == null)
            troopsBuilding = building;

        troopsBuilding.addTroopAmount(troop, 1);
        DatabaseBuildings.getInstance().updateTroopsData(troopsBuilding.getCoordinate(), troop, building.getTroopAmount().get(troop));

        if (!building.equals(troopsBuilding) && !building.getTroopAmount().isEmpty())
            moveTroopsToField(PlayerManager.getInstance().getPlayer(building.getUuid()));
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
                        fitAbleTroop.addTroopAmount(entry.getKey(), 1);
                        removed++;
                    }
                    if (removed != 0)
                        ((TroopsCreateBuilding) building).addTroopAmount(entry.getKey(), -removed);
                }
            }
        }

        for (TroopsBuilding changedBuilding : changedBuildings)
            DatabaseBuildings.getInstance().updateTroopsData(changedBuilding);
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
     * Returns a list of all {@link TroopsBuilding} the player has.
     *
     * @param uuid UUID - uuid of player.
     * @return List<TroopsBuilding> - A list of the buildings the player has.
     * @since 0.0.1
     */
    public List<TroopsBuilding> getTroopBuildings(UUID uuid) {
        if (PlayerManager.getInstance().isCached(uuid))
            return PlayerManager.getInstance().getPlayer(uuid).getBuildings().stream().filter(b -> b instanceof TroopsBuilding).map(list -> (TroopsBuilding) list).collect(Collectors.toList());

        List<TroopsBuilding> buildings = new ArrayList<>();
        try (ResultSet building = DatabaseBuildings.getInstance().getSpecifiedBuilding(TroopBuildings.values())) {
            while (building != null && building.next()) {
                int x = building.getInt("x");
                int z = building.getInt("z");
                Location location = new Location(ClashOfClubs.getInstance().getWorld(), x, ClashOfClubs.getBaseYCoordinate(), z);

                ConcurrentMap<ITroop, Integer> amount = new ConcurrentHashMap<>();
                for (Troops troop : Troops.values())
                    amount.put(troop, building.getInt(troop.name().toLowerCase()));

                buildings.add(new TroopsBuilding(uuid, BuildingsManager.getInstance().getBuildingById(building.getString("buildingID")),
                        location, building.getByte("rotation"), building.getInt("level"), amount));
            }
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }

        return buildings;
    }

    /**
     * Starts the education from a troop.
     * @param building {@link TroopsCreateBuilding} - the building to train.
     * @param troop {@link ITroop} - the troop to educated.
     * @since 0.0.1
     */
    public void startEducation(TroopsCreateBuilding building, ITroop troop) {
        CoCPlayer player = PlayerManager.getInstance().getPlayer(building.getUuid());
        if (player.getResource(ResourceTypes.FOOD) < troop.getCostAmount()) {
            APIPlayer apiPlayer = player.getOwner();
            apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE, "${type}", ResourceTypes.FOOD.getColoredName(apiPlayer.getLanguage()));
            return;
        }

        player.removeResource(ResourceTypes.FOOD, troop.getCostAmount());
        building.getTroopsQueue().add(troop);
        if (building.getTroopsQueue().size() == 1)
            building.setTrainSecondsLeft(troop.getTrainDuration());
    }
}
