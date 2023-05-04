package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClubs;
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
import net.fununity.clashofclans.player.buildingmode.BuildingData;
import net.fununity.clashofclans.player.buildingmode.ConstructionMode;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.clashofclans.util.BuildingsAmountUtil;
import net.fununity.clashofclans.values.PlayerValues;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.cloud.client.CloudClient;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.actionbar.ActionbarMessage;
import net.fununity.main.api.common.util.RandomUtil;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
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
                new GeneralBuilding(uuid, UUID.randomUUID(), Buildings.TOWN_HALL, baseLoc, new int[]{82, 90}, (byte) 0, 0),
                new GeneralBuilding(uuid, UUID.randomUUID(), Buildings.BUILDER, baseLoc, new int[]{113, 117}, (byte) 0, 1),
                new ResourceGatherBuilding(uuid, UUID.randomUUID(), ResourceGathererBuildings.GOLD_MINER, baseLoc, new int[]{65, 75}, (byte) 0, 1, 650),
                new ResourceContainerBuilding(uuid, UUID.randomUUID(), ResourceContainerBuildings.GOLD_STOCK, baseLoc, new int[]{117, 104}, (byte) 0, 1, 0),
                new DefenseBuilding(uuid, UUID.randomUUID(), DefenseBuildings.CANNON, baseLoc, new int[]{116, 83}, (byte) 0, 1)));

        List<RandomWorldBuilding> rdmBuildings = new ArrayList<>();
        for (int i = 0; i < RandomUtil.getRandomInt(10) + 7; i++) {
            RandomWorldBuildings building = RandomWorldBuildings.getStartBuildings()[RandomUtil.getRandomInt(RandomWorldBuildings.getStartBuildings().length)];
            Location randomBuildingLocation = BuildingLocationUtil.getRandomBuildingLocation(baseLoc, startBuildings, building.getSize());
            byte rotation = (byte) RandomUtil.getRandomInt(4);
            if (randomBuildingLocation != null)
                rdmBuildings.add(new RandomWorldBuilding(uuid, UUID.randomUUID(), building, baseLoc, BuildingLocationUtil.transferInRelatives(baseLoc, building.getSize(), rotation, randomBuildingLocation), rotation, 1));
        }

        startBuildings.addAll(rdmBuildings);

        DatabaseBuildings.getInstance().buildBuilding(startBuildings.toArray(new GeneralBuilding[0]));

        EnumMap<PlayerValues, Integer> map = new EnumMap<>(PlayerValues.class);
        for (PlayerValues value : PlayerValues.values())
            map.put(value, value.getDefaultValue());

        CoCPlayer coCPlayer = new CoCPlayer(uuid, baseLoc, map, CloudClient.getInstance().getClientId(), System.currentTimeMillis(), startBuildings);
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
        ConstructionMode constructionMode = (ConstructionMode) player.getBuildingMode();
        IBuilding building = constructionMode.getBuilding();

        if (!checkIfPlayerCanBuildAnotherBuilding(player, player.getOwner(), building, constructionMode.getBuildings().size())) {
            return;
        }

        List<GeneralBuilding> allBuildings = new ArrayList<>();
        for (BuildingData buildingToConstruct : constructionMode.getBuildings()) {

            Location newLocation = buildingToConstruct.getLocation();
            newLocation.setY(ClashOfClubs.getBaseYCoordinate());
            byte rotation = buildingToConstruct.getRotation();

            allBuildings.add(getBuildingInstance(player.getUniqueId(), UUID.randomUUID(), building,
                    player.getBaseStartLocation(),
                    BuildingLocationUtil.transferInRelatives(player.getBaseStartLocation(),
                    BuildingLocationUtil.getRealMinimum(building.getSize(), rotation, newLocation)), rotation, 0));
        }

        player.removeResourceWithUpdate(building.getBuildingCostType(), building.getBuildingLevelData()[0].getUpgradeCost() * constructionMode.getBuildings().size());
        ConstructionManager.getInstance().startConstruction(player, allBuildings);
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
        if (cost == -1 || player.getResourceAmount(building.getBuilding().getBuildingCostType()) < cost) {
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(building.getOwnerUUID(), new ActionbarMessage(TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE), "${type}", building.getBuilding().getBuildingCostType().getColoredName(player.getOwner().getLanguage()));
            return false;
        }

        if (player.getNormalBuildings().stream().filter(b -> b.getBuilding() == Buildings.BUILDER).count() <= player.getConstructionBuildings().size()) {
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(building.getOwnerUUID(), new ActionbarMessage(TranslationKeys.COC_PLAYER_BUILDERS_WORKING));
            return false;
        }

        player.removeResourceWithUpdate(building.getBuilding().getBuildingCostType(), cost);
        ConstructionManager.getInstance().startConstruction(player, List.of(building));
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
                    coCPlayer.addExp(((IDestroyableBuilding) building.getBuilding()).getExp());
                    APIPlayer apiPlayer = coCPlayer.getOwner();
                    apiPlayer.playSound(Sound.ENTITY_PLAYER_LEVELUP);
                    apiPlayer.getPlayer().setLevel(0);
                    apiPlayer.getPlayer().setExp(0);
                    apiPlayer.getPlayer().giveExp(coCPlayer.getExp());
                }

                coCPlayer.removeResourceWithUpdate(building.getBuilding().getBuildingCostType(), ((RandomWorldBuilding) building).getRemoveCost());
            }

            if (((IDestroyableBuilding) building.getBuilding()).receiveFullPayPrice()) {
                coCPlayer.addResourceWithUpdate(building.getBuilding().getBuildingCostType(), building.getUpgradeCost());
            }
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
    public GeneralBuilding getBuildingInstance(UUID uuid, UUID buildingUUID, IBuilding building, Location location, int[] baseRelatives, byte rotation, int level) {
        try {
            return building.getBuildingClass()
                    .getConstructor(UUID.class, UUID.class, IBuilding.class, Location.class, int[].class, byte.class, int.class)
                    .newInstance(uuid, buildingUUID, building, location, baseRelatives, rotation, level);
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
    public GeneralBuilding getBuildingInstance(UUID uuid, UUID buildingUUID, IBuilding building, Location location, int[] baseRelatives, byte rotation, int level, double amount) {
        try {
            return building.getBuildingClass()
                    .getConstructor(UUID.class, UUID.class, IBuilding.class, Location.class, int[].class, byte.class, int.class, double.class)
                    .newInstance(uuid, buildingUUID, building, location, baseRelatives, rotation, level, amount);
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
    public GeneralBuilding getBuildingInstance(UUID uuid, UUID buildingUUID, IBuilding building, Location location, int[] baseRelatives,  byte rotation, int level, ConcurrentHashMap<ITroop, Integer> troops) {
        try {
            return building.getBuildingClass()
                    .getConstructor(UUID.class, UUID.class, IBuilding.class, Location.class, int[].class, byte.class, int.class, ConcurrentHashMap.class)
                    .newInstance(uuid, buildingUUID, building, location, baseRelatives, rotation, level, troops);
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

    /**
     * Checks and sends error messages if player is not able to build the amount of buildings.
     * @param coCPlayer CoCPlayer - the CoCPlayer
     * @param apiPlayer APIPlayer - the apiPlayer
     * @param building IBuilding - the building to build
     * @param amountOfCreation int - the amount of buildings to create at once.
     * @return boolean - player can build the amount of buildings.
     */
    public boolean checkIfPlayerCanBuildAnotherBuilding(CoCPlayer coCPlayer, APIPlayer apiPlayer, IBuilding building, int amountOfCreation) {
        long buildingPlayerHas = coCPlayer.getAllBuildings().stream().filter(b -> b.getBuilding() == building).count();
        int buildingsPerLevel = BuildingsAmountUtil.getAmountOfBuilding(building, coCPlayer.getTownHallLevel());

        if (buildingPlayerHas + amountOfCreation > buildingsPerLevel) {
            apiPlayer.sendActionbar(new ActionbarMessage(TranslationKeys.COC_PLAYER_NO_MORE_BUILDINGS).setDuration(5), "${max}", buildingsPerLevel + "");
            apiPlayer.playSound(Sound.ENTITY_VILLAGER_NO);
            return false;
        }

        if (building.getBuildingLevelData()[0].getUpgradeCost() * amountOfCreation > coCPlayer.getResourceAmount(building.getBuildingCostType())) {
            apiPlayer.sendActionbar(new ActionbarMessage(TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE).setDuration(5), "${type}", building.getBuildingCostType().getColoredName(apiPlayer.getLanguage()));
            apiPlayer.playSound(Sound.ENTITY_VILLAGER_NO);
            return false;
        }

        if (building.getBuildingLevelData()[0].getBuildTime() > 0 && coCPlayer.getNormalBuildings().stream().filter(b -> b.getBuilding() == Buildings.BUILDER).count() < (coCPlayer.getConstructionBuildings().size() + amountOfCreation)) {
            apiPlayer.sendActionbar(new ActionbarMessage(TranslationKeys.COC_PLAYER_BUILDERS_WORKING).setDuration(5));
            apiPlayer.playSound(Sound.ENTITY_VILLAGER_NO);
            return false;
        }

        return true;
    }

    /**
     * Empties all given resource gatherer buildings and calls {@link BuildingsManager#emptyGatherer(ResourceGatherBuilding, CoCPlayer)}
     * Rebuilds all necessary builder.
     * @param emptyGatherer List<ResourceGathererBuilding> - the buildings
     * @return boolean - close inventory
     */
    public boolean emptyGatherer(List<ResourceGatherBuilding> emptyGatherer) {
        if (emptyGatherer.stream().noneMatch(b -> b.getAmount() >= 1)) return false;

        UUID uuid = emptyGatherer.get(0).getOwnerUUID();
        CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(uuid);
        if (player == null)
            return false;

        List<GeneralBuilding> rebuildBuildings = new ArrayList<>();
        emptyGatherer.forEach(b -> rebuildBuildings.addAll(emptyGatherer(b, player)));

        if (!rebuildBuildings.isEmpty())
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> Schematics.createBuildings(rebuildBuildings));

        ScoreboardMenu.show(player);

        if (TutorialManager.getInstance().getState(uuid) == TutorialManager.TutorialState.COLLECT_RESOURCE) {
            Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () -> {
                CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(uuid);
                if (coCPlayer != null)
                    TutorialManager.getInstance().finished(coCPlayer);
            }, 1L);
            return true;
        }
        return false;
    }

    /**
     * Drains the gatherer and calls {@link CoCPlayer#fillResourceToContainer(ResourceTypes, double)}.
     * @return List<GeneralBuilding> - buildings that need a rebuild
     * @since 0.0.2
     */
    private List<GeneralBuilding> emptyGatherer(ResourceGatherBuilding building, CoCPlayer cocPlayer) {
        if (building.getAmount() <= 0) return new ArrayList<>();

        double toAdd = Math.min(cocPlayer.getMaxResourceContainable(building.getContainingResourceType())
                - cocPlayer.getResourceAmount(building.getContainingResourceType()), building.getAmount());
        if (toAdd <= 0) {
            APIPlayer owner = cocPlayer.getOwner();
            if (owner != null)
                owner.sendActionbar(new ActionbarMessage(TranslationKeys.COC_PLAYER_NO_RESOURCE_TANKS));
            return new ArrayList<>();
        }

        List<GeneralBuilding> needRebuild = new ArrayList<>();
        if (building.setAmount(building.getAmount() - toAdd))
            needRebuild.add(building);

        needRebuild.addAll(cocPlayer.fillResourceToContainer(building.getContainingResourceType(), toAdd));
        return needRebuild;
    }

}
