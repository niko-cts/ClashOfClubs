package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.commands.CoCCommand;
import net.fununity.clashofclans.gui.BuildingBuyGUI;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.actionbar.ActionbarMessage;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener {

    private static final int Y_COORD = 51;
    private static final Location[] SCHEMATIC_SAVER = new Location[2];

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() == Action.PHYSICAL) return;

        Material handMaterial = event.getPlayer().getInventory().getItemInMainHand().getType();
        if (handMaterial.equals(Material.BARRIER)) {
            BuildingsManager.getInstance().quitEditorMode(event.getPlayer());
            return;
        }

        CoCPlayer player = PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;

        if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
            BuildingBuyGUI.open(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()), player);
            return;
        }

        if(event.getClickedBlock() == null) return;

        GeneralBuilding generalBuilding = player.getBuildings().stream().filter(b -> LocationUtil.isBetween(b.getCoordinate(), event.getClickedBlock().getLocation(),
                b.getCoordinate().clone().add(b.getBuilding().getSize()[0], 20, b.getBuilding().getSize()[1]))).findFirst().orElse(null);

        if (generalBuilding != null) {
            APIPlayer apiPlayer = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer());
            if (apiPlayer != null)
                generalBuilding.getInventory(apiPlayer.getLanguage()).open(apiPlayer);
            return;
        }

        if (handMaterial == Material.PISTON || handMaterial == Material.NETHER_STAR) {
            Block targetBlock = event.getClickedBlock();
            if (targetBlock == null || targetBlock.getLocation().getBlockY() != Y_COORD) return;

            int[] size;
            GeneralBuilding building = BuildingsManager.getInstance().getBuildingMoves().getOrDefault(player.getUniqueId(), null);
            if (building != null)
                size = building.getBuilding().getSize();
            else {
                size = BuildingsManager.getInstance().getCreateBuilding().get(player.getUniqueId()).getSize();
            }

            Location location = targetBlock.getLocation();
            boolean clear = true;
            for (int x = location.getBlockX(); x <= location.getBlockX() + size[0]; x++) {
                for (int z = location.getBlockZ(); z <= location.getBlockZ() + size[1]; z++) {
                    Location block = new Location(location.getWorld(), x, Y_COORD, z);
                    if (clear) {
                        GeneralBuilding buildingInWay = player.getBuildings().stream().filter(b -> LocationUtil.isBetween(b.getCoordinate(), block,
                                b.getCoordinate().clone().add(b.getBuilding().getSize()[0], Y_COORD, b.getBuilding().getSize()[1]))).findFirst().orElse(null);
                        if (building == null || buildingInWay != null && buildingInWay.getBuilding() != building.getBuilding()) {
                            clear = false;
                            break;
                        }
                    }
                }
            }

            APIPlayer apiPlayer = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer());

            if (!clear) {
                apiPlayer.sendActionbar(new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_BUILDINGINWAY));
                return;
            }

            BuildingsManager.getInstance().quitEditorMode(event.getPlayer());
            if (handMaterial == Material.PISTON && building != null) {
                BuildingsManager.getInstance().moveBuilding(building, location.clone().subtract(0, 1, 0));
                apiPlayer.sendActionbar(new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_MOVED));
            } else if (handMaterial == Material.NETHER_STAR) {
                BuildingsManager.getInstance().build(player, BuildingsManager.getInstance().getCreateBuilding().get(player.getUniqueId()), location.clone().subtract(0, 1, 0));
                apiPlayer.sendActionbar(new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_BUILD));
            }
        }


        if (!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) || !CoCCommand.getSchematicSetter().contains(event.getPlayer().getUniqueId()))
            return;
        event.setCancelled(true);
        if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            SCHEMATIC_SAVER[0] = event.getClickedBlock().getLocation();
        else
            SCHEMATIC_SAVER[1] = event.getClickedBlock().getLocation();
        event.getPlayer().sendMessage("§aSaved");
    }

    public static Location[] getSchematicSaver() {
        return SCHEMATIC_SAVER;
    }
}
