package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.buildings.classes.ConstructionBuilding;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceContainerBuilding;
import net.fununity.clashofclans.buildings.classes.TroopsBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.troops.Troops;
import net.fununity.misc.databasehandler.DatabaseHandler;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
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
     * @return {@link DatabaseBuildings} - the singleton instance of this class.
     * @since 0.0.1
     */
    public static DatabaseBuildings getInstance() {
        if(instance == null)
            instance = new DatabaseBuildings();
        return instance;
    }

    private static final String COL_INT = "INT NOT NULL";
    private static final String TABLE = "game_coc_building";
    public static final String TABLE_CONTAINER = "game_coc_building_container";
    private static final String TABLE_TROOPS = "game_coc_building_troops";
    private static final String TABLE_CONSTRUCTION = "game_coc_building_constructions";
    private static final String INNER_JOIN_ON_ALL_BUILDINGS = new StringBuilder()
                .append("LEFT JOIN ").append(TABLE_CONTAINER)
                .append(" ON ").append(TABLE).append(".x=").append(TABLE_CONTAINER).append(".x AND ")
                .append(TABLE).append(".z=").append(TABLE_CONTAINER).append(".z ")
                .append("LEFT JOIN ").append(TABLE_TROOPS)
                .append(" ON ").append(TABLE).append(".x=").append(TABLE_TROOPS).append(".x AND ")
                .append(TABLE).append(".z=").append(TABLE_TROOPS).append(".z ").toString();

    private final DatabaseHandler databaseHandler;

    /**
     * Instantiates the class.
     * @since 0.0.1
     */
    private DatabaseBuildings() {
        this.databaseHandler = DatabaseHandler.getInstance();
        if (!this.databaseHandler.doesTableExist(TABLE))
            this.databaseHandler.createTable(TABLE, Arrays.asList("uuid", "buildingID", "level", "x", "z", "rotation"),
                    Arrays.asList("VARCHAR(36) NOT NULL", "VARCHAR(36) NOT NULL", COL_INT + " default 1", COL_INT, COL_INT, "TINY" + COL_INT + " default 0"));
        if (!this.databaseHandler.doesTableExist(TABLE_CONTAINER))
            this.databaseHandler.createTable(TABLE_CONTAINER,
                    Arrays.asList("uuid", "x", "z", "amount", "type"),
                    Arrays.asList("VARCHAR(36) NOT NULL", COL_INT, COL_INT, "DOUBLE NOT NULL default 0", "VARCHAR(36) NOT NULL"));
        if (!this.databaseHandler.doesTableExist(TABLE_TROOPS)) {
            List<String> column = new ArrayList<>(Arrays.asList("uuid", "x", "z"));
            List<String> properties = new ArrayList<>(Arrays.asList("VARCHAR(36) NOT NULL", COL_INT, COL_INT));
            for (Troops troop : Troops.values()) {
                column.add(troop.name().toLowerCase());
                properties.add("INT NOT NULL default 0");
            }
            this.databaseHandler.createTable(TABLE_TROOPS, column, properties);
        }
        if (!this.databaseHandler.doesTableExist(TABLE_CONSTRUCTION))
            this.databaseHandler.createTable(TABLE_CONSTRUCTION,
                    Arrays.asList("uuid", "x", "z", "date"),
                    Arrays.asList("VARCHAR(36) NOT NULL", COL_INT, COL_INT, "VARCHAR(36) NOT NULL"));
    }

    /**
     * Creates the building.
     * @param uuid UUID - uuid of player.
     * @param building {@link GeneralBuilding} - the building instance.
     * @since 0.0.1
     */
    public void buildBuilding(UUID uuid, GeneralBuilding building) {
        this.databaseHandler.insertIntoTable(TABLE,
                Arrays.asList(uuid.toString(), building.getBuilding().name()+"", building.getLevel()+"", building.getCoordinate().getBlockX() + "", building.getCoordinate().getBlockZ() + "", building.getRotation()+""),
                Arrays.asList("string", "string", "", "", "", ""));
        if (building instanceof ResourceContainerBuilding)
           this.databaseHandler.insertIntoTable(TABLE_CONTAINER,
                   Arrays.asList(uuid.toString(), building.getCoordinate().getBlockX()+"", building.getCoordinate().getBlockZ()+"", ((ResourceContainerBuilding) building).getAmount() + "", ((ResourceContainerBuilding) building).getContainingResourceType().name()),
                   Arrays.asList("string", "", "", "", "string"));
        if (building instanceof TroopsBuilding) {
            List<String> column = new ArrayList<>(Arrays.asList(uuid.toString(), building.getCoordinate().getBlockX() + "", building.getCoordinate().getBlockZ() + ""));
            List<String> properties = new ArrayList<>(Arrays.asList("string", "", ""));
            for (Troops ignored : Troops.values()) {
                column.add("0");
                properties.add("");
            }
            this.databaseHandler.insertIntoTable(TABLE_TROOPS, column, properties);
        }
    }


    /**
     * Get the buildings from the locations.
     * @param buildings IBuilding[] - of the buildings.
     * @return ResultSet - the result set with the buildingID and level.
     * @since 0.0.1
     */
    public ResultSet getSpecifiedBuilding(IBuilding[] buildings) {
        List<String> columns = new ArrayList<>(Arrays.asList("buildingID", "level", TABLE + ".x", TABLE + ".z", "rotation", "amount"));
        for (Troops troops : Troops.values())
            columns.add(troops.name().toLowerCase());

        StringBuilder builder = new StringBuilder().append(INNER_JOIN_ON_ALL_BUILDINGS);

        builder.append("WHERE buildingID in(");

        Iterator<IBuilding> iterator = Arrays.asList(buildings).iterator();

        while (iterator.hasNext()) {
            IBuilding building = iterator.next();
            builder.append("'").append(building.getNameKey()).append("'");
            if (iterator.hasNext())
                builder.append(", ");
        }

        builder.append(")");

        return this.databaseHandler.select(TABLE, columns, builder.toString());
    }

    /**
     * Upgrades the building.
     * @param uuid UUID - uuid of player.
     * @param building {@link GeneralBuilding} - the building instance.
     * @param level int - the new level
     * @since 0.0.1
     */
    public void upgradeBuilding(UUID uuid, GeneralBuilding building, int level) {
        this.databaseHandler.update(TABLE, Collections.singletonList("level"), Collections.singletonList(level+""), Collections.singletonList(""), "WHERE uuid='" + uuid + "' AND x=" + building.getCoordinate().getBlockX() + " AND z=" + building.getCoordinate().getBlockZ() + " LIMIT 1");
   }

    /**
     * Updates the move coordinate.
     * @param generalBuilding GeneralBuilding - the building.
     * @param oldCoordinate Location - the old coordinate.
     * @param oldRotation int - the old rotation of the building.
     * @since 0.0.1
     */
    public void moveBuilding(GeneralBuilding generalBuilding, Location oldCoordinate, int oldRotation) {
        String whereClause = "WHERE x=" + oldCoordinate.getBlockX() + " AND z=" + oldCoordinate.getBlockZ() + " LIMIT 1";
        List<String> update = new ArrayList<>(Arrays.asList("x", "z"));
        List<String> values = new ArrayList<>(Arrays.asList(generalBuilding.getCoordinate().getBlockX() + "", generalBuilding.getCoordinate().getBlockZ() + ""));
        List<String> properties = new ArrayList<>(Arrays.asList("", ""));
        if (generalBuilding instanceof ResourceContainerBuilding)
            this.databaseHandler.update(TABLE_CONTAINER, update, values, properties, whereClause);
        if (generalBuilding instanceof TroopsBuilding)
            this.databaseHandler.update(TABLE_TROOPS, update, values, properties, whereClause);

        if (oldRotation != generalBuilding.getRotation()) {
            update.add("rotation");
            values.add(generalBuilding.getRotation() + "");
            properties.add("");
        }

        this.databaseHandler.update(TABLE, update, values, properties, whereClause);
    }

    /**
     * Removes a building.
     * @param building {@link GeneralBuilding} - the building.
     * @since 0.0.1
     */
    public void deleteBuilding(GeneralBuilding building) {
        String whereClause = "WHERE x=" + building.getCoordinate().getBlockX() + " AND z=" + building.getCoordinate().getBlockZ() + " LIMIT 1";
        if (building instanceof ResourceContainerBuilding)
            this.databaseHandler.delete(TABLE_CONTAINER, whereClause);
        if (building instanceof TroopsBuilding)
            this.databaseHandler.delete(TABLE_TROOPS, whereClause);
        this.databaseHandler.delete(TABLE, whereClause);
    }

    /**
     * Updates the data of the building.
     * @param location location - the building location.
     * @param amount int - the new amount.
     * @since 0.0.1
     */
    public void updateData(Location location, int amount) {
        this.databaseHandler.update(TABLE_CONTAINER, Collections.singletonList("amount"), Collections.singletonList(amount+""), Collections.singletonList(""),
                "WHERE x=" + location.getBlockX() + " AND z=" + location.getBlockZ() + " LIMIT 1");
    }

    /**
     * Updates the whole building troops data.
     * @param changedBuilding TroopsBuilding - the building.
     * @since 0.0.1
     */
    public void updateTroopsData(TroopsBuilding changedBuilding) {
        List<String> update = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<String> properties = new ArrayList<>();
        for (Map.Entry<ITroop, Integer> entry : changedBuilding.getTroopAmount().entrySet()) {
            update.add(entry.getKey().name().toLowerCase());
            values.add(entry.getValue() + "");
            properties.add("");
        }
        this.databaseHandler.update(TABLE_TROOPS, update, values, properties, "WHERE x=" + changedBuilding.getCoordinate().getBlockX() + " AND z=" + changedBuilding.getCoordinate().getBlockZ() + " LIMIT 1");
    }

    /**
     * Updates the data of the building.
     * @param location location - the building location.
     * @param troop ITroop - the troop.
     * @param amount int - the new amount.
     * @since 0.0.1
     */
    public void updateTroopsData(Location location, ITroop troop, int amount) {
        this.databaseHandler.update(TABLE_TROOPS, Collections.singletonList(troop.name().toLowerCase()), Collections.singletonList(amount+""), Collections.singletonList(""),"WHERE x=" + location.getBlockX() + " AND z=" + location.getBlockZ() + " LIMIT 1");
    }

    /**
     * Get all buildings of the uuid.
     * @param uuid UUID - uuid of player.
     * @return ResultSet - the result set.
     * @since 0.0.1
     */
    public ResultSet getBuildings(UUID uuid) {
        List<String> columns = new ArrayList<>(Arrays.asList("buildingID", "level", TABLE + ".x", TABLE + ".z", "rotation", "amount"));
        for (Troops troops : Troops.values())
            columns.add(troops.name().toLowerCase());

        StringBuilder whereClause = new StringBuilder().append(INNER_JOIN_ON_ALL_BUILDINGS)
                .append("WHERE ").append(TABLE).append(".uuid='").append(uuid).append("'");

        return this.databaseHandler.select(TABLE, columns, whereClause.toString());
    }

    /**
     * Get all troops data buildings of the uuid.
     * @param uuid UUID - uuid of player.
     * @return ResultSet - the result set.
     * @since 0.0.1
     */
    public ResultSet getTroopsDataBuildings(UUID uuid) {
        return this.databaseHandler.select(TABLE_TROOPS, Collections.singletonList("*"), "WHERE uuid='" + uuid + "'");
    }

    /**
     * Insert construction building in database.
     * @param building {@link ConstructionBuilding} - the building.
     * @since 0.0.1
     */
    public void constructBuilding(ConstructionBuilding building) {
        this.databaseHandler.insertIntoTable(TABLE_CONSTRUCTION, Arrays.asList(building.getUuid().toString(), building.getCoordinate().getBlockX() + "", building.getCoordinate().getBlockZ()+"", ChronoUnit.SECONDS.addTo(OffsetDateTime.now(), building.getMaxBuildingDuration()).toString()), Arrays.asList("string", "", "", "string"));
    }

    /**
     * Delete the construction building from the database.
     * @param location Location - the location
     * @since 0.0.1
     */
    public void finishedBuilding(Location location) {
        this.databaseHandler.delete(TABLE_CONSTRUCTION, "WHERE x=" + location.getBlockX() + " AND z=" + location.getBlockZ() + " LIMIT 1");
    }

    /**
     * Get all construction buildings.
     * @return ResultSet - the result set.
     * @since 0.0.1
     */
    public ResultSet getConstructionBuildings() {
        return this.databaseHandler.select(TABLE_CONSTRUCTION,
                Arrays.asList(TABLE_CONSTRUCTION + ".uuid", TABLE_CONSTRUCTION + ".x", TABLE_CONSTRUCTION + ".z", "date", "buildingID", "rotation", "level"),
                new StringBuilder().append("LEFT JOIN ").append(TABLE).append(" ON ").append(TABLE_CONSTRUCTION).append(".x=").append(TABLE).append(".x AND ")
                        .append(TABLE_CONSTRUCTION).append(".z=").append(TABLE).append(".z").toString());
    }

    /**
     * Get all construction buildings form one player.
     * @param uuid UUID - the uuid of the player.
     * @return ResultSet - the result set.
     * @since 0.0.1
     */
    public ResultSet getConstructionBuildings(UUID uuid) {
        return this.databaseHandler.select(TABLE_CONSTRUCTION, Collections.singletonList("*"), "WHERE uuid='" + uuid + "'");
    }

    /**
     * Deletes all buildings that are listed in the buildings table.
     * @param uuid UUID - uuid of the player.
     * @since 0.0.1
     */
    public void deleteAllBuildings(UUID uuid) {
        this.databaseHandler.delete(TABLE, "WHERE uuid='" + uuid + "'");
        this.databaseHandler.delete(TABLE_TROOPS, "WHERE uuid='" + uuid + "'");
        this.databaseHandler.delete(TABLE_CONSTRUCTION, "WHERE uuid='" + uuid + "'");
        this.databaseHandler.delete(TABLE_CONTAINER, "WHERE uuid='" + uuid + "'");
    }
}
