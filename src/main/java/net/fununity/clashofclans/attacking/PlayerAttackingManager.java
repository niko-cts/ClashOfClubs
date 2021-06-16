package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.attacking.history.AttackHistoryDatabase;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.buildings.classes.DefenseBuilding;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.classes.ResourceContainerBuilding;
import net.fununity.clashofclans.buildings.list.Buildings;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.ScoreboardMenu;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.common.util.SpecialChars;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.LocationUtil;
import net.fununity.main.api.util.PlayerDataUtil;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manager class for attacking systems.
 * This class contains all parts for the attacking system.
 * @author Niko
 * @since 0.0.1
 */
public class PlayerAttackingManager {

    private final Location base;
    private final CoCPlayer defender;
    private final CoCPlayer attacker;
    private APIPlayer playerAttacker;
    private final Map<ITroop, Integer> inventoryTroops;
    private final Map<ResourceTypes, Double> resourcesGathered;
    private final List<Troop> troopsOnField;
    private final List<GeneralBuilding> buildingsOnField;
    private int destroyedBuildingsAmount;
    private final DefenseBuildingsAttacker defenseBuildingsAttacker;
    private BukkitTask timer;
    private int secondsLeft;

    /**
     * Instantiates the class.
     * @param base Location - the base location.
     * @param defender CoCPlayer - the uuid of the defender.
     * @param attacker CoCPlayer - the attacker.
     * @param buildingsOnField List<GeneralBuilding> - the buildings on the base.
     * @since 0.0.1
     */
    PlayerAttackingManager(Location base, CoCPlayer defender, CoCPlayer attacker, List<GeneralBuilding> buildingsOnField) {
        this.base = base;
        this.defender = defender;
        this.resourcesGathered = new EnumMap<>(ResourceTypes.class);
        for (ResourceTypes type : ResourceTypes.values())
            resourcesGathered.put(type, 0.0);
        this.attacker = attacker;
        this.buildingsOnField = buildingsOnField;
        this.inventoryTroops = attacker.getTroops();
        this.defenseBuildingsAttacker = new DefenseBuildingsAttacker(this,
                buildingsOnField.stream().filter(b -> b instanceof DefenseBuilding).map(b -> (DefenseBuilding) b).collect(Collectors.toList()));
        this.troopsOnField = new ArrayList<>();
        this.secondsLeft = 180;
    }

    /**
     * Spawns an entity on the location.
     * @param troop {@link ITroop} - the troop to spawn.
     * @param spawnLocation Location - the location to spawn.
     * @since 0.0.1
     */
    public void spawnEntity(ITroop troop, Location spawnLocation) {
        if (inventoryTroops.get(troop) > 1)
            inventoryTroops.put(troop, inventoryTroops.get(troop) - 1);
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
        double oldHealth = attackBuilding.getCurrentHP();
        attackBuilding.setCurrentHP(playerAttacker, Math.max(attackBuilding.getCurrentHP() - troop.getTroop().getDamage(), 0));

        // todo particle

        if (attackBuilding instanceof ResourceContainerBuilding) {
            double amount = ((ResourceContainerBuilding) attackBuilding).getAmount();
            double gatheringResource = amount - attackBuilding.getCurrentHP() * amount / oldHealth;
            addResource(((ResourceContainerBuilding) attackBuilding).getContainingResourceType(), gatheringResource);
            ((ResourceContainerBuilding) attackBuilding).setAmount(amount - gatheringResource);
        }

        if (attackBuilding.getCurrentHP() == 0) {
            destroyedBuildingsAmount++;
            buildingsOnField.remove(attackBuilding);
            removeAttackingTroops(attackBuilding);
            if (attackBuilding instanceof DefenseBuilding)
                defenseBuildingsAttacker.removeBuilding((DefenseBuilding) attackBuilding);

            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () ->
                    Schematics.removeBuilding(attackBuilding.getCoordinate(), attackBuilding.getBuilding().getSize()));

            if (buildingsOnField.isEmpty())
                finishAttack();
        }
    }

    /**
     * Adds resource to the cache.
     * @param type {@link ResourceTypes} - type of resource.
     * @param amount double - amount of resource.
     * @since 0.0.1
     */
    private void addResource(ResourceTypes type, double amount) {
        this.resourcesGathered.put(type, this.resourcesGathered.getOrDefault(type, 0.0) + amount);
        ScoreboardMenu.showAttacking(playerAttacker, attacker, this);
    }

    /**
     * Disables attacking mode for all troops attacking this building.
     * @param attackBuilding {@link GeneralBuilding} - the building they attack.
     * @since 0.0.1
     */
    private void removeAttackingTroops(GeneralBuilding attackBuilding) {
        for (Troop troop : getTroopsOnField()) {
            if (attackBuilding.equals(troop.getAttackBuilding())) {
                troop.setAttackBuilding(null);
                troop.setAttack(false);
                troop.setMoving(false);
            }
        }
    }

    /**
     * Finishes the attack.
     * @since 0.0.1
     */
    public void finishAttack() {
        if (timer.isCancelled()) return;

        timer.cancel();
        this.secondsLeft = 0;
        if (playerAttacker != null) {
            playerAttacker.getPlayer().getInventory().clear();
            playerAttacker.getPlayer();
        }

        for (Troop troop : getTroopsOnField())
            troop.die();

        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            int elo = getElo();
            if (playerAttacker != null)
                playerAttacker.sendMessage(TranslationKeys.COC_ATTACK_FINISHED,
                        Arrays.asList("${name}", "${stars}", "${elo}", "${gold}", "${food}"),
                        Arrays.asList(PlayerDataUtil.getPlayerName(defender.getUniqueId()), getStars(), elo + "",
                                Math.round(resourcesGathered.get(ResourceTypes.GOLD)) + "",
                                Math.round(resourcesGathered.get(ResourceTypes.FOOD)) + ""));

            for (Map.Entry<ResourceTypes, Double> entry : this.resourcesGathered.entrySet()) {
                if (entry.getValue() > 0) {
                    attacker.addResource(entry.getKey(), (int) Math.round(entry.getValue() - 0.4));
                    defender.removeResource(entry.getKey(), (int) Math.round(entry.getValue() * AttackingHandler.DEFENDER_RESOURCE_MULTIPLIER - 0.4));
                }
            }

            defender.addElo(defender.getElo() - elo / 2);
            attacker.addElo(attacker.getElo() + elo);

            AttackHistoryDatabase.getInstance().addNewAttack(getAttacker(), getDefender(), OffsetDateTime.now(), getStarsAmount(), elo, this.resourcesGathered);

            AttackingHandler.removeManager(getAttacker());
            Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () -> {
                if (playerAttacker.getPlayer().isOnline())
                    Bukkit.dispatchCommand(playerAttacker.getPlayer(), "home");
            }, 20 * 5);
        });
    }

    /**
     * Get the amount of elo the attacker gains.
     * @return int - the amount of elo.
     * @since 0.0.1
     */
    private int getElo() {
        int stars = getStarsAmount();
        return buildingsOnField.stream().anyMatch(b -> b.getBuilding() == Buildings.TOWN_HALL) || stars > 2 ?
                stars * 5 * destroyedBuildingsAmount / (getBuildingsOnField().size() + destroyedBuildingsAmount) * (attacker.getTownHallLevel() / defender.getTownHallLevel() * attacker.getExp() / defender.getExp()) :
                stars * 5 * destroyedBuildingsAmount / (getBuildingsOnField().size() + destroyedBuildingsAmount) * (attacker.getTownHallLevel() / defender.getTownHallLevel() * attacker.getExp() / defender.getExp());
    }

    /**
     * Gets the amount of seconds left.
     * @return int - seconds left.
     * @since 0.0.1
     */
    public int getSecondsLeft() {
        return secondsLeft;
    }

    /**
     * Get the current amount of stars in string.
     * @return String - the stars
     * @since 0.0.1
     */
    public String getStars() {
        StringBuilder builder = new StringBuilder();
        int stars = getStarsAmount();
        for (int j = 1; j <= 5; j++)
            builder.append(stars >= j ? "§6§l" : "§7§l").append(SpecialChars.STAR);
        return builder.toString();
    }

    /**
     * Get the current amount of stars.
     * @return int - the stars amount 0 - 5
     * @since 0.0.1
     */
    private int getStarsAmount() {
        if (getBuildingsOnField().isEmpty())
            return 5;

        int townHallStar = (int) getBuildingsOnField().stream().filter(b -> b.getBuilding() == Buildings.TOWN_HALL).count() == 0 ? 1 : 0;

        int destroyPercentage = 100 * getBuildingsOnField().size() / (destroyedBuildingsAmount + getBuildingsOnField().size());

        if (destroyPercentage > 75)
            return 3 + townHallStar;
        if (destroyPercentage > 50)
            return 2 + townHallStar;
        if (destroyPercentage > 25)
            return 1 + townHallStar;
        return townHallStar;
    }

    /**
     * Will be called per second.
     * @since 0.0.1
     */
    private void timer() {
        secondsLeft--;
        if (secondsLeft <= 0) {
            finishAttack();
            return;
        }
        playerAttacker.getPlayer().setLevel(secondsLeft);
        playerAttacker.getPlayer().setExp(secondsLeft / 180F);
    }

    /**
     * Will be called, when the player joined.
     * @param apiPlayer APIPlayer - the player.
     * @since 0.0.1
     */
    public void playerJoined(APIPlayer apiPlayer) {
        this.playerAttacker = apiPlayer;

        Player player = apiPlayer.getPlayer();
        player.getInventory().clear();
        List<ItemStack> items = new ArrayList<>();
        for (Map.Entry<ITroop, Integer> entry : inventoryTroops.entrySet())
            items.add(new ItemBuilder(entry.getKey().getRepresentativeItem(), entry.getValue()).setName(entry.getKey().getName(apiPlayer.getLanguage())).craft());

        player.getInventory().setItem(8, new ItemBuilder(Material.BARRIER).setName("§cFinish Attack").craft());
        player.getInventory().addItem(items.toArray(new ItemStack[0]));
        player.setCollidable(false);
        Location teleport = base.clone().add(20, 0, 20);
        teleport.setY(LocationUtil.getBlockHeight(teleport) + 1);
        player.teleport(teleport);

        ScoreboardMenu.showAttacking(playerAttacker, attacker, this);

        timer = Bukkit.getScheduler().runTaskTimer(ClashOfClubs.getInstance(), this::timer, 20L, 20L);
    }

    /**
     * Will be called, when a spectator joins.
     * @param apiPlayer APIPlayer - the player who joins.
     * @since 0.0.1
     */
    public void viewerJoined(APIPlayer apiPlayer) {
        Location teleport = base.clone().add(20, 0, 20);
        teleport.setY(LocationUtil.getBlockHeight(teleport) + 1);
        Player player = apiPlayer.getPlayer();
        player.teleport(teleport);
        player.setCollidable(false);
        playerAttacker.getPlayer().hidePlayer(ClashOfClubs.getInstance(), player);
    }

    /**
     * Removes a troop from the table.
     * @param troop {@link Troop} - the troop
     * @since 0.0.1
     */
    public void removeTroop(Troop troop) {
        troop.die();
        this.troopsOnField.remove(troop);
        if (troopsOnField.isEmpty() && this.inventoryTroops.isEmpty())
            finishAttack();
    }

    /**
     * Get a copied list of all troops on the field.
     * @return List<Troop> - List of all troop entities on field.
     * @since 0.0.1
     */
    public List<Troop> getTroopsOnField() {
        return new ArrayList<>(troopsOnField);
    }

    /**
     * Get a copied list of all buildings on the field.
     * @return List<GeneralBuilding> - all buildings.
     * @since 0.0.1
     */
    public List<GeneralBuilding> getBuildingsOnField() {
        return new ArrayList<>(buildingsOnField);
    }

    /**
     * Get the uuid of the defender.
     * @return UUID - uuid of the defender.
     * @since 0.0.1
     */
    public UUID getDefender() {
        return defender.getUniqueId();
    }

    /**
     * Get the uuid of the attacker.
     * @return UUID - uuid of the attacker.
     * @since 0.0.1
     */
    public UUID getAttacker() {
        return attacker.getUniqueId();
    }
}
