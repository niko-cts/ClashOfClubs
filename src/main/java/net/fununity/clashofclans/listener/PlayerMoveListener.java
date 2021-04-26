package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null || event.getTo().getYaw() < 20) return;

        if (!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.PISTON) &&
                !event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.NETHER_STAR)) return;

        Player player = event.getPlayer();
        Block targetBlock = LocationUtil.getTargetBlock(player, 3);
        if (targetBlock == null || targetBlock.getLocation().getBlockY() != ClashOfClans.getBaseYCoordinate() + 1) return;
        CoCPlayer coCPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId());

        Location lastLoc = (Location) coCPlayer.getBuildingMode()[0];

        if (targetBlock.getLocation().equals(lastLoc)) return;

        if (lastLoc != null)
            BuildingLocationUtil.removeBuildingGround(player, coCPlayer.getBuildingMode());

        coCPlayer.setBuildingMode(targetBlock.getLocation());
        BuildingLocationUtil.createFakeGround(event.getPlayer(), coCPlayer);
    }
}
