package net.fununity.clashofclans.listener.interact;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.attacking.MatchmakingSystem;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.BuildingsMoveManager;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.resource.ResourceGatherBuilding;
import net.fununity.clashofclans.gui.AttackHistoryGUI;
import net.fununity.clashofclans.gui.BuildingBuyGUI;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.TutorialManager;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.clashofclans.util.HotbarItems;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.actionbar.ActionbarMessage;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerSelectHotbarListener implements Listener {

    private static final List<Material> WHITELIST_MATERIALS =
            Arrays.asList(HotbarItems.CANCEL, HotbarItems.CREATE_BUILDING, HotbarItems.MOVE_BUILDING, HotbarItems.ROTATE_BUILDING,
                    HotbarItems.SHOP, HotbarItems.POINTER, HotbarItems.TUTORIAL_BOOK,
                    HotbarItems.START_ATTACK, HotbarItems.ATTACK_HISTORY);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Material handMaterial = event.getPlayer().getInventory().getItemInMainHand().getType();
        if (handMaterial == Material.AIR) return;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) return;
        if (!WHITELIST_MATERIALS.contains(handMaterial) && Arrays.stream(ResourceTypes.values()).noneMatch(r -> r.getRepresentativeMaterial() == handMaterial))
            return;

        UUID uuid = event.getPlayer().getUniqueId();

        if (MatchmakingSystem.getInstance().isSpying(uuid)) {
            if (handMaterial == HotbarItems.CANCEL) {
                MatchmakingSystem.getInstance().cancelWatching(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
            } else if (event.getPlayer().getInventory().getHeldItemSlot() == 0) {
                MatchmakingSystem.getInstance().startAttack(uuid);
            } else if (event.getPlayer().getInventory().getHeldItemSlot() == 1) {
                MatchmakingSystem.getInstance().nextSpy(uuid);
            }
            return;
        }

        CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(uuid);

        if (player == null) return;

        ResourceTypes resourceType = Arrays.stream(ResourceTypes.values()).filter(r -> r.getRepresentativeMaterial() == handMaterial).findFirst().orElse(null);

        if (resourceType != null) {
            for (ResourceGatherBuilding building : player.getResourceGatherBuildings(resourceType)) {
                if (building.getAmount() > 0)
                    building.emptyGatherer();
            }
            return;
        }

        if (handMaterial == HotbarItems.CANCEL) {
            BuildingsMoveManager.getInstance().quitEditorMode(player);
        } else if (handMaterial == HotbarItems.ROTATE_BUILDING) {
            BuildingLocationUtil.removeBuildingGround(event.getPlayer(), player.getBuildingMode());
            int rotate = (byte) player.getBuildingMode()[2];
            player.setBuildingMode(player.getBuildingMode()[0], player.getBuildingMode()[1], rotate == 3 ? (byte) 0 : (byte) (rotate + 1));
            BuildingLocationUtil.createFakeGround(event.getPlayer(), player);

        } else if (handMaterial == HotbarItems.CREATE_BUILDING || handMaterial == HotbarItems.MOVE_BUILDING) {
            if (BuildingLocationUtil.otherBuildingInWay(player)) {
                FunUnityAPI.getInstance().getActionbarManager().addActionbar(uuid, new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_BUILDINGINWAY));
                return;
            }

            if (player.getBuildingMode()[0] != null)
                BuildingLocationUtil.removeBuildingGround(event.getPlayer(), player.getBuildingMode());

            if (handMaterial == HotbarItems.MOVE_BUILDING) {
                BuildingsMoveManager.getInstance().moveBuilding(player.getBuildingMode());
                FunUnityAPI.getInstance().getActionbarManager().addActionbar(uuid, new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_MOVED));
            } else {
                BuildingsManager.getInstance().build(player);
                FunUnityAPI.getInstance().getActionbarManager().addActionbar(uuid, new ActionbarMessage(TranslationKeys.COC_CONSTRUCTION_BUILD));
            }
            BuildingsMoveManager.getInstance().quitEditorMode(player);
        } else if (handMaterial == HotbarItems.SHOP) {
            BuildingBuyGUI.open(player);
        } else if (handMaterial == HotbarItems.POINTER) {
            Block targetBlock = LocationUtil.getTargetBlock(event.getPlayer(), 35);
            if (targetBlock != null)
                ClashOfClubs.getInstance().getPlayerManager().clickBlock(player, targetBlock);
        } else if (handMaterial == HotbarItems.TUTORIAL_BOOK) {
            TutorialManager.getInstance().openHelpBook(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
        } else if (handMaterial == HotbarItems.START_ATTACK) {
            MatchmakingSystem.getInstance().startMatchmakingLooking(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
        } else if (handMaterial == HotbarItems.ATTACK_HISTORY) {
            AttackHistoryGUI.openHistory(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
        }
    }


}
