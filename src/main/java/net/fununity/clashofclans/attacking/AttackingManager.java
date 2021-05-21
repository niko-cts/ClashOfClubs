package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.buildings.classes.DefenseBuilding;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceContainerBuilding;
import net.fununity.clashofclans.buildings.classes.TroopsBuilding;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.troops.ITroop;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manager class for attacking systems.
 * This class contains all parts for the attacking system.
 * @author Niko
 * @since 0.0.1
 */
public class AttackingManager {

    private static final int MIN_X_COORDINATE = -200000;
    private static final List<AttackingManager> MANAGERS = new ArrayList<>();
    private static final List<Integer> USED_IDS = new ArrayList<>();

    /**
     * Get the attacking manager based on the attacker.
     * @param uuid UUID - the attacker.
     * @return {@link AttackingManager} - instance of this class.
     * @since 0.0.1
     */
    public static AttackingManager getAttackingManager(UUID uuid) {
        return MANAGERS.stream().filter(m -> m.getAttacker().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Get the attacking manager instance from an attacker and defender.
     * This will set up and create the base.
     * @param attacker UUID - the uuid of the attacking player.
     * @param defender UUID - the uuid of the defending player.
     * @return {@link AttackingManager} - the attacking manager instance.
     * @since 0.0.1
     */
    public static AttackingManager getAttackInstance(UUID attacker, UUID defender) {
        int id = 0;
        for (;id<10000;id++) {
            if (!USED_IDS.contains(id))
                break;
        }
        USED_IDS.add(id);

        CoCPlayer attackingCoC = PlayerManager.getInstance().getPlayer(attacker);
        CoCPlayer defense = PlayerManager.getInstance().getPlayer(defender);
        Location playerBase = new Location(ClashOfClans.getInstance().getAttackWorld(), (id * ClashOfClans.getBaseSize() + 200) + MIN_X_COORDINATE, ClashOfClans.getBaseYCoordinate(), 0);
        List<GeneralBuilding> realBuilding = new ArrayList<>(defense.getBuildings());
        List<GeneralBuilding> fakeBuildings = new ArrayList<>();

        for (GeneralBuilding generalBuilding : realBuilding) {
            Location oldLoc = generalBuilding.getCoordinate();
            Location buildingLoc = playerBase.clone().add(new Location(ClashOfClans.getInstance().getPlayWorld(), oldLoc.getBlockX() - defense.getLocation().getBlockX(), 0, oldLoc.getBlockZ() - defense.getLocation().getBlockZ()));

            if (generalBuilding instanceof ResourceContainerBuilding)
               fakeBuildings.add(new ResourceContainerBuilding(generalBuilding.getUuid(), generalBuilding.getBuilding(), buildingLoc, generalBuilding.getRotation(), generalBuilding.getLevel(), ((ResourceContainerBuilding) generalBuilding).getAmount()));
            else if(generalBuilding instanceof TroopsBuilding)
                fakeBuildings.add(new TroopsBuilding(generalBuilding.getUuid(), generalBuilding.getBuilding(), buildingLoc, generalBuilding.getRotation(), generalBuilding.getLevel(), ((TroopsBuilding) generalBuilding).getTroopAmount()));
            else
                fakeBuildings.add(new GeneralBuilding(generalBuilding.getUuid(), generalBuilding.getBuilding(), buildingLoc, generalBuilding.getRotation(), generalBuilding.getLevel()));
        }

        Schematics.createPlayerBase(playerBase);
        for (GeneralBuilding building : fakeBuildings)
            Schematics.createBuilding(building);
        AttackingManager attackingManager = new AttackingManager(playerBase, attacker, attackingCoC.getTroops(), new ArrayList<>(defense.getBuildings()));
        MANAGERS.add(attackingManager);
        return attackingManager;
    }


    private final Location base;
    private final UUID attacker;
    private final Map<ITroop, Integer> inventoryTroops;
    private final List<Troop> troopsOnField;
    private final List<GeneralBuilding> buildingsOnField;
    private final DefenseBuildingsAttacker defenseBuildingsAttacker;

    /**
     * Instantiates the class.
     * @param base Location - the base location.
     * @param attacker UUID - the uuid of the attacker.
     * @param inventoryTroops Map<ITroop, Integer> - the possible troops the player can plant.
     * @param buildingsOnField List<GeneralBuilding> - the buildings on the base.
     * @since 0.0.1
     */
    private AttackingManager(Location base, UUID attacker, Map<ITroop, Integer> inventoryTroops, List<GeneralBuilding> buildingsOnField) {
        this.base = base;
        this.attacker = attacker;
        this.buildingsOnField = buildingsOnField;
        this.inventoryTroops = inventoryTroops;
        this.defenseBuildingsAttacker = new DefenseBuildingsAttacker(this, buildingsOnField.stream().filter(b -> b instanceof DefenseBuilding).map(b -> (DefenseBuilding) b).collect(Collectors.toList()));
        this.troopsOnField = new ArrayList<>();
    }

    /**
     * Spawns an entity on the location.
     * @param troop {@link ITroop} - the troop to spawn.
     * @param spawnLocation Location - the location to spawn.
     * @since 0.0.1
     */
    public void spawnEntity(ITroop troop, Location spawnLocation) {
        int amount = inventoryTroops.get(troop);
        if (amount > 1)
           inventoryTroops.put(troop, amount - 1);
        else
            inventoryTroops.remove(troop);
        troopsOnField.add(new Troop(this, troop, spawnLocation));
    }

    /**
     * Will be called, when a troop attacks a building.
     * @param troop {@link Troop} - the troop, which attacks the building.
     * @param attackBuilding {@link GeneralBuilding} - the building.
     * @since 0.0.1
     */
    public void attackBuilding(Troop troop, GeneralBuilding attackBuilding) {
        attackBuilding.setCurrentHP(attackBuilding.getCurrentHP() - troop.getTroop().getDamage());
        System.out.println(troop.getTroop() + " attacks " + attackBuilding.getCoordinate() + " hp: " + attackBuilding.getCurrentHP());
        // todo particle
        if (attackBuilding.getCurrentHP() <= 0) {
            System.out.println(troop.getTroop() + " destroyed building. ");
            buildingsOnField.remove(attackBuilding);
            if (attackBuilding instanceof DefenseBuilding)
                defenseBuildingsAttacker.removeBuilding((DefenseBuilding) attackBuilding);

            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClans.getInstance(), () ->
                    Schematics.removeBuilding(attackBuilding.getCoordinate(), attackBuilding.getBuilding().getSize()));
        }
    }

    public Location getBase() {
        return base;
    }

    public UUID getAttacker() {
        return attacker;
    }

    public Map<ITroop, Integer> getInventoryTroops() {
        return new HashMap<>(inventoryTroops);
    }

    public List<Troop> getTroopsOnField() {
        return troopsOnField;
    }

    public List<GeneralBuilding> getBuildingsOnField() {
        return new ArrayList<>(buildingsOnField);
    }
}
