package net.fununity.clashofclans.database;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.cloud.client.CloudClient;
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

    private final DatabaseHandler databaseHandler;

    /**
     * Instantiates the class.
     * @since 0.0.1
     */
    private DatabasePlayer() {
        this.databaseHandler = DatabaseHandler.getInstance();
        if (!this.databaseHandler.doesTableExist(TABLE_DATA))
            this.databaseHandler.createTable(TABLE_DATA, Arrays.asList("uuid", "elo", "x", "z", "gems", "xp", "last_login", "last_server"),
                    Arrays.asList("VARCHAR(36) NOT NULL PRIMARY KEY", "INT NOT NULL DEFAULT 100", "INT NOT NULL", "INT NOT NULL", "INT NOT NULL DEFAULT 50", "INT NOT NULL default 0", "LONG NOT NULL", "VARCHAR(32) NOT NULL"));
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
     * @param player CoCPlayer - player to create.
     * @since 0.0.1
     */
    public void createUser(CoCPlayer player) {
        this.databaseHandler.insertIntoTable(TABLE_DATA,
                Arrays.asList(player.getUniqueId().toString(), player.getElo()+"",
                        player.getBaseStartLocation().getBlockX()+"", player.getBaseStartLocation().getBlockZ()+"",
                        player.getGems()+ "", player.getExp()+"", player.getLastJoinMillis()+"", CloudClient.getInstance().getClientId()),
                Arrays.asList("string", "", "", "", "", "", "", "string"));
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
     * Updates player data.
     * Should be executed when quitting
     * @param players Collection<CoCPlayer> - all player to update.
     * @since 0.0.2
     */
    public void updatePlayer(Collection<CoCPlayer> players) {
        List<String> tableNames = new ArrayList<>();
        List<List<String>> allColumns = new ArrayList<>();
        List<List<String>> allValues = new ArrayList<>();
        List<List<String>> allDataTypes = new ArrayList<>();
        List<String> whereClauses = new ArrayList<>();

        for (CoCPlayer player : players) {
            tableNames.add(TABLE_DATA);
            allColumns.add(Arrays.asList("elo", "gems", "xp", "last_login", "last_server"));
            allValues.add(Arrays.asList(player.getElo()+"", player.getGems()+"", player.getExp()+"", System.currentTimeMillis()+"", CloudClient.getInstance().getClientId()));
            allDataTypes.add(Arrays.asList("", "", "", "string", "string"));
            whereClauses.add("WHERE uuid='" + player.getUniqueId() + "' LIMIT 1");
        }

        this.databaseHandler.update(tableNames, allColumns, allValues, allDataTypes, whereClauses);
    }

    /**
     * Get the players' data.
     * @param uuid UUID - the uuid to get the data from.
     * @return ResultSet - the result from the sql statement.
     * @since 0.0.1
     */
    public ResultSet getPlayerData(UUID uuid) {
        return this.databaseHandler.select(TABLE_DATA + ", " + DatabaseBuildings.TABLE_CONTAINER, Collections.singletonList("*"), "WHERE uuid='" + uuid + "' LIMIT 1");
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
                return location.getBlockX() >= 10000000 ? location.add(-10000000, 0, ClashOfClubs.getBaseSize() * 3) : location.add(ClashOfClubs.getBaseSize() * 3, 0, 0);
            }
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }

        return new Location(ClashOfClubs.getInstance().getWorld(), 0, ClashOfClubs.getBaseYCoordinate(), 0);
    }
}
