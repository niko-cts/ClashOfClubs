package net.fununity.clashofclans.listener.interact;

import net.fununity.clashofclans.commands.CoCCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupInteractListener implements Listener {

    private static final Map<UUID, Location[]> SCHEMATIC_SAVER = new HashMap<>();

    /**
     * Will be called, when a player interacts.
     * @param event PlayerInteractEvent - triggered event.
     * @since 0.0.1
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Material handMaterial = event.getPlayer().getInventory().getItemInMainHand().getType();
        UUID uuid = event.getPlayer().getUniqueId();
        // setup stuff
        if (CoCCommand.getSchematicSetter().contains(uuid)) {
            event.setCancelled(false);
            if (handMaterial == Material.IRON_AXE) {
                if (event.getClickedBlock() == null) return;
                event.setCancelled(true);
                Location[] map = SCHEMATIC_SAVER.getOrDefault(uuid, new Location[2]);
                if (event.getAction() == Action.LEFT_CLICK_BLOCK)
                    map[0] = event.getClickedBlock().getLocation();
                else
                    map[1] = event.getClickedBlock().getLocation();
                SCHEMATIC_SAVER.put(uuid, map);
                event.getPlayer().sendMessage(ChatColor.GREEN + "Saved");
            }
        }
    }

    public static Location[] getSchematicSaver(UUID uuid) {
        return SCHEMATIC_SAVER.get(uuid);
    }
}
