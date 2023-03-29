package net.fununity.clashofclans.database;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.attacking.AttackHistory;
import net.fununity.clashofclans.values.CoCValues;
import net.fununity.clashofclans.values.ICoCValue;
import net.fununity.misc.databasehandler.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Database class for the attacking system.
 * @author Niko
 * @since 0.0.1
 */
public class DatabaseAttackHistory {

    private static DatabaseAttackHistory instance;

    /**
     * Get the singleton instance of this class.
     * @return {@link DatabaseAttackHistory} - the singleton instance class.
     * @since 0.0.1
     */
    public static DatabaseAttackHistory getInstance() {
        if (instance == null)
            instance = new DatabaseAttackHistory();
        return instance;
    }

    private static final String TABLE = "game_coc_attack_history";

    private final DatabaseHandler databaseHandler;

    /**
     * Instantiates the class.
     * @since 0.0.1
     */
    private DatabaseAttackHistory() {
        this.databaseHandler = DatabaseHandler.getInstance();
        if (!this.databaseHandler.doesTableExist(TABLE)) {

            List<String> col = new ArrayList<>(Arrays.asList("attacker", "defender", "date", "stars", "seen"));
            List<String> data = new ArrayList<>(Arrays.asList("VARCHAR(36) NOT NULL", "VARCHAR(36) NOT NULL", "VARCHAR(36) NOT NULL", "INT NOT NULL", "TINYINT default 0"));

            for (ICoCValue resourceTypes : CoCValues.stoleAbleResource()) {
                col.add(resourceTypes.name().toLowerCase());
                data.add("INT NOT NULL DEFAULT 0");
            }

            col.add("");
            data.add("PRIMARY KEY (attacker, defender, date)");

            this.databaseHandler.createTable(TABLE, col, data);
        }
    }

    /**
     * Adds a new history to the database.
     * @param attacker UUID - the uuid of the attacker.
     * @param defender UUID - the uuid of the defender.
     * @param date OffsetDateTime - the date time.
     * @param stars int - the amount of stars.
     * @param resourcesGathered Map<ICoCValue, Double> - the resources gathered.
     * @since 0.0.1
     */
    public void addNewAttack(UUID attacker, UUID defender, OffsetDateTime date, int stars, Map<ICoCValue, Integer> resourcesGathered) {
        List<String> col = new ArrayList<>(Arrays.asList(attacker.toString(), defender.toString(), date.toString(), stars + "","0"));
        List<String> types = new ArrayList<>(Arrays.asList("string", "string", "string", "", ""));
        for (Integer amount : resourcesGathered.values()) {
            col.add(amount + "");
            types.add("");
        }

        this.databaseHandler.insertIntoTable(TABLE, col, types);
    }

    /**
     * Update all histories to see.
     * @param histories List<AttackHistory> - all seen histories.
     * @since 0.0.1
     */
    public void seen(List<AttackHistory> histories) {
        StringBuilder builder = new StringBuilder().append("WHERE ");
        Iterator<AttackHistory> iterator = histories.iterator();
        while (iterator.hasNext()) {
            AttackHistory history = iterator.next();
            builder.append("(attacker='").append(history.getAttacker()).append("' AND defender='").append(history.getDefender())
                    .append("' AND date='").append(history.getDate()).append("')");
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
    public List<AttackHistory> getBaseDefends(UUID defender, boolean seen) {
        return getData("WHERE defender='" + defender + (seen ? "' ORDER BY seen" : "' AND seen=0"));
    }

    /**
     * Get all attacks on other bases.
     * @param attacker UUID - the uuid of the attacker
     * @param seen boolean - display all or only unseen.
     * @return List<AttackHistory> - all attack histories.
     * @since 0.0.1
     */
    public List<AttackHistory> getMadeAttacks(UUID attacker, boolean seen) {
        return getData("WHERE attacker='" + attacker + (seen ? "' ORDER BY seen" : "' AND seen=0"));
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
                Map<ICoCValue, Integer> map = new HashMap<>();
                for (ICoCValue type : CoCValues.stoleAbleResource()) {
                    map.put(type, set.getInt(type.name().toLowerCase()));
                }
                list.add(new AttackHistory(UUID.fromString(set.getString("attacker")), UUID.fromString(set.getString("defender")),
                        OffsetDateTime.parse(set.getString("date")), set.getInt("stars"),
                        map, set.getInt("seen") == 1));
            }
        } catch (SQLException exception) {
            ClashOfClubs.getInstance().getLogger().warning(exception.getMessage());
        }
        return list;
    }

}
