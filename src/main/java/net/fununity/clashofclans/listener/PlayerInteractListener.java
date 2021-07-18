package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.attacking.MatchmakingSystem;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.commands.CoCCommand;
import net.fununity.clashofclans.gui.AttackHistoryGUI;
import net.fununity.clashofclans.gui.BuildingBuyGUI;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.actionbar.ActionbarMessage;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

/**
 * Listener class for interaction.
 * @author Niko
 * @since 0.0.1
 */
public class PlayerInteractListener implements Listener {

    private static final Map<UUID, Location[]> SCHEMATIC_SAVER = new HashMap<>();
    private static final List<Material> WHITELIST_MATERIALS = Arrays.asList(Material.BARRIER, Material.NETHER_STAR, Material.CLOCK,
            Material.PISTON, Material.STICK, Material.IRON_SWORD, Material.PAPER);

    /**
     * Will be called, when a player interacts.
     * @param event PlayerInteractEvent - triggered event.
     * @since 0.0.1
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() == Action.PHYSICAL) return;

        Material handMaterial = event.getPlayer().getInventory().getItemInMainHand().getType();

        // setup stuff
        if(CoCCommand.getSchematicSetter().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(false);
            if (handMaterial == Material.IRON_AXE) {
                if (event.getClickedBlock() == null) return;
                event.setCancelled(true);
                Location[] map = SCHEMATIC_SAVER.getOrDefault(event.getPlayer().getUniqueId(), new Location[2]);
                if (event.getAction() == Action.LEFT_CLICK_BLOCK)
                    map[0] = event.getClickedBlock().getLocation();
                else
                    map[1] = event.getClickedBlock().getLocation();
                SCHEMATIC_SAVER.put(event.getPlayer().getUniqueId(), map);
                event.getPlayer().sendMessage("§aSaved");
                return;
            }
        }


        if (MatchmakingSystem.getInstance().getAttackWatcher().containsKey(event.getPlayer().getUniqueId())) {
            if (handMaterial == Material.BARRIER) {
                MatchmakingSystem.getInstance().cancelWatching(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
            } else if (event.getPlayer().getInventory().getHeldItemSlot() == 0) {
                MatchmakingSystem.getInstance().startAttack(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
            } else if(event.getPlayer().getInventory().getHeldItemSlot() == 1) {
                MatchmakingSystem.getInstance().startMatchmakingLooking(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()),
                        MatchmakingSystem.getInstance().getVisitedAttacks().get(event.getPlayer().getUniqueId()));
            }
            return;
        }

        if (!WHITELIST_MATERIALS.contains(handMaterial)) {
            if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

            CoCPlayer player = PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
            if (player == null) return;

            GeneralBuilding clickedBuilding = player.getBuildings().stream()
                    .filter(b -> LocationUtil.isBetween(b.getCoordinate(), event.getClickedBlock().getLocation(),
                            b.getMaxCoordinate().add(0, 50, 0))).findFirst().orElse(null);

            if (clickedBuilding != null) {
                APIPlayer apiPlayer = player.getOwner();
                if (apiPlayer != null)
                    clickedBuilding.getInventory(apiPlayer.getLanguage()).open(apiPlayer);
            }
            return;
        }

        CoCPlayer player = PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;

        switch (handMaterial) {
            case BARRIER:
                BuildingsManager.getInstance().quitEditorMode(player);
                return;
            case CLOCK:
                if (player.getTownHallLevel() == 0) {
                    FunUnityAPI.getInstance().getActionbarManager().addActionbar(player.getUniqueId(), new ActionbarMessage(TranslationKeys.COC_PLAYER_REPAIR_TOWNHALL_FIRST));
                    return;
                }
                BuildingBuyGUI.open(player);
                return;
            case STICK:
                BuildingLocationUtil.removeBuildingGround(event.getPlayer(), player.getBuildingMode());
                int rotate = (byte) player.getBuildingMode()[2];
                player.setBuildingMode(player.getBuildingMode()[0], player.getBuildingMode()[1], rotate == 3 ? (byte) 0 : (byte) (rotate + 1));
                BuildingLocationUtil.createFakeGround(event.getPlayer(), player);
                return;
            case PAPER:
                AttackHistoryGUI.openHistory(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
                return;
            case IRON_SWORD:
                MatchmakingSystem.getInstance().startMatchmakingLooking(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()), new ArrayList<>());
                return;
            default:
                break;
        }

        Block targetBlock = event.getClickedBlock();

        if (targetBlock == null) return;

        if (targetBlock.getLocation().getBlockY() != ClashOfClubs.getBaseYCoordinate() + 1) return;

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

    public static Location[] getSchematicSaver(UUID uuid) {
        return SCHEMATIC_SAVER.get(uuid);
    }
}
