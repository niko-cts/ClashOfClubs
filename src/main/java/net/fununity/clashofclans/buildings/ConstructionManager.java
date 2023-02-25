package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.*;
import net.fununity.clashofclans.buildings.instances.troops.TroopsBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsCreateBuilding;
import net.fununity.clashofclans.buildings.list.Buildings;
import net.fununity.clashofclans.buildings.list.ResourceContainerBuildings;
import net.fununity.clashofclans.buildings.list.ResourceGathererBuildings;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.ScoreboardMenu;
import net.fununity.clashofclans.player.TutorialManager;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;

import java.util.UUID;

public class ConstructionManager {

    private static ConstructionManager instance;

    public static ConstructionManager getInstance() {
        if(instance == null)
            return instance = new ConstructionManager();
        return instance;
    }

    private ConstructionManager() {
        // not needed
    }

    /**
     * Creates the construction of the building.
     * @param player CoCPlayer - the player.
     * @param building GeneralBuilding - the building to construct.
     * @since 0.0.1
     */
    public void startConstruction(CoCPlayer player, GeneralBuilding building) {
        player.removeBuilding(building);

        if (building.getBuilding() == Buildings.TOWN_HALL && building.getLevel() == 0)
            FunUnityAPI.getInstance().getActionbarManager().clearActionbar(player.getUniqueId());

        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            ConstructionBuilding constructionBuilding = new ConstructionBuilding(building,
                    System.currentTimeMillis() + (building.getBuildingDuration() * 1000L));
            DatabaseBuildings.getInstance().constructBuilding(constructionBuilding);

            if (building.getBuilding() != Buildings.TOWN_HALL && building.getLevel() == 0)
                DatabaseBuildings.getInstance().buildBuilding(building);
            else
                Schematics.removeBuilding(building.getCoordinate(), building.getBuilding().getSize(), building.getRotation());

            BuildingLocationUtil.savePlayerFromBuilding(building);
            Schematics.createBuilding(constructionBuilding);

            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> player.addBuilding(constructionBuilding));
        });
    }

    /**
     * Gets called, when a building is fully constructed.
     * @param constructionBuilding {@link ConstructionBuilding} - the constructed building.
     * @since 0.0.1
     */
    public void finishedConstruction(ConstructionBuilding constructionBuilding) {
        UUID uuid = constructionBuilding.getOwnerUUID();
        CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(uuid);
        GeneralBuilding building = constructionBuilding.getConstructedBuilding();

        player.removeBuilding(constructionBuilding);

        building.setLevel(building.getLevel() + 1);
        building.setCurrentHP(null, building.getMaxHP());

        APIPlayer owner = player.getOwner();
        if (owner != null) {
            constructionBuilding.hideHologram(owner);
        }

        if (building instanceof TroopsBuilding && !(building instanceof TroopsCreateBuilding))
            TroopsBuildingManager.getInstance().moveTroopsFromCreationToCamp(player);

        player.addExp(building.getExp());
        player.getOwner().getPlayer().setLevel(player.getExp());

        ScoreboardMenu.show(player);

        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            // database building
            DatabaseBuildings.getInstance().removeConstruction(building.getBuildingUUID());
            DatabaseBuildings.getInstance().upgradeBuilding(building);

            // schematics
            Schematics.removeBuilding(building.getCoordinate(), building.getBuilding().getSize(), building.getRotation());
            BuildingLocationUtil.savePlayerFromBuilding(building);
            Schematics.createBuilding(building);

            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> {
                player.addBuilding(building);

                if (building instanceof GeneralHologramBuilding)
                   ((GeneralHologramBuilding) building).updateHologram(((GeneralHologramBuilding) building).getShowText());

                if (building.getBuilding() == Buildings.TOWN_HALL && TutorialManager.getInstance().getState(player.getUniqueId()) == TutorialManager.TutorialState.REPAIR_TOWNHALL)
                    TutorialManager.getInstance().finished(player);
                else if((building.getBuilding() == ResourceGathererBuildings.FARM || building.getBuilding() == ResourceContainerBuildings.BARN_STOCK) &&
                        TutorialManager.getInstance().getState(player.getUniqueId()) == TutorialManager.TutorialState.BUILD_FARM)
                    TutorialManager.getInstance().finished(player);
            });
        });
    }

}
