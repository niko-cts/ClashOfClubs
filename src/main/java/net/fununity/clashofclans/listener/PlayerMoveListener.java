package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class PlayerMoveListener implements Listener {

    private static final int Y_COORD = 51;
    private static PlayerMoveListener instance;
    private final Map<UUID, Location> targetedLocation;

    public PlayerMoveListener() {
        instance = this;
        this.targetedLocation = new HashMap<>();
    }

    public static PlayerMoveListener getInstance() {
        return instance;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null || event.getTo().getYaw() < 25) return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.PISTON) &&
                !event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.NETHER_STAR)) return;
        Player player = event.getPlayer();
        Block targetBlock = LocationUtil.getTargetBlock(player, 3);
        if (targetBlock == null || targetBlock.getLocation().getBlockY() != Y_COORD) return;
        Location lastLoc = targetedLocation.getOrDefault(player.getUniqueId(), null);
        if (targetBlock.getLocation().equals(lastLoc)) return;


        int[] size;
        GeneralBuilding building = BuildingsManager.getInstance().getBuildingMoves().getOrDefault(player.getUniqueId(), null);
        if (building != null)
            size = building.getBuilding().getSize();
        else {
            size = BuildingsManager.getInstance().getCreateBuilding().get(player.getUniqueId()).getSize();
        }

        Location location = targetBlock.getLocation();
        List<Location> blocks = new ArrayList<>();

        CoCPlayer coCPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId());

        if (lastLoc != null)
            removeBlocks(player, size);

        boolean clear = true;
        for (int x = location.getBlockX(); x <= location.getBlockX() + size[0]; x++) {
            for (int z = location.getBlockZ(); z <= location.getBlockZ() + size[1]; z++) {
                Location block = new Location(location.getWorld(), x, Y_COORD, z);
                blocks.add(block);
                if (clear) {
                    GeneralBuilding generalBuilding = coCPlayer.getBuildings().stream().filter(b -> LocationUtil.isBetween(b.getCoordinate(), block,
                            b.getCoordinate().clone().add(b.getBuilding().getSize()[0], Y_COORD, b.getBuilding().getSize()[1]))).findFirst().orElse(null);
                    if (building == null || generalBuilding != null && generalBuilding.getBuilding() != building.getBuilding())
                        clear = false;
                }
            }
        }

        BlockData data = clear ? Material.EMERALD_BLOCK.createBlockData() : Material.REDSTONE_BLOCK.createBlockData();
        blocks.forEach(b -> player.sendBlockChange(b, data));
        targetedLocation.put(player.getUniqueId(), location);
    }

    public void removeBlocks(Player player, int[] size) {
        Location lastLoc = targetedLocation.getOrDefault(player.getUniqueId(), null);
        if (lastLoc != null) {
            for (int x = lastLoc.getBlockX(); x <= lastLoc.getBlockX() + size[0]; x++) {
                for (int z = lastLoc.getBlockZ(); z <= lastLoc.getBlockZ() + size[1]; z++) {
                    Location block = new Location(ClashOfClans.getInstance().getPlayWorld(), x, Y_COORD, z);
                    player.sendBlockChange(block, block.getBlock().getBlockData());
                }
            }
        }
    }
}
