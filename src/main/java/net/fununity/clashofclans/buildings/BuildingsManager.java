package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.list.*;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.listener.PlayerMoveListener;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.DatabasePlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    private final ConcurrentMap<GeneralBuilding, Integer> buildingTime;
    private final List<IBuilding> allBuildings;
    private final Map<UUID, GeneralBuilding> buildingMoves;
    private final Map<UUID, IBuilding> createBuilding;

    /**
     * Instantiates the class.
     * Loads all Building enums to the cache.
     * @since 0.0.1
     */
    private BuildingsManager() {
        this.buildingMoves = new HashMap<>();
        this.createBuilding = new HashMap<>();
        this.buildingTime = new ConcurrentHashMap<>();
        this.allBuildings = new ArrayList<>();
        this.allBuildings.addAll(Arrays.asList(Buildings.values()));
        this.allBuildings.addAll(Arrays.asList(ResourceContainerBuildings.values()));
        this.allBuildings.addAll(Arrays.asList(ResourceGathererBuildings.values()));
        this.allBuildings.addAll(Arrays.asList(TroopBuildings.values()));
        this.allBuildings.addAll(Arrays.asList(TroopCreationBuildings.values()));
    }

    /**
     * Creates a new island for the player.
     * @param player Player - the player to create the island.
     * @since 0.0.1
     */
    public CoCPlayer createNewIsland(Player player) {
        UUID uuid = player.getUniqueId();
        Location highestCoordinate = DatabasePlayer.getInstance().getHighestCoordinate();
        DatabasePlayer.getInstance().createUser(uuid, highestCoordinate);
        Map<ResourceTypes, Integer> resources = new EnumMap<>(ResourceTypes.class);
        for (ResourceTypes resource : ResourceTypes.values())
            resources.put(resource, 0);

        CoCPlayer coCPlayer = new CoCPlayer(uuid, highestCoordinate, resources, 0);

        Schematics.createBuilding(highestCoordinate);

        createBuilding(getBuildingInstance(uuid, Buildings.TOWN_HALL, highestCoordinate.clone().add(50, 0, 50), 1));
        createBuilding(getBuildingInstance(uuid, ResourceGathererBuildings.GOLD_MINER, highestCoordinate.clone().add(30, 0, 50), 1));
        createBuilding(getBuildingInstance(uuid, ResourceContainerBuildings.GOLD_STOCK, highestCoordinate.clone().add(50, 0, 30), 1));

        Bukkit.getScheduler().runTask(ClashOfClans.getInstance(), () -> player.teleport(highestCoordinate.clone().add(20, 2, 20)));
        return coCPlayer;
    }

    public boolean build(CoCPlayer player, IBuilding building, Location location) {
        int cost = building.getBuildingLevelData()[0].getUpgradeCost();
        if (player.getResource(building.getResourceType()) < cost)
            return false;

        GeneralBuilding generalBuilding = getBuildingInstance(player.getUniqueId(), building, location, 0);
        if (generalBuilding == null)
            return false;

        player.removeResource(building.getResourceType(), cost);
        buildingTime.put(generalBuilding, building.getBuildingLevelData()[0].getBuildTime());
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () ->
                Schematics.createConstruction(building.getSize(), location));
        return true;
    }

    public boolean upgrade(UUID uuid, GeneralBuilding building) {
        return upgrade(PlayerManager.getInstance().getPlayer(uuid), building);
    }

    public boolean upgrade(CoCPlayer player, GeneralBuilding building) {
        int cost = building.getUpgradeCost();
        if (cost == -1 || player.getResource(building.getBuilding().getResourceType()) < cost)
            return false;

        player.removeResource(building.getBuilding().getResourceType(), cost);
        buildingTime.put(building, building.getBuildTime());
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> {
            Schematics.removeBuilding(building.getCoordinate(), building.getBuilding().getSize());
            Schematics.createConstruction(building.getBuilding().getSize(), building.getCoordinate());
        });
        return true;
    }

    public void finishedBuilding(GeneralBuilding building) {
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> {
            Schematics.removeBuilding(building.getCoordinate(), building.getBuilding().getSize());
            if (building.getLevel() == 0) {
                createBuilding(building);
            } else
                upgradeBuilding(building);
        });
    }

    /**
     * Creates the building.
     * @param building {@link GeneralBuilding} - the building to create.
     * @since 0.0.1
     */
    private void createBuilding(GeneralBuilding building) {
        building.setLevel(1);
        PlayerManager.getInstance().getPlayer(building.getUuid()).getBuildings().add(building);
        Schematics.createBuilding(building);
        DatabaseBuildings.getInstance().buildBuilding(building.getUuid(), building);
    }

    /**
     * Upgrades the building.
     * @param building {@link GeneralBuilding} - the building to upgrade.
     * @since 0.0.1
     */
    private void upgradeBuilding(GeneralBuilding building) {
        building.setLevel(building.getLevel() + 1);
        Schematics.createBuilding(building);
        DatabaseBuildings.getInstance().upgradeBuilding(building.getUuid(), building, building.getLevel());
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
    public GeneralBuilding getBuildingInstance(UUID uuid, IBuilding building, Location location, int level) {
        try {
            return building.getBuildingClass()
                    .getConstructor(UUID.class, IBuilding.class, Location.class, int.class)
                    .newInstance(uuid, building, location, level);
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
     * Fills up the resource from a {@link ResourceGatherBuilding} to the players bank.
     * @see CoCPlayer#addResource(ResourceTypes, int)
     * @param building {@link ResourceGatherBuilding} - the building to drain the resource from.
     * @since 0.0.1
     */
    public void fillResource(ResourceGatherBuilding building) {
        CoCPlayer cocPlayer = PlayerManager.getInstance().getPlayer(building.getUuid());

        int toAdd = Math.min(cocPlayer.getMaxResourceToHave(building.getResourceContaining()) - cocPlayer.getResource(building.getResourceContaining()), (int)building.getAmount());
        building.setAmount(building.getAmount() - toAdd);
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () -> DatabaseBuildings.getInstance().updateData(building.getCoordinate(), (int) building.getAmount()));
        cocPlayer.addResource(building.getResourceContaining(), toAdd);
    }

    public ConcurrentMap<GeneralBuilding, Integer> getBuildingTime() {
        return buildingTime;
    }

    public Map<UUID, GeneralBuilding> getBuildingMoves() {
        return new HashMap<>(buildingMoves);
    }

    public void enterMovingMode(APIPlayer apiPlayer, GeneralBuilding generalBuilding) {
        this.buildingMoves.put(apiPlayer.getUniqueId(), generalBuilding);
        Language lang = apiPlayer.getLanguage();
        Player player = apiPlayer.getPlayer();
        player.getInventory().setItem(0, new ItemBuilder(Material.PISTON)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_MOVE_LORE).split(";")).craft());
        player.getInventory().setItem(4, new ItemBuilder(Material.BARRIER)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_CANCEL_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_MOVE_ITEM_CANCEL_LORE).split(";")).craft());
        player.getInventory().setHeldItemSlot(0);
    }

    public void moveBuilding(GeneralBuilding building, Location newLocation) {
        Location oldLocation = building.getCoordinate().clone();
        building.setCoordinate(newLocation);
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), ()->{
            Schematics.removeBuilding(oldLocation, building.getBuilding().getSize());
            Schematics.createBuilding(building);
            DatabaseBuildings.getInstance().moveBuilding(building, oldLocation);
        });
    }

    public void quitEditorMode(Player player) {
        GeneralBuilding building = buildingMoves.getOrDefault(player.getUniqueId(), null);
        IBuilding iBuilding = createBuilding.getOrDefault(player.getUniqueId(), null);
        int[] size;
        if (building != null) {
            buildingMoves.remove(player.getUniqueId());
            size = building.getBuilding().getSize();
        } else if (iBuilding != null) {
            createBuilding.remove(player.getUniqueId());
            size = iBuilding.getSize();
        } else
            return;
        PlayerMoveListener.getInstance().removeBlocks(player, size);
        player.getInventory().setItem(0, new ItemStack(Material.AIR));
        player.getInventory().setItem(4, new ItemStack(Material.AIR));
    }

    public void createBuilding(APIPlayer apiPlayer, IBuilding building) {
        this.createBuilding.put(apiPlayer.getUniqueId(), building);
        Language lang = apiPlayer.getLanguage();
        Player player = apiPlayer.getPlayer();
        player.getInventory().setItem(0, new ItemBuilder(Material.NETHER_STAR)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CREATE_LORE).split(";")).craft());
        player.getInventory().setItem(4, new ItemBuilder(Material.BARRIER)
                .setName(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_CONSTRUCTION_CREATE_ITEM_CANCEL_LORE).split(";")).craft());
        player.getInventory().setHeldItemSlot(0);
    }

    public Map<UUID, IBuilding> getCreateBuilding() {
        return new HashMap<>(createBuilding);
    }

}
