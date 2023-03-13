package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.database.DatabaseBuildings;
import net.fununity.clashofclans.database.DatabasePlayer;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.cloud.client.spigot.event.ShutdownByCloudEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.logging.Level;

public class ServerShutdownListener implements Listener {

    @EventHandler
    public void onServer(ShutdownByCloudEvent event) {
        Collection<CoCPlayer> players = ClashOfClubs.getInstance().getPlayerManager().getPlayers().values();
        if (!players.isEmpty()) {
            ClashOfClubs.getInstance().getLogger().log(Level.INFO, "Saving data of {0} players", players.size());
            DatabasePlayer.getInstance().updatePlayer(players);
            DatabaseBuildings.getInstance().updateBuildings(players);
        }
    }

}
