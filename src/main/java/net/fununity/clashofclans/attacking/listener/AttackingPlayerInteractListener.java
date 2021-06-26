package net.fununity.clashofclans.attacking.listener;

import net.fununity.clashofclans.attacking.AttackingHandler;
import net.fununity.clashofclans.attacking.MatchmakingSystem;
import net.fununity.clashofclans.attacking.PlayerAttackingManager;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.troops.Troops;
import net.fununity.main.api.FunUnityAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Listener class for interaction in attacking.
 * @author Niko
 * @since 0.0.1
 */
public class AttackingPlayerInteractListener implements Listener {

    /**
     * Will be called, when a player interacts.
     * @param event PlayerInteractEvent - triggered event.
     * @since 0.0.1
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() == Action.PHYSICAL) return;

        Material handMaterial = event.getPlayer().getInventory().getItemInMainHand().getType();

        PlayerAttackingManager attackingManager = AttackingHandler.getAttackingManager(event.getPlayer().getUniqueId());
        if (attackingManager == null || attackingManager.getSecondsLeft() <= 0) return;

        if (handMaterial == Material.BARRIER) {
            attackingManager.finishAttack();
            return;
        }
        if (event.getClickedBlock() == null)
            return;

        event.setCancelled(true);

        ITroop troop = Troops.getByMaterial(handMaterial);
        if (troop != null) {
            attackingManager.spawnEntity(troop, event.getClickedBlock().getLocation().add(0, 1, 0));
            removeItem(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
        }
    }

    /**
     * Removes an item from the players inventory.
     * @param player Player - the player.
     * @param item ItemStack - the item.
     * @since 0.0.1
     */
    private void removeItem(Player player, ItemStack item) {
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack content = player.getInventory().getContents()[i];
            if (content != null && item.getType() == content.getType()) {
                if (content.getAmount() > 1)
                    content.setAmount(content.getAmount() - 1);
                else
                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
            }
        }
    }
}
