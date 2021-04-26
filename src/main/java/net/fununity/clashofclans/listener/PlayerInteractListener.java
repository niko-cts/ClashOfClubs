package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.commands.CoCCommand;
import net.fununity.clashofclans.gui.BuildingBuyGUI;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.util.BuildingLocationUtil;
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

import java.util.Arrays;
import java.util.List;

public class PlayerInteractListener implements Listener {

    private static final Location[] SCHEMATIC_SAVER = new Location[2];
    private static final List<Material> WHITELIST_MATERIALS = Arrays.asList(Material.BARRIER, Material.NETHER_STAR, Material.CLOCK, Material.PISTON, Material.STICK);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() == Action.PHYSICAL) return;
        Material handMaterial = event.getPlayer().getInventory().getItemInMainHand().getType();

        if (handMaterial == Material.IRON_AXE && CoCCommand.getSchematicSetter().contains(event.getPlayer().getUniqueId())) {
            if (event.getClickedBlock() == null) return;
            event.setCancelled(true);
            if (event.getAction() == Action.LEFT_CLICK_BLOCK)
                SCHEMATIC_SAVER[0] = event.getClickedBlock().getLocation();
            else
                SCHEMATIC_SAVER[1] = event.getClickedBlock().getLocation();
            event.getPlayer().sendMessage("§aSaved");
            return;
        }


        if (!WHITELIST_MATERIALS.contains(handMaterial)) {
            if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

            CoCPlayer player = PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
            if (player == null) return;


            GeneralBuilding clickedBuilding = player.getBuildings().stream()
                    .filter(b -> LocationUtil.isBetween(b.getCoordinate(), event.getClickedBlock().getLocation(),
                    b.getCoordinate().clone().add(b.getBuilding().getSize()[0], event.getClickedBlock().getLocation().getBlockY()+1, b.getBuilding().getSize()[1]))).findFirst().orElse(null);

            if (clickedBuilding != null) {
                APIPlayer apiPlayer = player.getOwner();
                if (apiPlayer != null)
                    clickedBuilding.getInventory(apiPlayer.getLanguage()).open(apiPlayer);
            }
            return;
        }

        CoCPlayer player = PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;

        if (handMaterial.equals(Material.BARRIER)) {
            BuildingsManager.getInstance().quitEditorMode(player);
            return;
        }

        if (handMaterial.equals(Material.CLOCK)) {
            if (player.getTownHallLevel() == 0) {
                FunUnityAPI.getInstance().getActionbarManager().addActionbar(player.getUniqueId(), new ActionbarMessage(TranslationKeys.COC_PLAYER_REPAIR_TOWNHALL_FIRST));
                return;
            }
            BuildingBuyGUI.open(player);
            return;
        }

        if (handMaterial.equals(Material.STICK)) {
            BuildingLocationUtil.removeBuildingGround(event.getPlayer(), player.getBuildingMode());
            int rotate = (byte) player.getBuildingMode()[2];
            player.setBuildingMode(player.getBuildingMode()[0], player.getBuildingMode()[1], rotate == 3 ? (byte) 0 : (byte) (rotate + 1));
            BuildingLocationUtil.createFakeGround(event.getPlayer(), player);
            return;
        }

        Block targetBlock = event.getClickedBlock();

        if (targetBlock == null) return;

        if (targetBlock.getLocation().getBlockY() != ClashOfClans.getBaseYCoordinate() + 1) return;

        GeneralBuilding building = player.getBuildingMode()[1] instanceof GeneralBuilding ? (GeneralBuilding) player.getBuildingMode()[1] : null;

        if (player.getBuildingMode()[0] != null)
            BuildingLocationUtil.removeBuildingGround(event.getPlayer(), player.getBuildingMode());

        player.setBuildingMode(targetBlock.getLocation());

        if (BuildingLocationUtil.otherBuildingInWay(player)) {
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(player.getUniqueId(), new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_BUILDINGINWAY));
            return;
        }

        player.setBuildingMode(targetBlock.getLocation().clone().subtract(0, 1, 0));
        if (handMaterial == Material.PISTON && building != null) {
            BuildingsManager.getInstance().moveBuilding(player.getBuildingMode());
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(player.getUniqueId(), new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_MOVED));
        } else if (handMaterial == Material.NETHER_STAR) {
            BuildingsManager.getInstance().build(player);
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(player.getUniqueId(), new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_BUILD));
        }
        BuildingsManager.getInstance().quitEditorMode(player);
    }

    public static Location[] getSchematicSaver() {
        return SCHEMATIC_SAVER;
    }
}
