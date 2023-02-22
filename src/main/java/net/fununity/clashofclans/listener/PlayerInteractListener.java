package net.fununity.clashofclans.listener;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.attacking.MatchmakingSystem;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.BuildingsMoveManager;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceGatherBuilding;
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

        UUID uuid = event.getPlayer().getUniqueId();
        Material handMaterial = event.getPlayer().getInventory().getItemInMainHand().getType();

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
                event.getPlayer().sendMessage("§aSaved");
                return;
            }
        }


        if (MatchmakingSystem.getInstance().isSpying(uuid)) {
            if (handMaterial == Material.BARRIER) {
                MatchmakingSystem.getInstance().cancelWatching(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
            } else if (event.getPlayer().getInventory().getHeldItemSlot() == 0) {
                MatchmakingSystem.getInstance().startAttack(uuid);
            } else if (event.getPlayer().getInventory().getHeldItemSlot() == 1) {
                MatchmakingSystem.getInstance().nextSpy(uuid);
            }
            return;
        }

        if (!WHITELIST_MATERIALS.contains(handMaterial)) {
            if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) return;

            CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(uuid);
            if (player == null) return;

            Location location = event.getClickedBlock().getLocation();

            player.getAllBuildings().stream().filter(b -> LocationUtil.isBetween(b.getCoordinate(), location, b.getMaxCoordinate())).findFirst().ifPresent(b ->{
                APIPlayer apiPlayer = player.getOwner();
                if (apiPlayer != null)
                    b.getInventory(apiPlayer.getLanguage()).open(apiPlayer);
            });
            return;
        }

        CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(uuid);
        if (player == null) return;

        ResourceTypes resourceType = Arrays.stream(ResourceTypes.canReachWithTownHall(player.getTownHallLevel())).filter(r -> r.getGlass().getType() == handMaterial).findFirst().orElse(null);

        if (resourceType != null) {
            for (ResourceGatherBuilding building : player.getResourceGatherBuildings(resourceType)) {
                if (building.getAmount() > 0)
                   building.emptyGatherer();
            }
            return;
        }


        Block targetBlock = event.getClickedBlock();

        switch (handMaterial) {
            case BARRIER:
                BuildingsMoveManager.getInstance().quitEditorMode(player);
                return;
            case CLOCK:
                if (player.getTownHallLevel() == 0) {
                    player.getOwner().getTitleSender().sendTitle(TranslationKeys.COC_PLAYER_REPAIR_TOWNHALL_FIRST, 20);
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
                MatchmakingSystem.getInstance().startMatchmakingLooking(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
                return;
            case TRIPWIRE_HOOK:
                if (targetBlock != null)
                    targetBlock = LocationUtil.getTargetBlock(event.getPlayer(), 30);
                break;
            case WRITTEN_BOOK:
                ClashOfClubs.getInstance().getPlayerManager().openHelpBook(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()), player.getTownHallLevel());
                break;
            default:
                break;
        }

        if (targetBlock == null || targetBlock.getLocation().getBlockY() == ClashOfClubs.getBaseYCoordinate() + 1) return;


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
            BuildingsMoveManager.getInstance().moveBuilding(player.getBuildingMode());
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(player.getUniqueId(), new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_MOVED));
        } else if (handMaterial == Material.NETHER_STAR) {
            BuildingsManager.getInstance().build(player);
            FunUnityAPI.getInstance().getActionbarManager().addActionbar(player.getUniqueId(), new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_BUILD));
        }
        BuildingsMoveManager.getInstance().quitEditorMode(player);
    }

    public static Location[] getSchematicSaver(UUID uuid) {
        return SCHEMATIC_SAVER.get(uuid);
    }
}
