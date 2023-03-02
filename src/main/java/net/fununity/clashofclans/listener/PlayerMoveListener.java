package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * A class for listening to spigot MoveEvents.
 * This will be used for creating and moving buildings.
 * @author Niko
 * @since 0.0.2
 */
public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null ||
                (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ())) return;

        Player player = event.getPlayer();

        if (!player.getInventory().contains(Material.PISTON) && !player.getInventory().contains(Material.NETHER_STAR)) return;

        Location groundLocation = event.getTo().clone();
        groundLocation.setY(ClashOfClubs.getBaseYCoordinate() + 1);
        
        CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(player);
        Location lastLoc = (Location) coCPlayer.getBuildingMode()[0];

        if (groundLocation.equals(lastLoc) || lastLoc == null)
            return;


        BuildingLocationUtil.removeBuildingModeDecorations(player, coCPlayer.getBuildingMode());
        coCPlayer.setBuildingMode(groundLocation);
        BuildingLocationUtil.createBuildingModeDecoration(player, coCPlayer);
    }
}
