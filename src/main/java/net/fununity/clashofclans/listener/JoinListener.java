package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.player.ScoreboardMenu;
import net.fununity.main.api.event.player.APIPlayerJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener class for joining events.
 * @author Niko
 * @since 0.0.1
 */
public class JoinListener implements Listener {

    /**
     * Will be called, when a player joins.
     * @param event PlayerJoinEvent - the event that was triggered.
     * @since 0.0.1
     */
    @EventHandler
    public void onJoin(APIPlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            CoCPlayer coCPlayer = PlayerManager.getInstance().playerJoins(event.getAPIPlayer());
            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> ScoreboardMenu.show(coCPlayer));
        });
    }

}
