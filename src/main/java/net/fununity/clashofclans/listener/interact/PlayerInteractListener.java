package net.fununity.clashofclans.listener.interact;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.player.CoCPlayer;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

/**
 * Listener class for interaction.
 *
 * @author Niko
 * @since 0.0.1
 */
public class PlayerInteractListener implements Listener {


    /**
     * Will be called, when a player interacts.
     *
     * @param event PlayerInteractEvent - triggered event.
     * @since 0.0.1
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() == Action.PHYSICAL) return;

        UUID uuid = event.getPlayer().getUniqueId();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block targetBlock = event.getClickedBlock();
            if (targetBlock != null) {
                CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(uuid);
                if (player != null && player.getBuildingMode() == null)
                    ClashOfClubs.getInstance().getPlayerManager().clickBlock(player, targetBlock);
            }
        }
    }

}
