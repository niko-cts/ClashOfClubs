package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.player.PlayerManager;
import org.bukkit.Bukkit;
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
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> PlayerManager.getInstance().playerLeft(event.getPlayer().getUniqueId()));
    }

}
