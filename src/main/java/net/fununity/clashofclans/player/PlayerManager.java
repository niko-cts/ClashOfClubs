package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.DatabaseBuildings;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.buildings.classes.ConstructionBuilding;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.classes.TroopsCreateBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuildingWithHologram;
import net.fununity.clashofclans.buildings.interfaces.IResourceContainerBuilding;
import net.fununity.clashofclans.buildings.interfaces.ITroopBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.tickhandler.BuildingTickHandler;
import net.fununity.clashofclans.tickhandler.ResourceTickHandler;
import net.fununity.clashofclans.tickhandler.TroopsTickHandler;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.troops.Troops;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class PlayerManager {

    private static PlayerManager instance;

    /**
     * Get this singleton class instance.
     * @return {@link PlayerManager} - the singleton instance class.
     * @since 0.0.1
     */
    public static PlayerManager getInstance() {
        if(instance == null)
            instance = new PlayerManager();
        return instance;
    }

    private final ConcurrentMap<UUID, CoCPlayer> playersMap;

    /**
     * Instantiates the class.
     * @since 0.0.1
     */
    private PlayerManager() {
        this.playersMap = new ConcurrentHashMap<>();
    }

    /**
     * Loading the player.
     * @param player Player - the player.
     * @since 0.0.1
     */
    public CoCPlayer playerJoins(APIPlayer player) {
        UUID uuid = player.getUniqueId();
        CoCPlayer coCPlayer;

        boolean contains = DatabasePlayer.getInstance().contains(uuid);
        if (contains) {
            coCPlayer = getPlayer(uuid);
            ResourceTickHandler.removeFromCache(coCPlayer);
            TroopsTickHandler.removeFromCache(coCPlayer);
            BuildingTickHandler.removeFromCache(coCPlayer);
            if (!ClashOfClubs.getInstance().getLoadedBases().contains(uuid)) {
                Schematics.createPlayerBase(coCPlayer.getLocation());
                coCPlayer.getBuildings().forEach(Schematics::createBuilding);
            }
        } else {
            player.sendMessage(MessagePrefix.INFO, TranslationKeys.COC_PLAYER_LOADING_BASE);
            coCPlayer = BuildingsManager.getInstance().createNewIsland(uuid);
        }
        coCPlayer.updateResources();
        this.playersMap.put(uuid, coCPlayer);
        ClashOfClubs.getInstance().getLoadedBases().add(uuid);
        Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> {
            coCPlayer.visit(player, !contains);
            coCPlayer.getBuildings().stream().filter(b -> b instanceof IBuildingWithHologram).forEach(b -> ((IBuildingWithHologram) b).updateHologram());
            ScoreboardMenu.show(coCPlayer);
        });
        return coCPlayer;
    }

    /**
     * Will be called when a player left.
     * Removes the player and caches the tick buildings.
     * @param uuid UUID - uuid of player
     * @since 0.0.1
     */
    public void playerLeft(UUID uuid) {
        if (!this.playersMap.containsKey(uuid)) return;
        CoCPlayer coCPlayer = this.playersMap.get(uuid);
        ResourceTickHandler.addToCache(coCPlayer.getBuildings().stream().filter(b -> b instanceof ResourceGatherBuilding).map(list -> (ResourceGatherBuilding) list).collect(Collectors.toList()));
        TroopsTickHandler.addToCache(coCPlayer.getBuildings().stream().filter(b -> b instanceof TroopsCreateBuilding).map(list -> (TroopsCreateBuilding) list).collect(Collectors.toList()));
        BuildingTickHandler.addToCache(coCPlayer.getBuildings().stream().filter(b -> b instanceof ConstructionBuilding).map(list -> (ConstructionBuilding) list).collect(Collectors.toList()));
        playersMap.remove(uuid);
    }

    /**
     * Forces an new open to the building inventory.
     * @param building {@link GeneralBuilding} - the building.
     * @since 0.0.1
     */
    public void forceUpdateInventory(GeneralBuilding building) {
        APIPlayer onlinePlayer = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(building.getUuid());
        if (onlinePlayer == null) return;

        if (onlinePlayer.hasCustomData("openInv")) {
            CustomInventory menu = (CustomInventory) onlinePlayer.getCustomData("openInv");
            if (menu.getSpecialHolder() != null && menu.getSpecialHolder().equals(building.getId() + "-" + building.getCoordinate().toString())) {
                onlinePlayer.getPlayer().closeInventory();
                building.getInventory(onlinePlayer.getLanguage()).open(onlinePlayer);
            }
        }
    }

    /**
     * Get the coc player instance.
     * @param uuid UUID - uuid of player.
     * @return {@link CoCPlayer} - the coc player instance.
     * @since 0.0.1
     */
    public CoCPlayer getPlayer(UUID uuid) {
        if (this.playersMap.containsKey(uuid))
            return this.playersMap.get(uuid);

        CoCPlayer coCPlayer = new CoCPlayer(getDataPlayer(uuid));

        try (ResultSet set = DatabaseBuildings.getInstance().getBuildings(uuid)) {
            while (set != null && set.next()) {
                IBuilding buildingID = BuildingsManager.getInstance().getBuildingById(set.getString("buildingID"));
                Location location = new Location(ClashOfClubs.getInstance().getPlayWorld(), set.getInt("x"), ClashOfClubs.getBaseYCoordinate(), set.getInt("z"));
                if (buildingID instanceof IResourceContainerBuilding) {
                    coCPlayer.getBuildings().add(BuildingsManager.getInstance()
                            .getBuildingInstance(uuid, buildingID, location, set.getByte("rotation"), set.getInt("level"), set.getInt("amount")));
                } else if (buildingID instanceof ITroopBuilding) {
                    ConcurrentMap<ITroop, Integer> troops = new ConcurrentHashMap<>();
                    for (Troops troop : Troops.values())
                        troops.put(troop, set.getInt(troop.name().toLowerCase()));
                    coCPlayer.getBuildings().add(BuildingsManager.getInstance().getBuildingInstance(uuid, buildingID, location, set.getByte("rotation"), set.getInt("level"), troops));
                } else {
                    coCPlayer.getBuildings().add(BuildingsManager.getInstance()
                            .getBuildingInstance(uuid, buildingID, location, set.getByte("rotation"), set.getInt("level")));
                }
            }
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }

        try (ResultSet construction = DatabaseBuildings.getInstance().getConstructionBuildings(uuid)) {
            while (construction != null && construction.next()) {
                int x = construction.getInt("x");
                int z = construction.getInt("z");
                OffsetDateTime date = OffsetDateTime.parse(construction.getString("date"));
                GeneralBuilding generalBuilding = coCPlayer.getBuildings().stream().filter(b -> b.getCoordinate().getBlockX() == x && b.getCoordinate().getBlockZ() == z).findFirst().orElse(null);
                if (generalBuilding != null) {
                    ConstructionBuilding constructionBuilding = new ConstructionBuilding(generalBuilding, (int) ChronoUnit.SECONDS.between(OffsetDateTime.now(), date));
                    coCPlayer.getBuildings().remove(generalBuilding);
                    coCPlayer.getBuildings().add(constructionBuilding);
                }
            }
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return coCPlayer;
    }

    /**
     * Get the data player instance.
     * @param uuid UUID - uuid of player.
     * @return {@link CoCDataPlayer} - the data of a player.
     * @since 0.0.1
     */
    public CoCDataPlayer getDataPlayer(UUID uuid) {
        if (playersMap.containsKey(uuid))
            return playersMap.get(uuid);

        try (ResultSet data = DatabasePlayer.getInstance().getPlayerData(uuid)) {
            if (data != null && data.next()) {

                int playerX = data.getInt("x");
                int playerZ = data.getInt("z");
                int xp = data.getInt("xp");
                int elo = data.getInt("elo");

                Map<ResourceTypes, Integer> resourceTypes = new EnumMap<>(ResourceTypes.class);
                for (ResourceTypes type : ResourceTypes.values())
                    resourceTypes.put(type, data.getInt(type.name().toLowerCase()));

                return new CoCDataPlayer(uuid, new Location(ClashOfClubs.getInstance().getPlayWorld(), playerX, ClashOfClubs.getBaseYCoordinate(), playerZ), resourceTypes, xp, elo);
            }
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return null;
    }

    public boolean isCached(UUID uuid) {
        return playersMap.containsKey(uuid);
    }

    /**
     * Get copied list of players.
     *
     * @return Map<UUID, CoCPlayer> - the players map.
     * @since 0.0.1
     */
    public Map<UUID, CoCPlayer> getPlayers() {
        return new HashMap<>(playersMap);
    }

}
