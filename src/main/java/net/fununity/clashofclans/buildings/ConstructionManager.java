package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.instances.ConstructionBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralHologramBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsCreateBuilding;
import net.fununity.clashofclans.buildings.list.*;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.ScoreboardMenu;
import net.fununity.clashofclans.player.TutorialManager;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;

import java.util.*;

public class ConstructionManager {

    private static ConstructionManager instance;

    public static ConstructionManager getInstance() {
        if (instance == null)
            return instance = new ConstructionManager();
        return instance;
    }

    private ConstructionManager() {
        // not needed
    }

    /**
     * Creates the construction of the building.
     *
     * @param player    CoCPlayer - the player.
     * @param buildings List<GeneralBuilding> - all buildings to construct.
     * @since 0.0.1
     */
    public void startConstruction(CoCPlayer player, List<GeneralBuilding> buildings) {
        buildings.forEach(player::removeBuilding);

        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            if (buildings.get(0).getBuilding() != Buildings.TOWN_HALL && buildings.get(0).getLevel() == 0) {
                DatabaseBuildings.getInstance().buildBuilding(buildings.toArray(new GeneralBuilding[0]));
            } else {
                buildings.forEach(building -> Schematics.removeBuilding(building.getCoordinate(), building.getBuilding().getSize(), building.getRotation()));
            }

            List<ConstructionBuilding> constructions = new ArrayList<>();
            buildings.forEach(building -> constructions.add(new ConstructionBuilding(building,
                    System.currentTimeMillis() + (building.getBuildingDuration() * 1000L))));

            DatabaseBuildings.getInstance().constructBuilding(constructions);

            Schematics.createConstruction(constructions);

            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> player.addConstructions(constructions));
        });
    }

    /**
     * Is called by {@link net.fununity.clashofclans.TickTimerManager} if any construction of any player is finished.
     * Removes construction buildings from the player
     * Constructs the new buildings all together
     * Updates the database
     *
     * @param allFinishedConstructions Map<UUID, List<{@link ConstructionBuilding}>> - all finished constructions.
     * @since 0.0.1
     */
    public void finishedConstruction(Map<UUID, List<ConstructionBuilding>> allFinishedConstructions) {
        Map<UUID, List<GeneralBuilding>> finishedBuildings = new HashMap<>();
        for (Map.Entry<UUID, List<ConstructionBuilding>> entry : allFinishedConstructions.entrySet()) {
            CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(entry.getKey());
            List<ConstructionBuilding> constructionBuildings = entry.getValue();

            player.removeConstructions(constructionBuildings);
            APIPlayer owner = player.getOwner();
            List<GeneralBuilding> buildings = new ArrayList<>();

            for (ConstructionBuilding construction : constructionBuildings) {
                GeneralBuilding building = construction.getConstructedBuilding();

                building.setLevel(building.getLevel() + 1);
                building.setCurrentHP(null, building.getMaxHP());

                if (owner != null) {
                    construction.hideHologram(owner);
                }

                buildings.add(building);
            }

            finishedBuildings.put(entry.getKey(), buildings);
        }

        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            List<GeneralBuilding> allFinishedBuildings = new ArrayList<>();

            List<UUID> buildingUUIDs = new ArrayList<>();
            for (List<GeneralBuilding> finished : finishedBuildings.values()) {
                allFinishedBuildings.addAll(finished);
                finished.forEach(building -> buildingUUIDs.add(building.getBuildingUUID()));
            }

            // database building
            DatabaseBuildings.getInstance().removeConstruction(buildingUUIDs);
            DatabaseBuildings.getInstance().upgradeBuilding(allFinishedBuildings);

            for (GeneralBuilding building : allFinishedBuildings) {
                // schematics
                Schematics.removeBuilding(building.getCoordinate(), building.getBuilding().getSize(), building.getRotation());
                BuildingLocationUtil.savePlayerFromBuilding(building);
            }

            // wait for the removing of blocks
            Bukkit.getScheduler().runTaskLaterAsynchronously(ClashOfClubs.getInstance(), () -> {

                long ticks = Schematics.createBuildings(allFinishedBuildings);

                Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () -> {
                    for (Map.Entry<UUID, List<GeneralBuilding>> entry : finishedBuildings.entrySet()) {
                        List<GeneralBuilding> buildings = entry.getValue();
                        CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(entry.getKey());

                        for (GeneralBuilding building : entry.getValue()) {
                            player.addBuilding(building);

                            player.addExp(building.getExp());

                            if (building instanceof GeneralHologramBuilding)
                                ((GeneralHologramBuilding) building).updateHologram(((GeneralHologramBuilding) building).getShowText());
                        }

                        if (entry.getValue().stream().anyMatch(building -> building instanceof TroopsBuilding && !(building instanceof TroopsCreateBuilding)))
                            TroopsBuildingManager.getInstance().moveTroopsFromCreationToCamp(player);

                        // Tutorial management
                        if (buildings.stream().anyMatch(b -> b.getBuilding() == Buildings.TOWN_HALL) && TutorialManager.getInstance().getState(player.getUniqueId()) == TutorialManager.TutorialState.REPAIR_TOWNHALL)
                            TutorialManager.getInstance().finished(player);

                        if ((buildings.stream().anyMatch(b -> b.getBuilding() == ResourceGathererBuildings.FARM) || buildings.stream().anyMatch(b -> b.getBuilding() == ResourceContainerBuildings.BARN_STOCK)) &&
                                TutorialManager.getInstance().getState(player.getUniqueId()) == TutorialManager.TutorialState.BUILD_FARM) {
                            // finish BUILD_FARM Tutorial if food container&gatherer were built
                            if (!player.getResourceContainerBuildings(ResourceTypes.FOOD).isEmpty() && !player.getResourceGatherBuildings(ResourceTypes.FOOD).isEmpty())
                                TutorialManager.getInstance().finished(player);
                            buildings.stream().filter(b -> b.getBuilding() == ResourceGathererBuildings.FARM).findFirst().ifPresent(b -> {
                                if (((ResourceGatherBuilding) b).setAmount(200)) {
                                    Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> Schematics.createBuilding(b));
                                }
                            });
                        }

                        if ((buildings.stream().anyMatch(b -> b.getBuilding() == TroopCreationBuildings.BARRACKS) || buildings.stream().anyMatch(b -> b.getBuilding() == TroopBuildings.ARMY_CAMP)) &&
                                TutorialManager.getInstance().getState(player.getUniqueId()) == TutorialManager.TutorialState.TROOPS) {
                            if (!player.getTroopsCreateBuildings().isEmpty() && !player.getTroopsCampBuildings().isEmpty())
                                TutorialManager.getInstance().finished(player);
                        }

                        if (buildings.stream().anyMatch(b -> b.getBuilding() == DefenseBuildings.CANNON) && TutorialManager.getInstance().getState(player.getUniqueId()) == TutorialManager.TutorialState.DEFENSE) {
                            TutorialManager.getInstance().finished(player);
                        }


                        APIPlayer apiPlayer = player.getOwner();
                        if (apiPlayer != null) {
                            apiPlayer.getPlayer().setLevel(0);
                            apiPlayer.getPlayer().setExp(0);
                            apiPlayer.getPlayer().giveExp(player.getExp());

                            ScoreboardMenu.show(player);
                        }
                    }
                }, ticks + 1);
            }, 10L);
        });
    }

}
