package net.fununity.clashofclans.attacking;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.main.api.common.util.SpecialChars;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomLookaround;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;

public class Troop extends EntityCreature {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");

    private final PlayerAttackingManager attackingManager;
    private final ITroop troop;
    private float health;
    private boolean moving;
    private boolean attack;
    private final BukkitTask attackTimer;

    private GeneralBuilding attackBuilding;

    public Troop(PlayerAttackingManager attackingManager, ITroop troop, Location location) {
        super(troop.getEntityType(), ((CraftWorld)location.getWorld()).getHandle());
        this.attackingManager = attackingManager;
        this.troop = troop;
        this.moving = true;
        this.attack = false;

        this.setInvulnerable(true);
        this.setCustomNameVisible(true);
        this.setHealth(troop.getMaxHP());

        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

        Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () -> moving = false, 5L);
        this.attackTimer = Bukkit.getScheduler().runTaskTimer(ClashOfClubs.getInstance(), this::attackTimer, 20L, 20L);
    }

    private void attackTimer() {
        if (moving || !attack) return;
        if (!attackingManager.getBuildingsOnField().contains(attackBuilding)) {
            setAttackBuilding(null);
            setAttack(false);
            return;
        }
        attackingManager.attackBuilding(this, attackBuilding);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalMoveToNextBuilding(this));
        this.goalSelector.a(1, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    public void setHealth(float health) {
        this.health = health;
        if (health >= 0)
            this.setCustomName(new ChatComponentText(ChatColor.RED + FORMAT.format(health) + SpecialChars.HEART));
    }

    @Override
    public void die() {
        super.die();
        this.attackTimer.cancel();
    }

    @Override
    public String toString() {
        return "Troop{" +
                "troop=" + troop +
                ", health=" + health +
                ", moving=" + moving +
                ", attack=" + attack +
                '}';
    }

    @Override
    public float getHealth() {
        return health;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setAttack(boolean attack) {
        this.attack = attack;
    }

    public boolean isAttack() {
        return attack;
    }

    public GeneralBuilding getAttackBuilding() {
        return attackBuilding;
    }

    public void setAttackBuilding(GeneralBuilding attackBuilding) {
        this.attackBuilding = attackBuilding;
    }

    public ITroop getTroop() {
        return troop;
    }

    public PlayerAttackingManager getAttackingManager() {
        return attackingManager;
    }
}
