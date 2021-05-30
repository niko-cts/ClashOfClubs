package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.attacking.MatchmakingSystem;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

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
        UUID uuid = event.getPlayer().getUniqueId();
        CoCPlayer player = PlayerManager.getInstance().getPlayer(uuid);
        APIPlayer apiPlayer = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(uuid);
        if (MatchmakingSystem.getInstance().getAttackWatcher().containsKey(uuid) || !player.getVisitors().contains(apiPlayer)) {
            player.visit(apiPlayer, true);
            MatchmakingSystem.getInstance().getVisitedAttacks().remove(uuid);
            MatchmakingSystem.getInstance().getAttackWatcher().remove(uuid);
        }
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> PlayerManager.getInstance().playerLeft(uuid));
    }

}
