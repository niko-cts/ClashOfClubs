package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.BuildingModeManager;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.clashofclans.util.HotbarItems;
import org.bukkit.Location;
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

        if (!player.getInventory().contains(HotbarItems.MOVE_BUILDING) && !player.getInventory().contains(HotbarItems.CREATE_BUILDING)) return;

        CoCPlayer coCPlayer = ClashOfClubs.getInstance().getPlayerManager().getPlayer(player);
        if (coCPlayer == null)
            return;

        Location lastLoc = coCPlayer.getBuildingMode().getLocation();
        if (lastLoc == null) {
            BuildingModeManager.getInstance().quitEditorMode(coCPlayer);
        }

        Location groundLocation = event.getTo().clone();
        groundLocation.setY(ClashOfClubs.getBaseYCoordinate() + 1);

        if (groundLocation.equals(lastLoc) || lastLoc == null)
            return;

        BuildingLocationUtil.removeBuildingModeDecorations(player, coCPlayer.getBuildingMode());
        coCPlayer.getBuildingMode().setLocation(groundLocation);
        BuildingLocationUtil.createBuildingModeDecoration(player, coCPlayer);
    }
}
