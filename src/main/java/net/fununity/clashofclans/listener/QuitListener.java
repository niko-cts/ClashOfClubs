package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.attacking.MatchmakingSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener class for quitting events.
 * @author Niko
 * @since 0.0.1
 */
public class QuitListener implements Listener {

    /**
     * Will be called, when a player quits.
     * @param event PlayerQuitEvent - the event that was triggered.
     * @since 0.0.1
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        MatchmakingSystem.getInstance().removePlayer(event.getPlayer().getUniqueId());
        ClashOfClubs.getInstance().getPlayerManager().playerLeft(event.getPlayer().getUniqueId());
    }

}
