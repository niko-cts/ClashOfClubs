package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.buildings.classes.DefenseBuilding;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceContainerBuilding;
import net.fununity.clashofclans.buildings.classes.TroopsBuilding;
import net.fununity.clashofclans.buildings.list.DecorativeBuildings;
import net.fununity.clashofclans.buildings.list.RandomWorldBuildings;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import org.bukkit.Location;

import java.util.*;

/**
 * Handler class for the {@link PlayerAttackingManager} handling.
 * @author Niko
 * @since 0.0.1
 */
public class AttackingHandler {

    private AttackingHandler() {
        throw new UnsupportedOperationException("AttackingHandler is a handler class and should not be instantiated.");
    }

    private static final double ATTACKING_RESOURCE_MULTIPLIER = 0.35;
    public static final double DEFENDER_RESOURCE_MULTIPLIER = 0.5; // DEFENDER = DEFENDER_RESOURCE_MULTIPLIER * ATTACKING_RESOURCE_MULTIPLIER * REAL_AMOUNT
    private static final int MIN_X_COORDINATE = 0;
    private static final Map<UUID, PlayerAttackingManager> MANAGER_MAP = new HashMap<>();
    private static final List<Integer> USED_IDS = new ArrayList<>();

    /**
     * Get the attacking manager based on the attacker.
     * @param uuid UUID - the attacker.
     * @return {@link PlayerAttackingManager} - instance of this class.
     * @since 0.0.1
     */
    public static PlayerAttackingManager getAttackingManager(UUID uuid) {
        return MANAGER_MAP.getOrDefault(uuid, null);
    }

    /**
     * Gets the attacking manager based on the defender.
     * @param uuid UUID - uuid of the defender.
     * @return {@link PlayerAttackingManager} - the attacking instance.
     * @since 0.0.1
     */
    public static PlayerAttackingManager getAttackingManagerByDefender(UUID uuid) {
        Map.Entry<UUID, PlayerAttackingManager> attackingManagerEntry = MANAGER_MAP.entrySet().stream().filter(e -> e.getValue().getDefender().equals(uuid)).findFirst().orElse(null);
        return attackingManagerEntry != null ? attackingManagerEntry.getValue() : null;
    }

    /**
     * Get the attacking manager instance from an attacker and defender.
     * This will set up and create the base.
     * @param attacker UUID - the uuid of the attacking player.
     * @param defender UUID - the uuid of the defending player.
     * @return {@link PlayerAttackingManager} - the attacking manager instance.
     * @since 0.0.1
     */
    public static PlayerAttackingManager createAttackingInstance(UUID attacker, UUID defender) {
        int id = 0;
        for (; id < 10000; id++) {
            if (!USED_IDS.contains(id))
                break;
        }
        USED_IDS.add(id);

        CoCPlayer attackingCoC = PlayerManager.getInstance().getPlayer(attacker);
        CoCPlayer defense = PlayerManager.getInstance().getPlayer(defender);
        Location oldBase = defense.getLocation();
        Location playerBase = new Location(ClashOfClubs.getInstance().getAttackWorld(), (id * ClashOfClubs.getBaseSize() + 200) + MIN_X_COORDINATE, ClashOfClubs.getBaseYCoordinate(), 0);
        List<GeneralBuilding> realBuilding = new ArrayList<>(defense.getBuildings());
        List<GeneralBuilding> fakeBuildings = new ArrayList<>();

        for (GeneralBuilding generalBuilding : realBuilding) {
            Location oldLoc = generalBuilding.getCoordinate();

            int xDif = oldLoc.getBlockX() - oldBase.getBlockX();
            int zDif = oldLoc.getBlockZ() - oldBase.getBlockZ();

            Location buildingLoc = playerBase.clone().add(new Location(ClashOfClubs.getInstance().getAttackWorld(), xDif, 0, zDif));

            generalBuilding.setCoordinate(buildingLoc);
            generalBuilding.setCoordinate(BuildingLocationUtil.getCoordinate(generalBuilding));

            if (generalBuilding instanceof ResourceContainerBuilding)
                fakeBuildings.add(new ResourceContainerBuilding(generalBuilding.getUuid(), generalBuilding.getBuilding(), buildingLoc, generalBuilding.getRotation(), generalBuilding.getLevel(),
                        ((ResourceContainerBuilding) generalBuilding).getAmount() * ATTACKING_RESOURCE_MULTIPLIER));
            else
                fakeBuildings.add(generalBuilding);
        }

        Schematics.createPlayerBase(playerBase);
        for (GeneralBuilding building : fakeBuildings)
            Schematics.createBuilding(building);
        fakeBuildings.removeIf(b -> b.getBuilding() instanceof DecorativeBuildings || b.getBuilding() instanceof RandomWorldBuildings);

        PlayerAttackingManager attackingManager = new PlayerAttackingManager(playerBase, defense, attackingCoC, fakeBuildings);
        MANAGER_MAP.put(attacker, attackingManager);
        return attackingManager;
    }
}
