package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.instances.ConstructionBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsCreateBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IResourceContainerBuilding;
import net.fununity.clashofclans.buildings.interfaces.ITroopBuilding;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.database.DatabasePlayer;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.troops.Troops;
import net.fununity.clashofclans.util.HotbarItems;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.LocationUtil;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerManager {

    private final ConcurrentMap<UUID, CoCPlayer> playersMap;

    /**
     * Instantiates the class.
     *
     * @since 0.0.1
     */
    public PlayerManager() {
        this.playersMap = new ConcurrentHashMap<>();
    }

    /**
     * Loading the player.
     *
     * @param player Player - the player.
     * @since 0.0.1
     */
    public void playerJoins(APIPlayer player) {
        if (DatabasePlayer.getInstance().contains(player.getUniqueId())) {
            player.getTitleSender().sendTitle(TranslationKeys.COC_PLAYER_LOADING_PLAYER_DATA_TITLE, 5 * 20);
            player.getTitleSender().sendSubtitle(TranslationKeys.COC_PLAYER_LOADING_PLAYER_DATA_SUBTITLE, 5 * 20);
            CoCPlayer coCPlayer = loadPlayer(player.getUniqueId());

            if (coCPlayer == null) {
                player.sendRawMessage(ChatColor.RED + "CoCPlayer could not have been loaded. Please speak with an administrator.");
                throw new IllegalStateException("CoCPlayer could not have been loaded.");
            }

            playersMap.put(player.getUniqueId(), coCPlayer);

            player.getTitleSender().sendTitle(TranslationKeys.COC_PLAYER_LOADING_RESOURCES_TITLE, 5 * 20);
            player.getTitleSender().sendSubtitle(TranslationKeys.COC_PLAYER_LOADING_RESOURCES_SUBTITLE, 5 * 20);
            double secondsGone = (System.currentTimeMillis() - coCPlayer.getLastJoinMillis()) / 1000.0;
            for (ResourceTypes types : ResourceTypes.values())
                coCPlayer.getResourceGatherBuildings(types).forEach(b -> b.addAmountPlayerWasGone(secondsGone));

            player.getTitleSender().sendTitle(TranslationKeys.COC_PLAYER_LOADING_TROOPS_TITLE, 5 * 20);
            player.getTitleSender().sendSubtitle(TranslationKeys.COC_PLAYER_LOADING_TROOPS_SUBTITLE, 5 * 20);
            coCPlayer.getTroopsCreateBuildings().forEach(b -> b.checkQueuePlayerWasGone(secondsGone));

            player.clearActionbar();
            player.getTitleSender().sendTitle(TranslationKeys.COC_PLAYER_LOADING_FINISHED_TITLE, 20);
            player.getTitleSender().sendSubtitle(TranslationKeys.COC_PLAYER_LOADING_FINISHED_SUBTITLE, 20);

            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> {
                coCPlayer.visit(player, true);
                ScoreboardMenu.show(coCPlayer);
                TutorialManager.getInstance().checkIfTutorialNeeded(coCPlayer);
            });
        } else {
            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> player.getPlayer().setGameMode(GameMode.SPECTATOR));
            playersMap.put(player.getUniqueId(), BuildingsManager.getInstance().createNewIsland(player));
        }
    }

    /**
     * Will be called when a player left.
     * Removes the player and caches the tick buildings.
     *
     * @param uuid UUID - uuid of player
     * @since 0.0.1
     */
    public void playerLeft(UUID uuid) {
        if (!this.playersMap.containsKey(uuid)) return;
        CoCPlayer coCPlayer = this.playersMap.get(uuid);
        playersMap.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            DatabasePlayer.getInstance().updatePlayer(coCPlayer);
            DatabaseBuildings.getInstance().updateBuildings(coCPlayer);
        });
    }

    /**
     * Forces a new open to the building inventory.
     *
     * @param building {@link GeneralBuilding} - the building.
     * @since 0.0.1
     */
    public void forceUpdateInventory(GeneralBuilding building) {
        APIPlayer onlinePlayer = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(building.getOwnerUUID());
        if (onlinePlayer == null) return;

        if (onlinePlayer.hasCustomData("openInv")) {
            CustomInventory menu = (CustomInventory) onlinePlayer.getCustomData("openInv");
            if (menu.getSpecialHolder() != null && menu.getSpecialHolder().equals(building.getCoordinate().toString())) {
                //onlinePlayer.getPlayer().closeInventory();
                building.getInventory(onlinePlayer.getLanguage()).open(onlinePlayer);
            }
        }
    }

    /**
     * Get the coc player instance.
     * @param player APIPlayer - the player.
     * @return {@link CoCPlayer} - the coc player instance.
     * @since 0.0.1
     */
    public CoCPlayer getPlayer(APIPlayer player) {
        return getPlayer(player.getUniqueId());
    }

    /**
     * Get the coc player instance.
     * @param player Player - the player.
     * @return {@link CoCPlayer} - the coc player instance.
     * @since 0.0.1
     */
    public CoCPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    /**
     * Get the coc player instance.
     *
     * @param uuid UUID - uuid of player.
     * @return {@link CoCPlayer} - the coc player instance.
     * @since 0.0.1
     */
    public CoCPlayer getPlayer(UUID uuid) {
        return this.playersMap.getOrDefault(uuid, null);
    }

    /**
     * Loads the player out of the database.
     *
     * @param uuid UUID - the player who joins.
     * @return CoCPlayer - loaded coc player.
     */
    public CoCPlayer loadPlayer(UUID uuid) {
        try (ResultSet data = DatabasePlayer.getInstance().getPlayerData(uuid)) {
            if (data != null && data.next()) {

                int playerX = data.getInt("x");
                int playerZ = data.getInt("z");
                int xp = data.getInt("xp");
                int elo = data.getInt("elo");
                int gems = data.getInt("gems");
                long lastJoin = data.getLong("last_login");
                String lastServer = data.getString("last_server");

                try (ResultSet set = DatabaseBuildings.getInstance().getBuildings(uuid)) {
                    List<GeneralBuilding> buildings = new ArrayList<>();

                    while (set != null && set.next()) {
                        UUID buildingUUID = UUID.fromString(set.getString(DatabaseBuildings.TABLE + ".building_uuid"));
                        IBuilding buildingID = BuildingsManager.getInstance().getBuildingById(set.getString("buildingID"));
                        byte rotation = set.getByte("rotation");
                        int level = set.getInt("level");
                        Location location = new Location(ClashOfClubs.getInstance().getWorld(), set.getInt("x"), ClashOfClubs.getBaseYCoordinate(), set.getInt("z"));

                        GeneralBuilding building;

                        if (buildingID instanceof IResourceContainerBuilding)
                            building = BuildingsManager.getInstance().getBuildingInstance(uuid, buildingUUID, buildingID, location, rotation, level, set.getDouble("amount"));
                        else if (buildingID instanceof ITroopBuilding) {
                            ConcurrentHashMap<ITroop, Integer> troops = new ConcurrentHashMap<>();
                            for (Troops troop : Troops.values())
                                troops.put(troop, set.getInt(troop.name().toLowerCase()));
                            building = BuildingsManager.getInstance().getBuildingInstance(uuid, buildingUUID, buildingID, location, rotation, level, troops);

                            if (building instanceof TroopsCreateBuilding)
                                ((TroopsCreateBuilding) building).insertQueue(set.getString("queue"));
                        } else
                            building = BuildingsManager.getInstance().getBuildingInstance(uuid, buildingUUID, buildingID, location, rotation, level);

                        if (set.getLong("finish_time") != 0)
                            building = new ConstructionBuilding(building, set.getLong("finish_time"));

                        buildings.add(building);
                    }

                    return new CoCPlayer(uuid, new Location(ClashOfClubs.getInstance().getWorld(), playerX, ClashOfClubs.getBaseYCoordinate(), playerZ), xp, elo, gems, lastServer, lastJoin, buildings);

                } catch (SQLException exception) {
                    ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
                }
            }
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return null;
    }

    public void giveDefaultItems(CoCPlayer coCPlayer) {
        giveDefaultItems(coCPlayer, coCPlayer.getOwner());
    }

    /**
     * Clears the inventory and gets the default items for the base.
     * @param coCPlayer CoCPlayer - the cocplayer
     * @param apiPlayer APIPlayer - the apiplayer
     * @since 0.0.2
     */
    public void giveDefaultItems(CoCPlayer coCPlayer, APIPlayer apiPlayer) {
        Language lang = apiPlayer.getLanguage();
        Player player = apiPlayer.getPlayer();
        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(HotbarItems.POINTER)
                .setName(lang.getTranslation(TranslationKeys.COC_INV_POINTER_NAME)).setLore(lang.getTranslation(TranslationKeys.COC_INV_POINTER_LORE).split(";")).craft());

        player.getInventory().setItem(7, new ItemBuilder(HotbarItems.TUTORIAL_BOOK)
                .setName(lang.getTranslation(TranslationKeys.COC_INV_BOOK_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_INV_BOOK_LORE).split(";")).craft());

        int townHallLevel = coCPlayer.getTownHallLevel();
        if (townHallLevel > 0) {
            for (ResourceTypes resourceTypes : ResourceTypes.canReachWithTownHall(townHallLevel)) {
                if (resourceTypes != ResourceTypes.GEMS)
                    player.getInventory().addItem(new ItemBuilder(resourceTypes.getRepresentativeMaterial())
                            .setName(lang.getTranslation(TranslationKeys.COC_INV_RESOURCE_NAME, Arrays.asList("${color}", "${type}"), Arrays.asList(resourceTypes.getChatColor() + "", resourceTypes.getColoredName(lang)))).setLore(lang.getTranslation(TranslationKeys.COC_INV_RESOURCE_LORE).split(";")).craft());
            }

            if (!coCPlayer.getTroopsCampBuildings().isEmpty() && !coCPlayer.getTroopsCreateBuildings().isEmpty()) {
                //player.getInventory().setItem(5, new ItemBuilder(HotbarItems.ATTACK_HISTORY).setName(lang.getTranslation(TranslationKeys.COC_INV_ATTACKHISTORY_NAME)).setLore(lang.getTranslation(TranslationKeys.COC_INV_ATTACKHISTORY_LORE).split(";")).craft());
                //player.getInventory().setItem(6, new ItemBuilder(Material.IRON_SWORD).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setName(lang.getTranslation(TranslationKeys.COC_GUI_ATTACK_NAME)).setLore(lang.getTranslation(TranslationKeys.COC_GUI_ATTACK_LORE).split(";")).craft());
            }

            player.getInventory().setItem(8, new ItemBuilder(HotbarItems.SHOP)
                    .setName(lang.getTranslation(TranslationKeys.COC_INV_CONSTRUCTION_NAME)).setLore(lang.getTranslation(TranslationKeys.COC_INV_CONSTRUCTION_LORE).split(";")).craft());
        }
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

    public void clickBlock(CoCPlayer player, Block targetBlock) {
        Location location = targetBlock.getLocation();
        player.getAllBuildings().stream().filter(b -> LocationUtil.isBetween(b.getCoordinate(), location, b.getMaxCoordinate())).findFirst().ifPresent(b -> {
            APIPlayer apiPlayer = player.getOwner();
            if (apiPlayer != null)
                b.getInventory(apiPlayer.getLanguage()).open(apiPlayer);
        });
    }
}
