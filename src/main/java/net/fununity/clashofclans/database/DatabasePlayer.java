package net.fununity.clashofclans.database;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.player.CoCDataPlayer;
import net.fununity.misc.databasehandler.DatabaseHandler;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Database class to transmit for the player database.
 * @author Niko
 * @since 0.0.1
 */
public class DatabasePlayer {

    private static DatabasePlayer instance;

    /**
     * Get the singleton instance of this class.
     * @return {@link DatabasePlayer} - the singeleton instance class.
     * @since 0.0.1
     */
    public static DatabasePlayer getInstance() {
        if(instance == null)
            instance = new DatabasePlayer();
        return instance;
    }

    private static final String TABLE_DATA = "game_coc_player_data";
    private static final List<String> PLAYER_DATA_VALUES = Arrays.asList(TABLE_DATA + ".uuid", "elo", "xp", "gems",
            "game_coc_player_data.x", "game_coc_player_data.z",
            "SUM(IF(type = '" +ResourceTypes.GOLD.name() + "', amount, 0)) as '" + ResourceTypes.GOLD.name().toLowerCase() + "'",
            "SUM(IF(type = '" + ResourceTypes.FOOD.name() + "', amount, 0)) as '" + ResourceTypes.FOOD.name().toLowerCase() + "'",
            "SUM(IF(type = '" + ResourceTypes.ELECTRIC.name() + "', amount, 0)) as '" + ResourceTypes.ELECTRIC.name().toLowerCase() + "'");

    private final DatabaseHandler databaseHandler;

    /**
     * Instantiates the class.
     * @since 0.0.1
     */
    private DatabasePlayer() {
        this.databaseHandler = DatabaseHandler.getInstance();
        if (!this.databaseHandler.doesTableExist(TABLE_DATA))
            this.databaseHandler.createTable(TABLE_DATA, Arrays.asList("uuid", "elo", "x", "z", "gems", "xp"),
                    Arrays.asList("VARCHAR(36) NOT NULL PRIMARY KEY", "INT NOT NULL DEFAULT 100", "INT NOT NULL", "INT NOT NULL", "INT NOT NULL DEFAULT 50", "INT NOT NULL default 0"));
    }

    /**
     * Check if the user exists.
     * @param uuid UUID - uuid to check.
     * @return boolean - get if the user exists.
     * @since 0.0.1
     */
    public boolean contains(UUID uuid) {
        try (ResultSet playerData = this.databaseHandler.select(TABLE_DATA, Collections.singletonList("1"), "WHERE uuid='" + uuid + "' LIMIT 1")) {
            return playerData != null && playerData.next();
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return false;
    }

    /**
     * Creates the user.
     * @param uuid UUID - uuid to create.
     * @param coordinate Location - the base.
     * @since 0.0.1
     */
    public void createUser(UUID uuid, Location coordinate) {
        this.databaseHandler.insertIntoTable(TABLE_DATA,
                Arrays.asList(uuid.toString(), "1000", coordinate.getBlockX()+"", coordinate.getBlockZ()+"", "50", "200"),
                Arrays.asList("string", "", "", "", "", ""));
    }

    /**
     * Deletes a user.
     * @param uuid UUID - uuid of player.
     * @since 0.0.1
     */
    public void deleteUser(UUID uuid) {
        this.databaseHandler.delete(TABLE_DATA, "WHERE uuid='" + uuid + "' LIMIT 1");
    }

    /**
     * Set the xp of a player.
     * @param uuid UUID - uuid to set.
     * @param exp int - the xp to set.
     * @since 0.0.1
     */
    public void setExp(UUID uuid, int exp) {
        updatePlayer(uuid, exp, "xp");
    }

    /**
     * Adds the xp of a player.
     * @param uuid UUID - uuid to set.
     * @param exp int - the xp to set.
     * @since 0.0.1
     */
    public void addExp(UUID uuid, int exp) {
        try (ResultSet set = this.databaseHandler.select(TABLE_DATA, Collections.singletonList("xp"), "WHERE uuid='" + uuid + "' LIMIT 1")) {
            if (set != null && set.next())
                this.databaseHandler.update(TABLE_DATA, Collections.singletonList("xp"), Collections.singletonList((set.getInt("xp") + exp) + ""), Collections.singletonList(""), "WHERE uuid='" + uuid + "' LIMIT 1");
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
    }

    /**
     * Set the gems of a player.
     * @param uuid UUID - uuid to set.
     * @param amount int - the gems to set.
     * @since 0.0.1
     */
    public void setGems(UUID uuid, int amount) {
        updatePlayer(uuid, amount, "gems");
    }

    /**
     * Set the elo of a player.
     * @param uuid UUID - uuid to set.
     * @param amount int - the elo to set.
     * @since 0.0.1
     */
    public void setElo(UUID uuid, int amount) {
        updatePlayer(uuid, amount, "elo");
    }

    /**
     * Updates the player data.
     * @param uuid UUID - uuid to update.
     * @param amount int - amount to set.
     * @param update String - the column to update.
     * @since 0.0.1
     */
    private void updatePlayer(UUID uuid, int amount, String update) {
        this.databaseHandler.update(TABLE_DATA, Collections.singletonList(update), Collections.singletonList(amount+""), Collections.singletonList(""), "WHERE uuid='" + uuid + "' LIMIT 1");
    }

    /**
     * Get the players data.
     * Includes all TABLE_DATA contents and the amount of gold, food and electric.
     * @param uuid UUID - the uuid to get the data from.
     * @return ResultSet - the result from the sql statement.
     * @since 0.0.1
     */
    public ResultSet getPlayerData(UUID uuid) {
        return this.databaseHandler.select(TABLE_DATA + ", " + DatabaseBuildings.TABLE_CONTAINER, PLAYER_DATA_VALUES,
                "WHERE " + TABLE_DATA + ".uuid=" + DatabaseBuildings.TABLE_CONTAINER + ".uuid AND " + TABLE_DATA + ".uuid='" + uuid + "' LIMIT 1");
    }

    /**
     * Get the highest coordinate.
     * @return Location - the highest coordinate.
     * @since 0.0.1
     */
    public Location getHighestCoordinate() {
        try (ResultSet set = this.databaseHandler.select(TABLE_DATA, Arrays.asList("x", "z"), "WHERE 1=1 ORDER BY x DESC, z DESC LIMIT 1")) {
            if (set != null && set.next()) {
                Location location = new Location(ClashOfClubs.getInstance().getWorld(), set.getInt("x"), ClashOfClubs.getBaseYCoordinate(), set.getInt("z"));
                return location.getBlockX() >= 20000000 ? location.add(-40000000, 0, ClashOfClubs.getBaseSize() * 3) : location.add(ClashOfClubs.getBaseSize() * 3, 0, 0);
            }
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }

        return new Location(ClashOfClubs.getInstance().getWorld(), -20000000, ClashOfClubs.getBaseYCoordinate(), -20000000);
    }


    /**
     * Get all players and their location.
     * @param blacklisted UUID - blacklisted uuid.
     * @return Map<UUID, Location> - all users with their base location.
     * @since 0.0.1
     */
    public List<CoCDataPlayer> getAllPlayerData(UUID blacklisted) {
        List<CoCDataPlayer> list = new ArrayList<>();
        try (ResultSet data = this.databaseHandler.select(TABLE_DATA + ", " + DatabaseBuildings.TABLE_CONTAINER, PLAYER_DATA_VALUES,
                "WHERE " + TABLE_DATA + ".uuid!='" + blacklisted + "'")) {
            while (data != null && data.next()) {

                int playerX = data.getInt("x");
                int playerZ = data.getInt("z");
                int xp = data.getInt("xp");
                int elo = data.getInt("elo");

                Map<ResourceTypes, Integer> resourceTypes = new EnumMap<>(ResourceTypes.class);
                for (ResourceTypes type : ResourceTypes.values())
                    resourceTypes.put(type, data.getInt(type.name().toLowerCase()));

                list.add(new CoCDataPlayer(UUID.fromString(data.getString(TABLE_DATA + ".uuid")),
                        new Location(ClashOfClubs.getInstance().getWorld(), playerX, ClashOfClubs.getBaseYCoordinate(), playerZ), resourceTypes, xp, elo));
            }
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }

        return list;
    }
}
