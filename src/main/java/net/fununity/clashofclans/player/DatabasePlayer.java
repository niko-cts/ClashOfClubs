package net.fununity.clashofclans.player;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.misc.databasehandler.DatabaseHandler;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

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
            this.databaseHandler.createTable(TABLE_DATA, Arrays.asList("uuid", "xp", "x", "z", "gems"),
                    Arrays.asList("VARCHAR(36) NOT NULL PRIMARY KEY", "INT NOT NULL DEFAULT 0", "INT NOT NULL", "INT NOT NULL", "INT NOT NULL default 0"));
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
            ClashOfClans.getInstance().getLogger().warning(exception.getMessage());
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
        this.databaseHandler.insertIntoTable(TABLE_DATA, Arrays.asList(uuid.toString(), "0", coordinate.getBlockX()+"", coordinate.getBlockZ()+"", "200"), Arrays.asList("string", "", "", "", ""));
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
            ClashOfClans.getInstance().getLogger().warning(exception.getMessage());
        }
    }

    public void setGems(UUID uuid, int amount) {
        updatePlayer(uuid, amount, "gems");
    }

    private void updatePlayer(UUID uuid, int amount, String update) {
        this.databaseHandler.update(TABLE_DATA, Collections.singletonList(update), Collections.singletonList(amount+""), Collections.singletonList(""), "WHERE uuid='" + uuid + "' LIMIT 1");
    }

    public ResultSet getPlayerData(UUID uuid) {
        return this.databaseHandler.select(DatabasePlayer.TABLE_DATA, Collections.singletonList("*"), "WHERE uuid='" + uuid + "' LIMIT 1");
    }

    public Location getHighestCoordinate() {
        try (ResultSet set = this.databaseHandler.select(TABLE_DATA, Arrays.asList("x", "z"), "WHERE 1=1 ORDER BY x DESC, z DESC LIMIT 1")) {
            if (set != null && set.next()) {
                Location location = new Location(ClashOfClans.getInstance().getPlayWorld(), set.getInt("x"), ClashOfClans.getBaseYCoordinate(), set.getInt("z"));
                return location.getBlockX() >= 20000000 ? location.add(-40000000, 0, ClashOfClans.getBaseSize() * 3) : location.add(ClashOfClans.getBaseSize() * 3, 0, 0);
            }
        } catch (SQLException exception) {
            ClashOfClans.getInstance().getLogger().warning(exception.getMessage());
        }

        return new Location(ClashOfClans.getInstance().getPlayWorld(), -20000000, ClashOfClans.getBaseYCoordinate(), -20000000);
    }

}
