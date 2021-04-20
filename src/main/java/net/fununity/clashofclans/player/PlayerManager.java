package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.ResourceTickHandler;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.TroopsTickHandler;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.DatabaseBuildings;
import net.fununity.clashofclans.buildings.classes.*;
import net.fununity.clashofclans.buildings.interfaces.ITroopBuilding;
import net.fununity.clashofclans.troops.Troops;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class PlayerManager {

    private static final int Y_COORD = 50;
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
        CoCPlayer coCPlayer = DatabasePlayer.getInstance().contains(uuid) ? getPlayer(uuid) : BuildingsManager.getInstance().createNewIsland(player.getPlayer());
        this.playersMap.put(uuid, coCPlayer);
        ResourceTickHandler.removeFromCache(coCPlayer);
        TroopsTickHandler.removeFromCache(coCPlayer);
        Bukkit.getScheduler().runTask(ClashOfClans.getInstance(), () -> {
            player.getPlayer().setAllowFlight(true);
            coCPlayer.getBuildings().stream().filter(b -> b instanceof ResourceContainerBuilding).forEach(b -> player.showHologram(((ResourceContainerBuilding)b).getHologram()));
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
        playersMap.remove(uuid);
    }

    /**
     * Forces an new open to the building inventory.
     * @param building {@link GeneralBuilding} - the building.
     * @since 0.0.1
     */
    public void forceUpdateInventory(GeneralBuilding building) {
        for (APIPlayer onlinePlayer : FunUnityAPI.getInstance().getPlayerHandler().getOnlinePlayers()) {
            if (onlinePlayer.hasCustomData("openInv")) {
                CustomInventory menu = (CustomInventory) onlinePlayer.getCustomData("openInv");
                if (menu.getSpecialHolder() != null && menu.getSpecialHolder().equals(building.getId() + "-" + building.getCoordinate().toString())) {
                    onlinePlayer.getPlayer().closeInventory();
                    building.getInventory(onlinePlayer.getLanguage()).open(onlinePlayer);
                }
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
        CoCPlayer coCPlayer;
        try (ResultSet data = DatabasePlayer.getInstance().getPlayerData(uuid)) {
            if (data != null && data.next()) {

                int playerX = data.getInt("x");
                int playerZ = data.getInt("z");
                int xp = data.getInt("xp");

                Map<ResourceTypes, Integer> resourceTypes = new EnumMap<>(ResourceTypes.class);
                for (ResourceTypes type : ResourceTypes.values())
                    resourceTypes.put(type, 0);

                coCPlayer = new CoCPlayer(uuid, new Location(ClashOfClans.getInstance().getPlayWorld(), playerX, Y_COORD, playerZ), resourceTypes, xp);
            } else
                return null;
        } catch (SQLException exception) {
            ClashOfClans.getInstance().getLogger().warning("dx:" + exception.getMessage());
            return null;
        }

        try (ResultSet set = DatabaseBuildings.getInstance().getBuildings(uuid)) {
            while (set != null && set.next()) {
                coCPlayer.getBuildings().add(BuildingsManager.getInstance()
                        .getBuildingInstance(uuid, BuildingsManager.getInstance().getBuildingById(set.getString("buildingID")),
                                new Location(ClashOfClans.getInstance().getPlayWorld(), set.getInt("x"), Y_COORD, set.getInt("z")),
                                set.getInt("level")));
            }
        } catch (SQLException exception) {
            ClashOfClans.getInstance().getLogger().warning("b: " + exception.getMessage());
        }

        try (ResultSet dataBuildingsSet = DatabaseBuildings.getInstance().getContainerDataBuildings(uuid)) {
            while (dataBuildingsSet != null && dataBuildingsSet.next()) {
                int x = dataBuildingsSet.getInt("x");
                int z = dataBuildingsSet.getInt("z");
                ContainerBuilding containerBuilding = (ContainerBuilding) coCPlayer.getBuildings().stream().filter(b -> b.getCoordinate().getBlockX() == x && b.getCoordinate().getBlockZ() == z).findFirst().orElse(null);
                if (containerBuilding != null) {
                    containerBuilding.setAmount(dataBuildingsSet.getInt("amount"));
                }
            }
            coCPlayer.updateResources();
        } catch (SQLException exception) {
            ClashOfClans.getInstance().getLogger().warning("c: " + exception.getMessage());
        }

        try (ResultSet dataTroopsSet = DatabaseBuildings.getInstance().getTroopsDataBuildings(uuid)) {
            while (dataTroopsSet != null && dataTroopsSet.next()) {
                int x = dataTroopsSet.getInt("x");
                int z = dataTroopsSet.getInt("z");
                TroopsBuilding containerBuilding = (TroopsBuilding) coCPlayer.getBuildings().stream().filter(b -> b.getCoordinate().getBlockX() == x && b.getCoordinate().getBlockZ() == z).findFirst().orElse(null);
                if (containerBuilding != null) {
                    for (Troops troop : Troops.values()) {
                        containerBuilding.getTroopAmount().put(troop, dataTroopsSet.getInt(troop.name().toLowerCase()));
                    }
                }
            }
        } catch (SQLException exception) {
            ClashOfClans.getInstance().getLogger().warning("t:" + exception.getMessage());
        }
        return coCPlayer;
    }

        /**
         * Get copied list of players.
         * @return Map<UUID, CoCPlayer> - the players map.
         * @since 0.0.1
         */
        public Map<UUID, CoCPlayer> getPlayers() {
            return new HashMap<>(playersMap);
        }

        /**
         * Returns a list of all {@link TroopsBuilding} the player has.
         * @param uuid UUID - uuid of player.
         * @return List<TroopsBuilding> - A list of the buildings the player has.
         * @since 0.0.1
         */
        public List<TroopsBuilding> getTroopBuildings (UUID uuid){
            if (playersMap.containsKey(uuid)) {
                return playersMap.get(uuid).getBuildings().stream().filter(b -> b instanceof TroopsBuilding).map(list -> (TroopsBuilding) list).collect(Collectors.toList());
            }
            List<TroopsBuilding> buildings = new ArrayList<>();
            try (ResultSet dataTroopsSet = DatabaseBuildings.getInstance().getTroopsDataBuildings(uuid)) {
                while (dataTroopsSet != null && dataTroopsSet.next()) {
                    int x = dataTroopsSet.getInt("x");
                    int z = dataTroopsSet.getInt("z");
                    TroopsBuilding troopsBuilding = new TroopsBuilding(uuid, (ITroopBuilding) BuildingsManager.getInstance().getBuildingById(dataTroopsSet.getString("buildingID")),
                            new Location(ClashOfClans.getInstance().getPlayWorld(), x, Y_COORD, z),
                            dataTroopsSet.getInt("level"));
                    for (Troops troop : Troops.values()) {
                        troopsBuilding.getTroopAmount().put(troop, dataTroopsSet.getInt(troop.name().toLowerCase()));
                    }
                    buildings.add(troopsBuilding);
                }
            } catch (SQLException exception) {
                ClashOfClans.getInstance().getLogger().warning(exception.getMessage());
            }
            return buildings;
        }
    }
