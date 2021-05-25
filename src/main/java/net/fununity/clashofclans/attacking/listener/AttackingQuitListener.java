package net.fununity.clashofclans.attacking.listener;

import net.fununity.clashofclans.attacking.AttackingHandler;
import net.fununity.clashofclans.attacking.PlayerAttackingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener class for quitting events.
 * @author Niko
 * @since 0.0.1
 */
public class AttackingQuitListener implements Listener {

    /**
     * Will be called, when a player quits.
     * @param event PlayerQuitEvent - the event that was triggered.
     * @since 0.0.1
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerAttackingManager attackingManager = AttackingHandler.getAttackingManager(event.getPlayer().getUniqueId());
        if (attackingManager != null)
            attackingManager.finishAttack();
    }

}
