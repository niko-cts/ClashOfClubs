package net.fununity.clashofclans.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getRawSlot() != event.getSlot()) return;
        event.setCancelled(true);
    }

}
