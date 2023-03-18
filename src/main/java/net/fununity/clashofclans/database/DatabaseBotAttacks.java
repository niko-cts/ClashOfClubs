package net.fununity.clashofclans.database;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.interfaces.IResourceContainerBuilding;
import net.fununity.clashofclans.buildings.list.ResourceContainerBuildings;
import net.fununity.clashofclans.buildings.list.ResourceGathererBuildings;
import net.fununity.misc.databasehandler.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseBotAttacks {
    private static DatabaseBotAttacks instance;

    /**
     * Get the singleton instance of this class.
     *
     * @return {@link DatabaseBotAttacks} - the singleton instance class.
     * @since 0.0.1
     */
    public static DatabaseBotAttacks getInstance() {
        if (instance == null)
            instance = new DatabaseBotAttacks();
        return instance;
    }

    private static final String TABLE = "game_coc_attack_bots";

    private final DatabaseHandler databaseHandler;
    private final List<String> botsUUID = Arrays.asList("81c2cfc1-3285-4eda-8022-39d5ec38061f");

    private final String whereBotsIn;


    /**
     * Instantiates the class.
     *
     * @since 0.0.1
     */
    private DatabaseBotAttacks() {
        this.databaseHandler = DatabaseHandler.getInstance();
        if (!this.databaseHandler.doesTableExist(TABLE)) {
            List<String> col = new ArrayList<>(Arrays.asList("attacker", "bot", "won"));
            List<String> data = new ArrayList<>(Arrays.asList("VARCHAR(36) NOT NULL", "VARCHAR(36) NOT NULL", "TINYINT NOT NULL default 0"));
            for (ResourceTypes resourceTypes : ResourceTypes.allWithoutGems()) {
                col.add(resourceTypes.name().toLowerCase());
                data.add("DOUBLE NOT NULL");
            }

            col.add("");
            data.add("PRIMARY KEY (attacker, bot)");

            this.databaseHandler.createTable(TABLE, col, data);
        }

        Iterator<String> iterator = botsUUID.iterator();

        StringBuilder botsIn = new StringBuilder().append("WHERE owner_uuid in (");
        while (iterator.hasNext()) {
            botsIn.append("'").append(iterator.next()).append("'");
            while (iterator.hasNext())
                botsIn.append(",");
        }
        this.whereBotsIn = botsIn.append(")").toString();
    }

    public void insertBot(UUID attacker, BotData botData) {
        List<String> col = new ArrayList<>(Arrays.asList(attacker + "", botData.botUUID + "", botData.won() ? "1" : "0"));
        List<String> types = new ArrayList<>(Arrays.asList("string", "string", ""));
        for (ResourceTypes resourceTypes : ResourceTypes.allWithoutGems()) {
            col.add(botData.resources().getOrDefault(resourceTypes, 0.0) + "");
            types.add("");
        }

        this.databaseHandler.insertIntoTable(TABLE, col, types);
    }

    public void updateBot(UUID attacker, BotData botData) {
        List<String> update = new ArrayList<>(List.of("won"));
        List<String> col = new ArrayList<>(List.of(botData.won() ? "1" : "0"));
        List<String> types = new ArrayList<>(List.of(""));
        for (ResourceTypes resourceTypes : ResourceTypes.allWithoutGems()) {
            update.add(resourceTypes.name().toLowerCase());
            col.add(botData.resources().getOrDefault(resourceTypes, 0.0) + "");
            types.add("");
        }

        this.databaseHandler.update(TABLE, update, col, types, "WHERE attacker='" + attacker + "' AND bot='" + botData.botUUID +  "' LIMIT 1");
    }

    public List<BotData> getAllDoneBots(UUID player) {
        List<BotData> botData = new ArrayList<>();
        List<String> col = new ArrayList<>(Arrays.asList("bot", "won"));
        for (ResourceTypes resourceTypes : ResourceTypes.allWithoutGems()) {
            col.add(resourceTypes.name().toLowerCase());
        }
        try (ResultSet set = this.databaseHandler.select(TABLE, col, "WHERE attacker='" + player + "' LIMIT " + botsUUID.size())) {
            while (set != null && set.next())
                botData.add(new BotData(UUID.fromString(set.getString("bot")), getFromSet(set), set.getInt("won") == 1));
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return botData;
    }

    public BotData getBot(UUID player, UUID bot) {
        try (ResultSet set = this.databaseHandler.select(TABLE, Arrays.asList("gold", "food", "won"), "WHERE attacker='" + player + "' AND bot='" + bot + "' LIMIT 1")) {
            if (set != null && set.next())
                return new BotData(bot, getFromSet(set), set.getInt("won") == 1);
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }

        return new BotData(bot, new EnumMap<>(ResourceTypes.class), false);
    }

    private Map<ResourceTypes, Double> getFromSet(ResultSet set) throws SQLException {
        Map<ResourceTypes, Double> map = new EnumMap<>(ResourceTypes.class);
        for (ResourceTypes resourceTypes : ResourceTypes.allWithoutGems()) {
            map.put(resourceTypes, set.getDouble(resourceTypes.name().toLowerCase()));
        }
        return map;
    }

    public Set<BotData> getNormalBotData() {
        Set<BotData> botData = new HashSet<>();
        List<String> values = new ArrayList<>(List.of("owner_uuid"));
        for (ResourceTypes resourceTypes : ResourceTypes.allWithoutGems()) {
            StringBuilder buildingIds = new StringBuilder();
            int i = 0;
            List<IResourceContainerBuilding> buildings = new ArrayList<>(List.of(ResourceGathererBuildings.values()));
            buildings.addAll(List.of(ResourceContainerBuildings.values()));
            for (IResourceContainerBuilding building : buildings) {
                if (building.getContainingResourceType() == resourceTypes) {
                    buildingIds.append(i > 0 ? " OR " : "").append("building_id='").append(building.name().toLowerCase()).append("'");
                    i++;
                }
            }

            values.add("(SELECT SUM(amount) FROM " + DatabaseBuildings.TABLE + " NATURAL JOIN " + DatabaseBuildings.TABLE_CONTAINER + " " + whereBotsIn +
                    " AND (" + buildingIds + ") GROUP BY owner_uuid) as \"" + resourceTypes.name().toLowerCase() + "\"");
        }

        try (ResultSet set = this.databaseHandler.select(DatabaseBuildings.TABLE, values, whereBotsIn + " LIMIT " + botsUUID.size())) {
            while (set != null && set.next())
                botData.add(new BotData(UUID.fromString(set.getString("owner_uuid")), getFromSet(set), false));
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return botData;
    }

    public record BotData(UUID botUUID, Map<ResourceTypes, Double> resources, boolean won) {
    }
}