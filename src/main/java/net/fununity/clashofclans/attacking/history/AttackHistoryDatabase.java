package net.fununity.clashofclans.attacking.history;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.misc.databasehandler.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Database class for the attacking system.
 * @author Niko
 * @since 0.0.1
 */
public class AttackHistoryDatabase {

    private static AttackHistoryDatabase instance;

    /**
     * Get the singleton instance of this class.
     * @return {@link AttackHistoryDatabase} - the singleton instance class.
     * @since 0.0.1
     */
    public static AttackHistoryDatabase getInstance() {
        if(instance == null)
            instance = new AttackHistoryDatabase();
        return instance;
    }

    private static final String TABLE = "game_coc_attack_history";
    private static final DecimalFormat FORMAT = new DecimalFormat("0");

    private final DatabaseHandler databaseHandler;

    /**
     * Instantiates the class.
     * @since 0.0.1
     */
    private AttackHistoryDatabase() {
        this.databaseHandler = DatabaseHandler.getInstance();
        if (!this.databaseHandler.doesTableExist(TABLE))
            this.databaseHandler.createTable(TABLE, Arrays.asList("attacker", "defender", "date", "gold", "food", "stars", "elo", "seen"),
                    Arrays.asList("VARCHAR(36) NOT NULL", "VARCHAR(36) NOT NULL", "VARCHAR(36) NOT NULL", "INT NOT NULL", "INT NOT NULL", "INT NOT NULL", "INT NOT NULL", "TINYINT default 0"));
    }

    /**
     * Adds a new history to the database.
     * @param attacker UUID - the uuid of the attacker.
     * @param defender UUID - the uuid of the defender.
     * @param date OffsetDateTime - the date time.
     * @param stars int - the amount of stars.
     * @param elo int - the elo the attacker gained.
     * @param resourcesGathered Map<ResourceTypes, Double> - the resources gathered.
     * @since 0.0.1
     */
    public void addNewAttack(UUID attacker, UUID defender, OffsetDateTime date, int stars, int elo, Map<ResourceTypes, Double> resourcesGathered) {
        this.databaseHandler.insertIntoTable(TABLE,
                Arrays.asList(attacker.toString(), defender.toString(), date.toString(),
                        FORMAT.format(resourcesGathered.get(ResourceTypes.GOLD)), FORMAT.format(resourcesGathered.get(ResourceTypes.FOOD)),
                        stars+"", elo+"", "0"),
                Arrays.asList("string", "string", "string", "", "", "", "", ""));
    }

    /**
     * Update all histories to seen.
     * @param histories List<AttackHistory> - all seen histories.
     * @since 0.0.1
     */
    public void seen(List<AttackHistory> histories) {
        StringBuilder builder = new StringBuilder().append("WHERE ");
        Iterator<AttackHistory> iterator = histories.iterator();
        while (iterator.hasNext()) {
            AttackHistory history = iterator.next();
            builder.append("attacker='").append(history.getAttacker()).append("' AND defender='").append(history.getDefender())
                    .append("' AND date='").append(history.getDate()).append("'");
            if (iterator.hasNext())
                builder.append(" OR ");
        }
        builder.append(" LIMIT ").append(histories.size());
        this.databaseHandler.update(TABLE, Collections.singletonList("seen"), Collections.singletonList("1"), Collections.singletonList(""), builder.toString());
    }

    /**
     * Get all attacks on the base.
     * @param defender UUID - the uuid of the defending base
     * @param seen boolean - display all or only unseen.
     * @return List<AttackHistory> - all attack histories.
     * @since 0.0.1
     */
    public List<AttackHistory> getBaseAttacks(UUID defender, boolean seen) {
        return getData("WHERE defender='" + defender + (seen ? "'" : "' AND seen=0"));
    }

    /**
     * Get all attacks on other bases.
     * @param attacker UUID - the uuid of the attacker
     * @param seen boolean - display all or only unseen.
     * @return List<AttackHistory> - all attack histories.
     * @since 0.0.1
     */
    public List<AttackHistory> getBaseDefends(UUID attacker, boolean seen) {
        return getData("WHERE attacker='" + attacker + (seen ? "'" : "' AND seen=0"));
    }

    /**
     * Get the data of all attack histories.
     * @param where String - the where clause on the select statement.
     * @return List<AttackHistory> - all histories.
     * @since 0.0.1
     */
    private List<AttackHistory> getData(String where) {
        List<AttackHistory> list = new ArrayList<>();
        try (ResultSet set = this.databaseHandler.select(TABLE, Collections.singletonList("*"), where)) {
            while (set != null && set.next()) {
                Map<ResourceTypes, Double> map = new EnumMap<>(ResourceTypes.class);
                map.put(ResourceTypes.GOLD, (double) set.getInt("gold"));
                map.put(ResourceTypes.FOOD, (double) set.getInt("food"));
                list.add(new AttackHistory(UUID.fromString(set.getString("attacker")), UUID.fromString(set.getString("defender")),
                        OffsetDateTime.parse(set.getString("date")), set.getInt("stars"), set.getInt("elo"),
                        map, set.getInt("seen") == 1));
            }
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return list;
    }


}
