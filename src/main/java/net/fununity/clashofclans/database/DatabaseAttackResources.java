package net.fununity.clashofclans.database;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.troops.TroopsBuilding;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.values.CoCValues;
import net.fununity.clashofclans.values.ICoCValue;
import net.fununity.misc.databasehandler.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Class for database communication to track resources which where stolen / gathered since last join.
 * @author Niko
 * @since 1.0.2
 */
public class DatabaseAttackResources {

    private static DatabaseAttackResources instance;

    /**
     * Get the singleton instance of this class.
     * @return {@link DatabaseAttackResources} - the singleton instance class.
     * @since 0.0.1
     */
    public static DatabaseAttackResources getInstance() {
        if (instance == null)
            instance = new DatabaseAttackResources();
        return instance;
    }

    private static final String TABLE = "game_coc_attack_resource_gathered";

    private final DatabaseHandler databaseHandler;

    /**
     * Instantiates the class.
     * @since 0.0.1
     */
    private DatabaseAttackResources() {
        this.databaseHandler = DatabaseHandler.getInstance();
        if (!this.databaseHandler.doesTableExist(TABLE)) {
            List<String> col = new ArrayList<>(Arrays.asList("id", "uuid"));
            List<String> data = new ArrayList<>(Arrays.asList("INT NOT NULL PRIMARY KEY AUTO_INCREMENT", "VARCHAR(36) NOT NULL"));

            for (ICoCValue value : CoCValues.stoleAbleResource()) {
                col.add(value.name().toLowerCase());
                data.add("INT NOT NULL DEFAULT 0");
            }

            this.databaseHandler.createTable(TABLE, col, data);
        }
    }

    /**
     * Inserts a new resource change of gathered resources
     * @param uuid UUID - uuid of the player.
     * @param resourcesGathered Map<String, Integer> resourcesGathered - the type and amount.
     * @since 1.0.2
     */
    public void insertNewResourceChange(UUID uuid, Map<String, Integer> resourcesGathered) {
        insertNewResourceChange(uuid, null, resourcesGathered);
    }

    /**
     * Inserts two new resource change. The stolen amount will be negative.
     * @param attacker UUID - uuid of the attacker.
     * @param defender UUID - uuid of the attacker.
     * @param resourcesGathered Map<String, Integer> resourcesGathered - the type and amount.
     * @since 1.0.2
     */
    public void insertNewResourceChange(UUID attacker, UUID defender, Map<String, Integer> resourcesGathered) {
        if (resourcesGathered.values().stream().noneMatch(v -> v > 0)) return;

        List<String> col = new ArrayList<>(Arrays.asList("null", attacker.toString()));
        List<String> types = new ArrayList<>(Arrays.asList("", "string"));
        for (Integer amount : resourcesGathered.values()) {
            col.add(amount + "");
            types.add("");
        }

        if (defender == null) {
            this.databaseHandler.insertIntoTable(TABLE, col, types);
            return;
        }

        col.add(null);
        types.add(null);
        col.add(defender.toString());
        types.add("string");
        for (Integer amount : resourcesGathered.values()) {
            col.add((-amount) + ""); // negative cause stolen
            types.add("");
        }

        this.databaseHandler.insertIntoTable(TABLE, col, types);
    }

    /**
     * Retrieve the summed up amount of resources gathered/lost through attacks.
     * This data will be deleted after the retrieve.
     * @param uuid UUID - uuid of player.
     * @return EnumMap<ResourceTypes, Integer> - summed up amount of resources.
     * @since 1.0.2
     */
    public Map<ICoCValue, Integer> retrieveAllAndDelete(UUID uuid) {
        Map<ICoCValue, Integer> resourcesGathered = new HashMap<>();
        try (ResultSet set = this.databaseHandler.select(TABLE, List.of("*"), "WHERE uuid='" + uuid + "'")) {
            StringBuilder whereClause = new StringBuilder().append("WHERE id IN (");
            boolean hasInput = false;
            while (set != null && set.next()) {
                for (ICoCValue type : CoCValues.stoleAbleResource()) {
                    resourcesGathered.put(type, resourcesGathered.getOrDefault(type, 0) + set.getInt(type.name().toLowerCase()));
                }
                if (hasInput) {
                    whereClause.append(",");
                }

                hasInput = true;
                whereClause.append(set.getInt("id"));
            }

            if (hasInput)
                this.databaseHandler.delete(TABLE, whereClause.append(")").toString());
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return resourcesGathered;
    }

    /**
     * Update troops data only.
     * @param troopsBuildings List<TroopsBuilding> - all troop buildings.
     * @since 1.0.2
     */
    public void updateTroopsData(List<TroopsBuilding> troopsBuildings) {
        List<List<String>> allColumns = new ArrayList<>();
        List<List<String>> allValues = new ArrayList<>();
        List<List<String>> allDataTypes = new ArrayList<>();
        List<String> whereClauses = new ArrayList<>();
        List<String> tableNames = new ArrayList<>();
        for (TroopsBuilding troopsCampBuilding : troopsBuildings) {
            List<String> columns = new ArrayList<>();
            List<String> values = new ArrayList<>();
            List<String> dataTypes = new ArrayList<>();

            for (Map.Entry<ITroop, Integer> entry : troopsCampBuilding.getTroopAmount().entrySet()) {
                columns.add(entry.getKey().name().toLowerCase());
                values.add(entry.getValue() + "");
                dataTypes.add("");
            }

            tableNames.add(DatabaseBuildings.TABLE_TROOPS);
            allColumns.add(columns);
            allValues.add(values);
            allDataTypes.add(dataTypes);
            whereClauses.add("WHERE building_uuid='" + troopsCampBuilding.getBuildingUUID() + "' LIMIT 1");
        }
        this.databaseHandler.update(tableNames, allColumns, allValues, allDataTypes, whereClauses);
    }
}
