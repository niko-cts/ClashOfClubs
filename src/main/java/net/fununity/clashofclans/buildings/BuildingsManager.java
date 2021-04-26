package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.classes.*;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDestroyableBuilding;
import net.fununity.clashofclans.buildings.list.*;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.DatabasePlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.actionbar.ActionbarMessage;
import net.fununity.main.api.common.util.RandomUtil;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

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
    }

    /**
     * Creates a new island for the player.
     * @param uuid UUID - the player to create the island.
     * @since 0.0.1
     */
    public CoCPlayer createNewIsland(UUID uuid) {
       return createNewIsland(uuid, DatabasePlayer.getInstance().getHighestCoordinate());
    }

    /**
     * Creates a new island for the player.
     * @param uuid UUID - the player to create the island.
     * @param baseLoc Location - the base location.
     * @since 0.0.1
     */
    public CoCPlayer createNewIsland(UUID uuid, Location baseLoc) {
        DatabasePlayer.getInstance().createUser(uuid, baseLoc);
        Map<ResourceTypes, Integer> resources = new EnumMap<>(ResourceTypes.class);
        for (ResourceTypes resource : ResourceTypes.values())
            resources.put(resource, 0);

        resources.put(ResourceTypes.GEMS, 200);

        CoCPlayer coCPlayer = new CoCPlayer(uuid, baseLoc, resources, 0);

        Schematics.createPlayerBase(baseLoc);
        List<GeneralBuilding> startBuildings = new ArrayList<>(Arrays.asList(
                new GeneralBuilding(uuid, Buildings.TOWN_HALL, baseLoc.clone().add(50, 0, 50), (byte) 0, 0),
                new GeneralBuilding(uuid, Buildings.BUILDER, baseLoc.clone().add(15, 0, 15), (byte) 0, 1),
                new ResourceGatherBuilding(uuid, ResourceGathererBuildings.GOLD_MINER, baseLoc.clone().add(30, 0, 50), (byte) 1, 1),
                new ResourceContainerBuilding(uuid, ResourceContainerBuildings.GOLD_STOCK, baseLoc.clone().add(50, 0, 30), (byte) 0, 1, 350),
                new ResourceContainerBuilding(uuid, ResourceContainerBuildings.BARN_STOCK, baseLoc.clone().add(40, 0, 30), (byte) 1, 1, 350),
                new GeneralBuilding(uuid, Buildings.CLUB_TOWER, baseLoc.clone().add(50, 0, 10), (byte) 0, 0)));


        for (GeneralBuilding building : startBuildings) {
            building.setCoordinate(BuildingLocationUtil.getCoordinate(building));
            coCPlayer.getBuildings().add(building);
            DatabaseBuildings.getInstance().buildBuilding(uuid, building);
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> Schematics.createBuilding(building));
        }

        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> {
            List<RandomWorldBuilding> rdmBuildings = new ArrayList<>();
            for (int i = 0; i < RandomUtil.getRandomInt(15) + 10; i++) {
                RandomWorldBuildings building = RandomWorldBuildings.getStartBuildings()[RandomUtil.getRandomInt(RandomWorldBuildings.getStartBuildings().length)];
                Location randomBuildingLocation = BuildingLocationUtil.getRandomBuildingLocation(baseLoc, startBuildings, building.getSize());
                if (randomBuildingLocation != null)
                    rdmBuildings.add(new RandomWorldBuilding(uuid, building, randomBuildingLocation, (byte) 1, 1));
            }
            for (RandomWorldBuilding building : rdmBuildings) {
                building.setCoordinate(BuildingLocationUtil.getCoordinate(building));
                coCPlayer.getBuildings().add(building);
                DatabaseBuildings.getInstance().buildBuilding(uuid, building);
                Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> Schematics.createBuilding(building));
            }
        });

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
        if (player.getResource(building.getResourceType()) < cost)
            return;

        byte rotation = (byte) player.getBuildingMode()[2];

        GeneralBuilding generalBuilding = getBuildingInstance(player.getUniqueId(), building, BuildingLocationUtil.getCoordinate(building.getSize(), rotation, (Location) player.getBuildingMode()[0]), rotation, 0);
        if (generalBuilding == null)
            return;

        player.removeResource(building.getResourceType(), cost);
        createConstruction(player, generalBuilding);
    }

    /**
     * Upgrades the given building.
     * @param building {@link GeneralBuilding} - the building the player wants to upgrade.
     * @return boolean - upgrade was successful.
     * @since 0.0.1
     */
    public boolean upgrade(GeneralBuilding building) {
        int cost = building.getUpgradeCost();
        CoCPlayer player = PlayerManager.getInstance().getPlayer(building.getUuid());
        if (cost == -1 || player.getResource(building.getBuilding().getResourceType()) < cost) {
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(building.getUuid(), new ActionbarMessage(TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE), "${type}", building.getBuilding().getResourceType().getColoredName(player.getOwner().getLanguage()));
            return false;
        }

        if(player.getBuildings().stream().filter(b -> b.getBuilding() == Buildings.BUILDER).count() <= player.getBuildings().stream().filter(b -> b instanceof ConstructionBuilding).count()) {
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(building.getUuid(), new ActionbarMessage(TranslationKeys.COC_PLAYER_BUILDERS_WORKING));
            return false;
        }

        player.removeResource(building.getBuilding().getResourceType(), cost);
        createConstruction(player, building);
        return true;
    }


    private void createConstruction(CoCPlayer player, GeneralBuilding building) {
        ConstructionBuilding constructionBuilding = new ConstructionBuilding(building, building.getMaxBuildingDuration());
        player.getBuildings().add(constructionBuilding);
        player.getBuildings().remove(building);
        if (constructionBuilding.getBuildingDuration() < 1) {
            finishedBuilding(constructionBuilding);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> {
            if (building.getBuilding() == Buildings.TOWN_HALL || building.getLevel() != 0)
                Schematics.removeBuilding(building.getCoordinate(), building.getBuilding().getSize());

            Schematics.createBuilding(constructionBuilding);
            DatabaseBuildings.getInstance().constructBuilding(constructionBuilding);
        });
    }

    public void finishedBuilding(ConstructionBuilding constructionBuilding) {
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> {
            UUID uuid = constructionBuilding.getUuid();

            GeneralBuilding building = constructionBuilding.getConstructionBuilding();

            if (PlayerManager.getInstance().isCached(uuid)) {
                CoCPlayer player = PlayerManager.getInstance().getPlayer(uuid);
                APIPlayer owner = player.getOwner();
                if (owner != null)
                    constructionBuilding.getHolograms().forEach(owner::hideHologram);

                player.getBuildings().add(building);
                player.getBuildings().remove(constructionBuilding);
                DatabasePlayer.getInstance().setExp(uuid, player.addXp(building.getExp()));
            } else
                DatabasePlayer.getInstance().addExp(uuid, building.getExp());

            Schematics.removeBuilding(building.getCoordinate(), building.getBuilding().getSize());

            building.setLevel(building.getLevel() + 1);

            Schematics.createBuilding(building);

            if (building.getLevel() == 0)
                DatabaseBuildings.getInstance().buildBuilding(uuid, building);
            else
                DatabaseBuildings.getInstance().upgradeBuilding(uuid, building, building.getLevel());

            DatabaseBuildings.getInstance().finishedBuilding(building.getCoordinate());
        });
    }

    /**
     * Removes a building from the world.
     * @param building GeneralBuilding - the building.
     * @since 0.0.1
     */
    public void removeBuilding(GeneralBuilding building) {
        if (PlayerManager.getInstance().isCached(building.getUuid())) {
            CoCPlayer coCPlayer = PlayerManager.getInstance().getPlayer(building.getUuid());
            coCPlayer.getBuildings().remove(building);
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
                    coCPlayer.addResource(building.getBuilding().getResourceType(), building.getUpgradeCost());
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> {
            Schematics.removeBuilding(building.getCoordinate(), building.getBuilding().getSize());
            DatabaseBuildings.getInstance().deleteBuilding(building);
        });
    }

    /**
     * Get the building instance from the interface class.
     * @see IBuilding#getBuildingClass()
     * @param building {@link IBuilding} - the interface building class.
     * @param location Location - the location the building stands.
     * @param level int - the level the building has.
     * @return {@link GeneralBuilding} - the building instance.
     * @since 0.0.1
     */
    public GeneralBuilding getBuildingInstance(UUID uuid, IBuilding building, Location location, byte rotation, int level) {
        try {
            return building.getBuildingClass()
                    .getConstructor(UUID.class, IBuilding.class, Location.class, byte.class, int.class)
                    .newInstance(uuid, building, location, rotation, level);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the building instance from the interface class.
     * @see IBuilding#getBuildingClass()
     * @param building {@link IBuilding} - the interface building class.
     * @param location Location - the location the building stands.
     * @param level int - the level the building has.
     * @param amount double - the amount the building contains.
     * @return {@link GeneralBuilding} - the building instance.
     * @since 0.0.1
     */
    public GeneralBuilding getBuildingInstance(UUID uuid, IBuilding building, Location location, byte rotation, int level, double amount) {
        try {
            return building.getBuildingClass()
                    .getConstructor(UUID.class, IBuilding.class, Location.class, byte.class, int.class, double.class)
                    .newInstance(uuid, building, location, rotation, level, amount);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the building instance from the interface class.
     * @see IBuilding#getBuildingClass()
     * @param building {@link IBuilding} - the interface building class.
     * @param location Location - the location the building stands.
     * @param level int - the level the building has.
     * @param troopsAmount ConcurrentMap<ITroop, Integer> - the troops amount.
     * @return {@link GeneralBuilding} - the building instance.
     * @since 0.0.1
     */
    public GeneralBuilding getBuildingInstance(UUID uuid, IBuilding building, Location location, byte rotation, int level, ConcurrentMap<ITroop, Integer> troopsAmount) {
        try {
            return building.getBuildingClass()
                    .getConstructor(UUID.class, IBuilding.class, Location.class, byte.class, int.class, ConcurrentMap.class)
                    .newInstance(uuid, building, location, rotation, level, troopsAmount);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
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
     * Fill resource from all resource gatherer of the type.
     * @see BuildingsManager#fillResource(ResourceGatherBuilding)
     * @param type {@link ResourceTypes} - the type to fill up.
     * @param player CoCPlayer - the player that fills up.
     * @since 0.0.1
     */
    public void fillResource(ResourceTypes type, CoCPlayer player) {
        player.getBuildings().stream().filter(b -> b instanceof ResourceGatherBuilding).filter(b -> ((ResourceGatherBuilding) b).getContainingResourceType() == type).forEach(b -> fillResource((ResourceGatherBuilding) b));
    }

    /**
     * Fills up the resource from a {@link ResourceGatherBuilding} to the players bank.
     * @see CoCPlayer#addResource(ResourceTypes, int)
     * @param building {@link ResourceGatherBuilding} - the building to drain the resource from.
     * @since 0.0.1
     */
    public void fillResource(ResourceGatherBuilding building) {
        CoCPlayer cocPlayer = PlayerManager.getInstance().getPlayer(building.getUuid());

        int toAdd = Math.min(cocPlayer.getMaxResourceContainable(building.getContainingResourceType()) - cocPlayer.getResource(building.getContainingResourceType()), (int)building.getAmount());
        if (toAdd == 0) {
            APIPlayer owner = cocPlayer.getOwner();
            if(owner != null)
                owner.sendActionbar(new ActionbarMessage(TranslationKeys.COC_PLAYER_NO_RESOURCE_TANKS));
            return;
        }
        building.setAmount(building.getAmount() - toAdd);
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> DatabaseBuildings.getInstance().updateData(building.getCoordinate(), (int) building.getAmount()));
        cocPlayer.addResource(building.getContainingResourceType(), toAdd);
    }

    public void enterMovingMode(APIPlayer apiPlayer, GeneralBuilding generalBuilding) {
        Player player = apiPlayer.getPlayer();
        PlayerManager.getInstance().getPlayer(apiPlayer.getUniqueId()).setBuildingMode(player.getLocation(), generalBuilding, generalBuilding.getRotation());
        Language lang = apiPlayer.getLanguage();
        player.getInventory().setItem(0, new ItemBuilder(Material.PISTON)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_LORE).split(";")).craft());
        player.getInventory().setItem(1, new ItemBuilder(Material.STICK)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_LORE).split(";")).craft());
        player.getInventory().setItem(4, new ItemBuilder(Material.BARRIER)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_CANCEL_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_CANCEL_LORE).split(";")).craft());
        player.getInventory().setHeldItemSlot(0);
    }

    public void moveBuilding(Object[] buildingMode) {
        GeneralBuilding building = (GeneralBuilding) buildingMode[1];
        int oldRotation = building.getRotation();
        Location oldLocation = building.getCoordinate().clone();
        if (building.getRotation() != (byte) buildingMode[2])
            building.setRotation((byte) buildingMode[2]);

        building.setCoordinate(BuildingLocationUtil.getCoordinate(building.getBuilding().getSize(), building.getRotation(), (Location) buildingMode[0]));
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> {
            Schematics.removeBuilding(oldLocation, building.getBuilding().getSize());
            Schematics.createBuilding(building);
            DatabaseBuildings.getInstance().moveBuilding(building, oldLocation, oldRotation);
        });
    }

    public void quitEditorMode(CoCPlayer coCPlayer) {
        Player player = coCPlayer.getOwner().getPlayer();

        coCPlayer.setBuildingMode(null, null, null);
        player.getInventory().setItem(0, new ItemStack(Material.AIR));
        player.getInventory().setItem(1, new ItemStack(Material.AIR));
        player.getInventory().setItem(4, new ItemStack(Material.AIR));
    }

    public void enterCreationMode(CoCPlayer coCPlayer, IBuilding building) {
        APIPlayer apiPlayer = coCPlayer.getOwner();
        Player player = apiPlayer.getPlayer();
        coCPlayer.setBuildingMode(player.getLocation(), building, (byte) 0);
        Language lang = apiPlayer.getLanguage();
        player.getInventory().setItem(0, new ItemBuilder(Material.NETHER_STAR)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_LORE).split(";")).craft());
        player.getInventory().setItem(1, new ItemBuilder(Material.STICK)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_ROTATE_LORE).split(";")).craft());
        player.getInventory().setItem(4, new ItemBuilder(Material.BARRIER)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_LORE).split(";")).craft());
        player.getInventory().setHeldItemSlot(0);
    }

}