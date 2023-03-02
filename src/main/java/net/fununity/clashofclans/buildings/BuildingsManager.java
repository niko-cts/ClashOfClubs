package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.ConstructionBuilding;
import net.fununity.clashofclans.buildings.instances.DefenseBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.destroyables.RandomWorldBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceContainerBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDestroyableBuilding;
import net.fununity.clashofclans.buildings.list.*;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.database.DatabasePlayer;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.ScoreboardMenu;
import net.fununity.clashofclans.player.TutorialManager;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.cloud.client.CloudClient;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.actionbar.ActionbarMessage;
import net.fununity.main.api.common.util.RandomUtil;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BuildingsManager {

    private static BuildingsManager instance;

    /**
     * Get the singleton instance of this class.
     * @return {@link BuildingsManager} - the singleton instance.
     * @since 0.0.1
     */
    public static BuildingsManager getInstance() {
        if (instance == null)
            instance = new BuildingsManager();
        return instance;
    }

    private final List<IBuilding> allBuildings;

    /**
     * Instantiates the class.
     * Loads all Building enums to the cache.
     * @since 0.0.1
     */
    private BuildingsManager() {
        this.allBuildings = new ArrayList<>();
        this.allBuildings.addAll(Arrays.asList(Buildings.values()));
        this.allBuildings.addAll(Arrays.asList(ResourceContainerBuildings.values()));
        this.allBuildings.addAll(Arrays.asList(ResourceGathererBuildings.values()));
        this.allBuildings.addAll(Arrays.asList(TroopBuildings.values()));
        this.allBuildings.addAll(Arrays.asList(TroopCreationBuildings.values()));
        this.allBuildings.addAll(Arrays.asList(DecorativeBuildings.values()));
        this.allBuildings.addAll(Arrays.asList(RandomWorldBuildings.values()));
        this.allBuildings.addAll(Arrays.asList(DefenseBuildings.values()));
        this.allBuildings.addAll(Arrays.asList(WallBuildings.values()));
    }

    /**
     * Creates a new island for the player.
     * @param player APIPlayer - the player to create the island.
     * @since 0.0.1
     */
    public CoCPlayer createNewIsland(APIPlayer player) {
       return createNewIsland(player, DatabasePlayer.getInstance().getHighestCoordinate());
    }

    /**
     * Creates a new island for the player.
     * @param player APIPlayer - the player to create the island.
     * @param baseLoc Location - the base location.
     * @since 0.0.1
     */
    public CoCPlayer createNewIsland(APIPlayer player, Location baseLoc) {
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> player.getPlayer().teleport(baseLoc.clone().add(ClashOfClubs.getBaseSize() / 2.0, 30, ClashOfClubs.getBaseSize() / 2.0)));
        player.getTitleSender().sendTitle(TranslationKeys.COC_PLAYER_LOADING_NEW_BASE_TITLE, 40 * 20);
        player.getTitleSender().sendSubtitle(TranslationKeys.COC_PLAYER_LOADING_NEW_BASE_SUBTITLE, 40 * 20);
        List<GeneralBuilding> startBuildings = new ArrayList<>(Arrays.asList(
                new GeneralBuilding(uuid, UUID.randomUUID(), Buildings.TOWN_HALL, baseLoc.clone().add(82, 0, 90), (byte) 0, 0),
                new GeneralBuilding(uuid, UUID.randomUUID(), Buildings.BUILDER, baseLoc.clone().add(113, 0, 117), (byte) 0, 1),
                new ResourceGatherBuilding(uuid, UUID.randomUUID(), ResourceGathererBuildings.GOLD_MINER, baseLoc.clone().add(65, 0, 75), (byte) 0, 1, 650),
                new ResourceContainerBuilding(uuid, UUID.randomUUID(), ResourceContainerBuildings.GOLD_STOCK, baseLoc.clone().add(117, 0, 104), (byte) 0, 1, 0),
                new DefenseBuilding(uuid, UUID.randomUUID(), DefenseBuildings.CANNON, BuildingLocationUtil.getRealMinimum(DefenseBuildings.CANNON.getSize(), (byte) 2, baseLoc.clone().add(116, 0, 83)), (byte) 2, 1)));

        List<RandomWorldBuilding> rdmBuildings = new ArrayList<>();
        for (int i = 0; i < RandomUtil.getRandomInt(10) + 7; i++) {
            RandomWorldBuildings building = RandomWorldBuildings.getStartBuildings()[RandomUtil.getRandomInt(RandomWorldBuildings.getStartBuildings().length)];
            Location randomBuildingLocation = BuildingLocationUtil.getRandomBuildingLocation(baseLoc, startBuildings, building.getSize());
            byte rotation = (byte) RandomUtil.getRandomInt(4);
            if (randomBuildingLocation != null)
                rdmBuildings.add(new RandomWorldBuilding(uuid, UUID.randomUUID(), building, BuildingLocationUtil.getRealMinimum(building.getSize(), rotation, randomBuildingLocation), rotation, 1));
        }

        startBuildings.addAll(rdmBuildings);

        DatabaseBuildings.getInstance().buildBuilding(startBuildings.toArray(new GeneralBuilding[0]));


        CoCPlayer coCPlayer = new CoCPlayer(uuid, baseLoc, 0, 0, 200, CloudClient.getInstance().getClientId(), System.currentTimeMillis(), startBuildings);
        DatabasePlayer.getInstance().createUser(coCPlayer);

        long takingTicks = Schematics.createPlayerBase(baseLoc, startBuildings) + 5;

        player.getTitleSender().sendTitle(TranslationKeys.COC_PLAYER_LOADING_NEW_PLACING_TITLE, 10 * 20);
        player.getTitleSender().sendSubtitle(TranslationKeys.COC_PLAYER_LOADING_NEW_PLACING_SUBTITLE, 10 * 20);

        Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () -> {
            player.getTitleSender().sendTitle(TranslationKeys.COC_PLAYER_LOADING_NEW_FINISHED_TITLE, 2 * 20);
            player.getTitleSender().sendSubtitle(TranslationKeys.COC_PLAYER_LOADING_NEW_FINISHED_SUBTITLE, 2 * 20);
            TutorialManager.getInstance().startTutorialState(coCPlayer, TutorialManager.TutorialState.COLLECT_RESOURCE);
            coCPlayer.visit(player, true);
            ScoreboardMenu.show(coCPlayer);
        }, takingTicks);

        return coCPlayer;
    }

    /**
     * Creates a building the player wants.
     * @param player {@link CoCPlayer} - the player, who builds
     * @since 0.0.1
     */
    public void build(CoCPlayer player) {
        IBuilding building = (IBuilding) player.getBuildingMode()[1];
        int cost = building.getBuildingLevelData()[0].getUpgradeCost();
        if (player.getResourceAmount(building.getResourceType()) < cost)
            return;

        Location newLocation = (Location) player.getBuildingMode()[0];
        newLocation.setY(ClashOfClubs.getBaseYCoordinate());
        byte rotation = (byte) player.getBuildingMode()[2];

        GeneralBuilding generalBuilding = getBuildingInstance(player.getUniqueId(), UUID.randomUUID(), building,
                BuildingLocationUtil.getRealMinimum(building.getSize(), rotation, newLocation), rotation, 0);
        if (generalBuilding == null)
            return;

        player.removeResource(building.getResourceType(), cost);
        ConstructionManager.getInstance().startConstruction(player, generalBuilding);
    }

    /**
     * Upgrades the given building.
     * @param building {@link GeneralBuilding} - the building the player wants to upgrade.
     * @return boolean - upgrade was successful.
     * @since 0.0.1
     */
    public boolean upgrade(GeneralBuilding building) {
        int cost = building.getUpgradeCost();
        CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(building.getOwnerUUID());
        if (cost == -1 || player.getResourceAmount(building.getBuilding().getResourceType()) < cost) {
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(building.getOwnerUUID(), new ActionbarMessage(TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE), "${type}", building.getBuilding().getResourceType().getColoredName(player.getOwner().getLanguage()));
            return false;
        }

        if (player.getNormalBuildings().stream().filter(b -> b.getBuilding() == Buildings.BUILDER).count() <= player.getConstructionBuildings().size()) {
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(building.getOwnerUUID(), new ActionbarMessage(TranslationKeys.COC_PLAYER_BUILDERS_WORKING));
            return false;
        }

        player.removeResource(building.getBuilding().getResourceType(), cost);
        ConstructionManager.getInstance().startConstruction(player, building);
        return true;
    }

    /**
     * Removes a building from the world.
     * @param building GeneralBuilding - the building.
     * @since 0.0.1
     */
    public void removeBuilding(GeneralBuilding building) {
        CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(building.getOwnerUUID());
        coCPlayer.removeBuilding(building);
        if (building.getBuilding() instanceof IDestroyableBuilding) {

            if (building instanceof RandomWorldBuilding) {
                int gemsToAdd = RandomUtil.getRandomInt(((IDestroyableBuilding) building.getBuilding()).getGems());
                if (gemsToAdd > 0) {
                    coCPlayer.setGems(coCPlayer.getGems() + gemsToAdd);
                    coCPlayer.getOwner().playSound(Sound.ENTITY_PLAYER_LEVELUP);
                }

                coCPlayer.removeResource(building.getBuilding().getResourceType(), ((RandomWorldBuilding) building).getRemoveCost());
            }

            if (((IDestroyableBuilding) building.getBuilding()).receiveFullPayPrice())
                coCPlayer.fillResourceToContainer(building.getBuilding().getResourceType(), building.getUpgradeCost());
        }

        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            DatabaseBuildings.getInstance().deleteBuilding(building);
            Schematics.removeBuilding(building.getCoordinate(), building.getBuilding().getSize(), building.getRotation());
        });
    }

    /**
     * Get the building instance from the interface class.
     * @see IBuilding#getBuildingClass()
     * @param uuid UUID - user uuid
     * @param buildingUUID - uuid of building
     * @param building {@link IBuilding} - the interface building class.
     * @param location Location - the location the building stands.
     * @param level int - the level the building has.
     * @return {@link GeneralBuilding} - the building instance.
     * @since 0.0.1
     */
    public GeneralBuilding getBuildingInstance(UUID uuid, UUID buildingUUID, IBuilding building, Location location, byte rotation, int level) {
        try {
            return building.getBuildingClass()
                    .getConstructor(UUID.class, UUID.class, IBuilding.class, Location.class, byte.class, int.class)
                    .newInstance(uuid, buildingUUID, building, location, rotation, level);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            ClashOfClubs.getInstance().getLogger().warning(e.getMessage());
            return null;
        }
    }

    /**
     * Get the building instance from the interface class.
     * @see IBuilding#getBuildingClass()
     * @param uuid UUID - user uuid
     * @param buildingUUID - uuid of building
     * @param building {@link IBuilding} - the interface building class.
     * @param location Location - the location the building stands.
     * @param level int - the level the building has.
     * @return {@link GeneralBuilding} - the building instance.
     * @since 0.0.1
     */
    public GeneralBuilding getBuildingInstance(UUID uuid, UUID buildingUUID, IBuilding building, Location location, byte rotation, int level, double amount) {
        try {
            return building.getBuildingClass()
                    .getConstructor(UUID.class, UUID.class, IBuilding.class, Location.class, byte.class, int.class, double.class)
                    .newInstance(uuid, buildingUUID, building, location, rotation, level, amount);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            ClashOfClubs.getInstance().getLogger().warning(e.getMessage());
            return null;
        }
    }

    /**
     * Get the building instance from the interface class.
     * @see IBuilding#getBuildingClass()
     * @param uuid UUID - user uuid
     * @param buildingUUID - uuid of building
     * @param building {@link IBuilding} - the interface building class.
     * @param location Location - the location the building stands.
     * @param level int - the level the building has.
     * @return {@link GeneralBuilding} - the building instance.
     * @since 0.0.1
     */
    public GeneralBuilding getBuildingInstance(UUID uuid, UUID buildingUUID, IBuilding building, Location location, byte rotation, int level, ConcurrentHashMap<ITroop, Integer> troops) {
        try {
            return building.getBuildingClass()
                    .getConstructor(UUID.class, UUID.class, IBuilding.class, Location.class, byte.class, int.class, ConcurrentHashMap.class)
                    .newInstance(uuid, buildingUUID, building, location, rotation, level, troops);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            ClashOfClubs.getInstance().getLogger().warning(e.getMessage());
            return null;
        }
    }
    /**
     * Get the building name from all buildings.
     * @param name String - the name of the building.
     * @see IBuilding#name()
     * @return {@link IBuilding} - the building.
     * @since 0.0.1
     */
    public IBuilding getBuildingById(String name) {
        return allBuildings.stream().filter(b -> b.name().equals(name)).findFirst().orElse(null);
    }

}
