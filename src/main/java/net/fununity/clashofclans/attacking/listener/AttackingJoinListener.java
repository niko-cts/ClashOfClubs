package net.fununity.clashofclans.attacking.listener;

import net.fununity.clashofclans.attacking.AttackingHandler;
import net.fununity.clashofclans.attacking.PlayerAttackingManager;
import net.fununity.main.api.event.player.APIPlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener class for joining events.
 * @author Niko
 * @since 0.0.1
 */
public class AttackingJoinListener implements Listener {

    /**
     * Will be called, when a player joins.
     * @param event PlayerJoinEvent - the event that was triggered.
     * @since 0.0.1
     */
    @EventHandler
    public void onJoin(APIPlayerJoinEvent event) {
        PlayerAttackingManager attackingManager = AttackingHandler.getAttackingManager(event.getAPIPlayer().getUniqueId());
        if (attackingManager != null) {
            attackingManager.playerJoined(event.getAPIPlayer());
            return;
        }
        attackingManager = AttackingHandler.getAttackingManagerByDefender(event.getAPIPlayer().getUniqueId());
        if(attackingManager == null) return;
        attackingManager.viewerJoined(event.getAPIPlayer());
    }

}
