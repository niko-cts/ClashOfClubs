package net.fununity.clashofclans.database;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.instances.*;
import net.fununity.clashofclans.buildings.instances.resource.ResourceContainerBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceGatherBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsBuilding;
import net.fununity.clashofclans.buildings.instances.troops.TroopsCreateBuilding;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.troops.Troops;
import net.fununity.misc.databasehandler.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * The database class for buildings.
 * @see DatabaseHandler
 * @author Niko
 * @since 0.0.1
 */
public class DatabaseBuildings {

    private static DatabaseBuildings instance;

    /**
     * Get the singleton instance.
     *
     * @return {@link DatabaseBuildings} - the singleton instance of this class.
     * @since 0.0.1
     */
    public static DatabaseBuildings getInstance() {
        if (instance == null)
            instance = new DatabaseBuildings();
        return instance;
    }

    private static final String COL_INT = "INT NOT NULL";
    public static final String TABLE = "game_coc_building";
    public static final String TABLE_CONTAINER = "game_coc_building_container";
    private static final String TABLE_TROOPS = "game_coc_building_troops";
    private static final String TABLE_TROOPS_QUEUE = "game_coc_building_troops_queue";
    private static final String TABLE_CONSTRUCTION = "game_coc_building_constructions";
    private final DatabaseHandler databaseHandler;

    /**
     * Instantiates the class.
     *
     * @since 0.0.1
     */
    private DatabaseBuildings() {
        this.databaseHandler = DatabaseHandler.getInstance();
        if (!this.databaseHandler.doesTableExist(TABLE))
            this.databaseHandler.createTable(TABLE, Arrays.asList("building_uuid", "owner_uuid", "buildingID", "level", "x", "z", "rotation"),
                    Arrays.asList("VARCHAR(36) NOT NULL PRIMARY KEY", "VARCHAR(36) NOT NULL", "VARCHAR(36) NOT NULL", COL_INT + " default 1", COL_INT, COL_INT, "TINY" + COL_INT + " default 0"));
        if (!this.databaseHandler.doesTableExist(TABLE_CONTAINER))
            this.databaseHandler.createTable(TABLE_CONTAINER,
                    Arrays.asList("building_uuid", "amount"),
                    Arrays.asList("VARCHAR(36) NOT NULL PRIMARY KEY", "DOUBLE NOT NULL default 0"));
        if (!this.databaseHandler.doesTableExist(TABLE_TROOPS)) {
            List<String> column = new ArrayList<>(Collections.singletonList("building_uuid"));
            List<String> properties = new ArrayList<>(Collections.singletonList("VARCHAR(36) NOT NULL PRIMARY KEY"));
            for (Troops troop : Troops.values()) {
                column.add(troop.name().toLowerCase());
                properties.add("INT NOT NULL default 0");
            }
            this.databaseHandler.createTable(TABLE_TROOPS, column, properties);
        }

        if (!this.databaseHandler.doesTableExist(TABLE_TROOPS_QUEUE))
            this.databaseHandler.createTable(TABLE_TROOPS_QUEUE,
                    Arrays.asList("building_uuid", "queue"),
                    Arrays.asList("VARCHAR(36) NOT NULL PRIMARY KEY", "VARCHAR(100) NOT NULL DEFAULT ''"));

        if (!this.databaseHandler.doesTableExist(TABLE_CONSTRUCTION))
            this.databaseHandler.createTable(TABLE_CONSTRUCTION,
                    Arrays.asList("building_uuid", "finish_time"),
                    Arrays.asList("VARCHAR(36) NOT NULL PRIMARY KEY", "LONG NOT NULL"));
    }

    /**
     * Inserts the given buildings into the database.
     *
     * @param buildings {@link GeneralBuilding}[] - the building instances.
     * @since 0.0.1
     */
    public void buildBuilding(GeneralBuilding... buildings) {
        Iterator<GeneralBuilding> normalBuildings = Arrays.asList(buildings).iterator();
        Iterator<GeneralBuilding> resourceBuildings = Arrays.stream(buildings).filter(b -> b instanceof ResourceContainerBuilding).toList().iterator();
        Iterator<GeneralBuilding> troopsBuildings = Arrays.stream(buildings).filter(b -> b instanceof TroopsBuilding).toList().iterator();
        Iterator<GeneralBuilding> troopsQueueBuildings = Arrays.stream(buildings).filter(b -> b instanceof TroopsCreateBuilding).toList().iterator();

        List<String> values = new ArrayList<>();
        List<String> dataTypes = new ArrayList<>();
        while (normalBuildings.hasNext()) {
            GeneralBuilding building = normalBuildings.next();
            values.addAll(Arrays.asList(building.getBuildingUUID().toString(), building.getOwnerUUID().toString(), building.getBuilding().name() + "", building.getLevel() + "", building.getCoordinate().getBlockX() + "", building.getCoordinate().getBlockZ() + "", building.getRotation() + ""));
            dataTypes.addAll(Arrays.asList("string", "string", "string", "", "", "", ""));
            if (normalBuildings.hasNext()) {
                values.add(null);
                dataTypes.add(null);
            }
        }
        if (!values.isEmpty())
            this.databaseHandler.insertIntoTable(TABLE, values, dataTypes);
        values.clear();
        dataTypes.clear();

        while (resourceBuildings.hasNext()) {
            GeneralBuilding building = resourceBuildings.next();
            values.addAll(Arrays.asList(building.getBuildingUUID().toString(), ((ResourceContainerBuilding) building).getAmount() + ""));
            dataTypes.addAll(Arrays.asList("string", ""));
            if (resourceBuildings.hasNext()) {
                values.add(null);
                dataTypes.add(null);
            }
        }
        if (!values.isEmpty())
            this.databaseHandler.insertIntoTable(TABLE_CONTAINER, values, dataTypes);
        values.clear();
        dataTypes.clear();

        while (troopsBuildings.hasNext()) {
            GeneralBuilding building = troopsBuildings.next();
            values.add(building.getBuildingUUID().toString());
            dataTypes.add("string");
            for (Troops ignored : Troops.values()) {
                values.add("0");
                dataTypes.add("");
            }
            if (troopsBuildings.hasNext()) {
                values.add(null);
                dataTypes.add(null);
            }
        }
        if (!values.isEmpty())
            this.databaseHandler.insertIntoTable(TABLE_TROOPS, values, dataTypes);

        while (troopsQueueBuildings.hasNext()) {
            GeneralBuilding building = troopsQueueBuildings.next();
            values.add(building.getBuildingUUID().toString());
            dataTypes.add("string");
            if (troopsQueueBuildings.hasNext()) {
                values.add(null);
                dataTypes.add(null);
            }
        }
        if (!values.isEmpty())
            this.databaseHandler.insertIntoTable(TABLE_TROOPS_QUEUE, values, dataTypes);
    }

    /**
     * Updates every special building data
     *
     * @param coCPlayer CocPlayer - the player to update the buildings.
     */
    public void updateBuildings(CoCPlayer coCPlayer) {
        for (ResourceTypes type : ResourceTypes.values()) {
            for (ResourceGatherBuilding resourceGatherBuilding : coCPlayer.getResourceGatherBuildings(type)) {
                this.databaseHandler.update(TABLE_CONTAINER, Collections.singletonList("amount"), Collections.singletonList(resourceGatherBuilding.getAmount() + ""), Collections.singletonList(""), "WHERE building_uuid='" + resourceGatherBuilding.getBuildingUUID() + "' LIMIT 1");
            }
            for (ResourceContainerBuilding resourceContainerBuilding : coCPlayer.getResourceContainerBuildings(type)) {
                this.databaseHandler.update(TABLE_CONTAINER, Collections.singletonList("amount"), Collections.singletonList(resourceContainerBuilding.getAmount() + ""), Collections.singletonList(""), "WHERE building_uuid='" + resourceContainerBuilding.getBuildingUUID() + "' LIMIT 1");
            }
        }
        List<TroopsBuilding> troopsBuildings = new ArrayList<>(coCPlayer.getTroopsCreateBuildings());
        for (TroopsBuilding troopsBuilding : troopsBuildings) {
            this.databaseHandler.update(TABLE_TROOPS_QUEUE, Collections.singletonList("queue"),
                    Collections.singletonList(((TroopsCreateBuilding) troopsBuilding).getTroopsQueueId()), Collections.singletonList("string"), "WHERE building_uuid='" + troopsBuilding.getBuildingUUID() + "' LIMIT 1");
        }

        troopsBuildings.addAll(coCPlayer.getTroopsCampBuildings());
        for (TroopsBuilding troopsCampBuilding : troopsBuildings) {
            List<String> columns = new ArrayList<>();
            List<String> values = new ArrayList<>();
            List<String> dataTypes = new ArrayList<>();

            for (Map.Entry<ITroop, Integer> entry : troopsCampBuilding.getTroopAmount().entrySet()) {
                columns.add(entry.getKey().name().toLowerCase());
                values.add(entry.getValue() + "");
                dataTypes.add("");
            }

            this.databaseHandler.update(TABLE_TROOPS, columns, values, dataTypes, "WHERE building_uuid='" + troopsCampBuilding.getBuildingUUID() + "' LIMIT 1");

        }
    }

    /**
     * Upgrades the building.
     * @param building {@link GeneralBuilding} - the building instance.
     * @since 0.0.1
     */
    public void upgradeBuilding(GeneralBuilding building) {
        this.databaseHandler.update(TABLE, Collections.singletonList("level"), Collections.singletonList(building.getLevel() + ""), Collections.singletonList(""), "WHERE building_uuid='" + building.getBuildingUUID() + "' LIMIT 1");
    }

    /**
     * Updates the move coordinate.
     * @param generalBuilding GeneralBuilding - the building.
     * @since 0.0.1
     */
    public void moveBuilding(GeneralBuilding generalBuilding) {
        this.databaseHandler.update(TABLE, Arrays.asList("x", "z", "rotation"),
                Arrays.asList(generalBuilding.getCoordinate().getBlockX() + "", generalBuilding.getCoordinate().getBlockZ() + "", generalBuilding.getRotation() + ""),
                Arrays.asList("", "", ""),
                "WHERE building_uuid='" + generalBuilding.getBuildingUUID() + "' LIMIT 1");
    }

    /**
     * Removes a building.
     * @param building {@link GeneralBuilding} - the building.
     * @since 0.0.1
     */
    public void deleteBuilding(GeneralBuilding building) {
        String whereClause = "WHERE building_uuid='" + building.getBuildingUUID() + "' LIMIT 1";
        if (building instanceof ResourceContainerBuilding)
            this.databaseHandler.delete(TABLE_CONTAINER, whereClause);
        if (building instanceof TroopsBuilding) {
            this.databaseHandler.delete(TABLE_TROOPS, whereClause);
            if (building instanceof TroopsCreateBuilding)
                this.databaseHandler.delete(TABLE_TROOPS_QUEUE, whereClause);
        }
        if (building instanceof ConstructionBuilding)
            this.databaseHandler.delete(TABLE_CONSTRUCTION, whereClause);
        this.databaseHandler.delete(TABLE, whereClause);
    }

    /**
     * Get all buildings of the uuid.
     * @param uuid UUID - uuid of player.
     * @return ResultSet - the result set.
     * @since 0.0.1
     */
    public ResultSet getBuildings(UUID uuid) {
        String building_col = ".building_uuid";
        return this.databaseHandler.select(TABLE, Collections.singletonList("*"), new StringBuilder()
                .append("LEFT JOIN ").append(TABLE_CONTAINER).append(" ON ").append(TABLE).append(building_col).append("=").append(TABLE_CONTAINER).append(building_col)
                .append(" LEFT JOIN ").append(TABLE_TROOPS).append(" ON ").append(TABLE).append(building_col).append("=").append(TABLE_TROOPS).append(building_col)
                .append(" LEFT JOIN ").append(TABLE_TROOPS_QUEUE).append(" ON ").append(TABLE).append(building_col).append("=").append(TABLE_TROOPS_QUEUE).append(building_col)
                .append(" LEFT JOIN ").append(TABLE_CONSTRUCTION).append(" ON ").append(TABLE).append(building_col).append("=").append(TABLE_CONSTRUCTION).append(building_col)
                .append(" WHERE owner_uuid='").append(uuid).append("'").toString());
    }

    /**
     * Insert construction building in database.
     * @param building {@link ConstructionBuilding} - the building.
     * @since 0.0.1
     */
    public void constructBuilding(ConstructionBuilding building) {
        this.databaseHandler.insertIntoTable(TABLE_CONSTRUCTION,
                Arrays.asList(building.getBuildingUUID().toString(), building.getBuildingFinishTime() + ""), Arrays.asList("string", ""));
    }

    /**
     * Delete the construction building from the database.
     * @param buildingUUID UUID - the uuid of the building
     * @since 0.0.1
     */
    public void removeConstruction(UUID buildingUUID) {
        this.databaseHandler.delete(TABLE_CONSTRUCTION, "WHERE building_uuid='" + buildingUUID + "' LIMIT 1");
    }

    /**
     * Deletes all buildings that are listed in the buildings table.
     * @param uuid UUID - uuid of the player.
     * @since 0.0.1
     */
    public void deleteAllBuildings(UUID uuid) {
        List<UUID> uuids = new ArrayList<>();
        try (ResultSet resultSet = this.databaseHandler.select(TABLE, Collections.singletonList("building_uuid"), "WHERE uuid='" + uuid + "'")) {
            while (resultSet != null && resultSet.next()) {
                uuids.add(UUID.fromString(resultSet.getString("building_uuid")));
            }
        } catch (SQLException exception) {
            // ignored
        }
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        Iterator<UUID> iterator = uuids.iterator();
        while (iterator.hasNext()) {
            builder.append("'").append(iterator.next()).append("'");
            if(iterator.hasNext())
                builder.append(",");
        }
        builder.append(")");

        this.databaseHandler.delete(TABLE, "WHERE owner_uuid='" + uuid + "'");
        this.databaseHandler.delete(TABLE_TROOPS, "WHERE building_uuid in" + builder);
        this.databaseHandler.delete(TABLE_CONSTRUCTION, "WHERE building_uuid in" + builder);
        this.databaseHandler.delete(TABLE_CONTAINER, "WHERE building_uuid in" + builder);
        this.databaseHandler.delete(TABLE_TROOPS_QUEUE, "WHERE building_uuid in" + builder);
    }

}
