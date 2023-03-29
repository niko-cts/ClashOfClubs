package net.fununity.clashofclans.database;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.interfaces.IResourceContainerBuilding;
import net.fununity.clashofclans.buildings.list.ResourceContainerBuildings;
import net.fununity.clashofclans.buildings.list.ResourceGathererBuildings;
import net.fununity.clashofclans.values.CoCValues;
import net.fununity.clashofclans.values.ICoCValue;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.misc.databasehandler.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * A class for database communication regarding bot attacks.
 * @author Niko
 * @since 1.0.1
 */
public class DatabaseAttackBots {
    private static DatabaseAttackBots instance;

    /**
     * Get the singleton instance of this class.
     *
     * @return {@link DatabaseAttackBots} - the singleton instance class.
     * @since 0.0.1
     */
    public static DatabaseAttackBots getInstance() {
        if (instance == null)
            instance = new DatabaseAttackBots();
        return instance;
    }

    private static final String TABLE = "game_coc_attack_bots";

    private final DatabaseHandler databaseHandler;
    private final List<String> botsUUID;

    private final String whereBotsIn;


    /**
     * Instantiates the class.
     * Checks if any tables need to be created.
     * @since 0.0.1
     */
    private DatabaseAttackBots() {
        this.databaseHandler = DatabaseHandler.getInstance();
        if (!this.databaseHandler.doesTableExist(TABLE)) {
            List<String> col = new ArrayList<>(Arrays.asList("attacker", "bot", "won"));
            List<String> data = new ArrayList<>(Arrays.asList("VARCHAR(36) NOT NULL", "VARCHAR(36) NOT NULL", "TINYINT NOT NULL default 0"));
            for (ICoCValue value : CoCValues.stoleAbleResource()) {
                if (value instanceof ResourceTypes) { // from bot can no elo be stolen
                    col.add(value.name().toLowerCase());
                    data.add("INT NOT NULL DEFAULT 0");
                }
            }

            col.add("");
            data.add("PRIMARY KEY (attacker, bot)");

            this.databaseHandler.createTable(TABLE, col, data);
        }

        this.botsUUID = Arrays.asList("81c2cfc1-3285-4eda-8022-39d5ec38061f"); // the bots uuid
        Iterator<String> iterator = botsUUID.iterator();

        StringBuilder botsIn = new StringBuilder().append("WHERE owner_uuid in (");
        while (iterator.hasNext()) {
            botsIn.append("'").append(iterator.next()).append("'");
            while (iterator.hasNext())
                botsIn.append(",");
        }
        this.whereBotsIn = botsIn.append(")").toString();
    }

    /**
     * Inserts a new bot data record with the given attacker.
     * @param attacker UUID - the uuid of the player attacker.
     * @param botData BotData - the record of the bot data.
     * @since 1.0.1
     */
    public void changeBotData(UUID attacker, BotData botData, boolean update) {
        if (update) {
            updateBot(attacker, botData);
            return;
        }
        List<String> col = new ArrayList<>(Arrays.asList(attacker + "", botData.botUUID + "", botData.won() ? "1" : "0"));
        List<String> types = new ArrayList<>(Arrays.asList("string", "string", ""));
        for (ResourceTypes value : ResourceTypes.values()) {
            col.add(botData.resources().getOrDefault(value, 0) + "");
            types.add("");
        }

        this.databaseHandler.insertIntoTable(TABLE, col, types);
    }

    /**
     * Updates attack information.
     * @param attacker UUID - uuid of the attacker.
     * @param botData BotData - the record of the bot data.
     * @since 1.0.1
     */
    private void updateBot(UUID attacker, BotData botData) {
        List<String> update = new ArrayList<>(List.of("won"));
        List<String> col = new ArrayList<>(List.of(botData.won() ? "1" : "0"));
        List<String> types = new ArrayList<>(List.of(""));
        for (Map.Entry<ResourceTypes, Integer> entry : botData.resources.entrySet()) {
            update.add(entry.getKey().name().toLowerCase());
            col.add(entry.getValue() + "");
            types.add("");
        }

        this.databaseHandler.update(TABLE, update, col, types, "WHERE attacker='" + attacker + "' AND bot='" + botData.botUUID +  "' LIMIT 1");
    }

    /**
     * Returns a list of all bots the player has won against.
     * @param player UUID - uuid of the player.
     * @return List<BotData> - the list of bots.
     * @since 1.01
     */
    public List<BotData> getAllDoneBots(UUID player) {
        List<BotData> botData = new ArrayList<>();
        List<String> col = new ArrayList<>(Arrays.asList("bot", "won"));
        for (ResourceTypes value : ResourceTypes.values()) {
            col.add(value.name().toLowerCase());
        }

        try (ResultSet set = this.databaseHandler.select(TABLE, col, "WHERE attacker='" + player + "' LIMIT " + botsUUID.size())) {
            while (set != null && set.next())
                botData.add(new BotData(UUID.fromString(set.getString("bot")), getFromSet(set), set.getInt("won") == 1));
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return botData;
    }

    /**
     * Returns the bot data with the given player.
     * The resources will be the amount the player stolen from the bot.
     * @param player UUID - uuid of the player.
     * @param bot UUID - uuid of the bot.
     * @return BotData - the record of the bot.
     * @since 1.0.1
     */
    public BotData getBot(UUID player, UUID bot) {
        List<String> col = new ArrayList<>(Collections.singletonList("won"));
        for (ResourceTypes value : ResourceTypes.values()) {
            col.add(value.name().toLowerCase());
        }

        try (ResultSet set = this.databaseHandler.select(TABLE, col, "WHERE attacker='" + player + "' AND bot='" + bot + "' LIMIT 1")) {
            if (set != null && set.next())
                return new BotData(bot, getFromSet(set), set.getInt("won") == 1);
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }

        return new BotData(bot, new EnumMap<>(ResourceTypes.class), false);
    }


    /**
     * Returns a map with the amount of resource from the given set.
     * @param set ResultSet - the result set.
     * @return Map<ResourceTypes, Integer> - the amount of resource per type.
     * @since 1.0.1
     */
    private EnumMap<ResourceTypes, Integer> getFromSet(ResultSet set) throws SQLException {
        EnumMap<ResourceTypes, Integer> map = new EnumMap<>(ResourceTypes.class);
        for (ResourceTypes value : ResourceTypes.values()) {
            map.put(value, set.getInt(value.name().toLowerCase()));
        }
        return map;
    }

    /**
     * Returns a set of bot data with attackable bots and their resources.
     * @return Set<BotData> - the hashset of attackable bots.
     * @since 1.0.1
     */
    public Set<BotData> getNormalBotData() {
        Set<BotData> botData = new HashSet<>();
        List<String> values = new ArrayList<>(List.of("owner_uuid"));
        for (ResourceTypes value : ResourceTypes.values()) { // bot does not have elo +
            StringBuilder buildingIds = new StringBuilder();
            int i = 0;
            List<IResourceContainerBuilding> buildings = new ArrayList<>(List.of(ResourceGathererBuildings.values()));
            buildings.addAll(List.of(ResourceContainerBuildings.values()));
            for (IResourceContainerBuilding building : buildings) {
                if (building.getContainingResourceType() == value) {
                    buildingIds.append(i > 0 ? " OR " : "").append("building_id='").append(building.name().toLowerCase()).append("'");
                    i++;
                }
            }

            if (i == 0)
                continue;

            values.add("(SELECT SUM(amount) FROM " + DatabaseBuildings.TABLE + " NATURAL JOIN " + DatabaseBuildings.TABLE_CONTAINER + " " + whereBotsIn +
                    " AND (" + buildingIds + ") GROUP BY owner_uuid) as \"" + value.name().toLowerCase() + "\"");
        }

        try (ResultSet set = this.databaseHandler.select(DatabaseBuildings.TABLE, values, whereBotsIn + " LIMIT " + botsUUID.size())) {
            while (set != null && set.next())
                botData.add(new BotData(UUID.fromString(set.getString("owner_uuid")), getFromSet(set), false));
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return botData;
    }

    /**
     * The bot data record which stores information about the bot and its resources.
     * Will be used for attacking information and general bot information.
     * @param botUUID UUID - the uuid of the bot.
     * @param resources EnumMap<ResourceTypes, Integer> - stores the amount of resources.
     * @param won boolean - bot was defeated (used for player attacking information).
     * @since 1.0.1
     */
    public record BotData(UUID botUUID, EnumMap<ResourceTypes, Integer> resources, boolean won) { }
}