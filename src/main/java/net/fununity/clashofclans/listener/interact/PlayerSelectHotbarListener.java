package net.fununity.clashofclans.listener.interact;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.attacking.spying.SpyingManager;
import net.fununity.clashofclans.buildings.BuildingModeManager;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.gui.AttackHistoryGUI;
import net.fununity.clashofclans.gui.AttackLookingGUI;
import net.fununity.clashofclans.gui.BuildingBuyGUI;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.TutorialManager;
import net.fununity.clashofclans.player.buildingmode.BuildingData;
import net.fununity.clashofclans.player.buildingmode.ConstructionMode;
import net.fununity.clashofclans.player.buildingmode.IBuildingMode;
import net.fununity.clashofclans.player.buildingmode.MovingMode;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.clashofclans.util.HotbarItems;
import net.fununity.clashofclans.values.ResourceTypes;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.messages.MessagePrefix;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.Location;
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
            Arrays.asList(HotbarItems.CANCEL, HotbarItems.CREATE_BUILDING, HotbarItems.MOVE_BUILDING, HotbarItems.ROTATE_BUILDING, HotbarItems.CREATE_BUILDING_ANOTHER, HotbarItems.CREATE_BUILDING_REMOVE,
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

        if (SpyingManager.getInstance().isSpying(uuid)) {
            if (handMaterial == HotbarItems.CANCEL) {
                SpyingManager.getInstance().cancelWatching(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
            } else if (event.getPlayer().getInventory().getHeldItemSlot() == 0) {
                SpyingManager.getInstance().startAttack(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
            } else if (event.getPlayer().getInventory().getHeldItemSlot() == 1) {
                SpyingManager.getInstance().nextSpy(uuid);
            }
            return;
        }

        CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(uuid);

        if (player == null) return;

        ResourceTypes resourceType = Arrays.stream(ResourceTypes.values()).filter(r -> r.getRepresentativeMaterial() == handMaterial).findFirst().orElse(null);

        if (resourceType != null) {
            BuildingsManager.getInstance().emptyGatherer(player.getResourceGatherBuildings(resourceType));
            return;
        }

        if (handMaterial == HotbarItems.CANCEL) {
            BuildingModeManager.getInstance().quitEditorMode(player);
        } else if (handMaterial == HotbarItems.ROTATE_BUILDING) {
            BuildingLocationUtil.removeBuildingModeDecorations(event.getPlayer(), player.getBuildingMode());
            int rotate = player.getBuildingMode().getRotation();
            player.getBuildingMode().setRotation(rotate == 3 ? (byte) 0 : (byte) (rotate + 1));
            BuildingLocationUtil.createBuildingModeDecoration(event.getPlayer(), player);
        } else if (handMaterial == HotbarItems.CREATE_BUILDING_ANOTHER) {
            IBuildingMode buildingMode = player.getBuildingMode();
            APIPlayer apiPlayer = player.getOwner();
            if (BuildingLocationUtil.otherBuildingInWay(player, BuildingLocationUtil.getAllLocationsOnGround(buildingMode))) {
                apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_CONSTRUCTION_BUILDINGINWAY);
                return;
            }

            if (BuildingsManager.getInstance().checkIfPlayerCanBuildAnotherBuilding(player, player.getOwner(), buildingMode.getBuilding(),
                    ((ConstructionMode) buildingMode).getBuildings().size() + 1)) {
                BuildingLocationUtil.removeBuildingModeDecorations(event.getPlayer(), buildingMode);


                byte rotation = player.getBuildingMode().getRotation();
                Location newLocation = player.getBuildingMode().getLocation().clone().add(0, 0, 2);

                List<BuildingData> buildings = ((ConstructionMode) player.getBuildingMode()).getBuildings();
                buildings.add(new BuildingData(UUID.randomUUID(), newLocation, rotation));

                BuildingLocationUtil.createBuildingModeDecoration(event.getPlayer(), player);
            }
        } else if (handMaterial == HotbarItems.CREATE_BUILDING_REMOVE) {

            BuildingLocationUtil.removeBuildingModeDecorations(event.getPlayer(), player.getBuildingMode());

            List<BuildingData> buildings = ((ConstructionMode) player.getBuildingMode()).getBuildings();

            if (buildings.size() > 1)
                buildings.remove(buildings.size() - 1);

            BuildingLocationUtil.createBuildingModeDecoration(event.getPlayer(), player);
        } else if (handMaterial == HotbarItems.CREATE_BUILDING || handMaterial == HotbarItems.MOVE_BUILDING) {

            APIPlayer apiPlayer = player.getOwner();

            // check if any building is in way
            if (BuildingLocationUtil.otherBuildingInWay(player, BuildingLocationUtil.getAllLocationsOnGround(player.getBuildingMode()))) {
                apiPlayer.sendMessage(MessagePrefix.ERROR, TranslationKeys.COC_CONSTRUCTION_BUILDINGINWAY);
                return;
            }

            BuildingLocationUtil.removeBuildingModeDecorations(event.getPlayer(), player.getBuildingMode());

            if (player.getBuildingMode() instanceof MovingMode) {
                BuildingModeManager.getInstance().moveBuilding(player);
                apiPlayer.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_CONSTRUCTION_MOVED);
            } else {
                BuildingsManager.getInstance().build(player);
                apiPlayer.sendMessage(MessagePrefix.SUCCESS, TranslationKeys.COC_CONSTRUCTION_BUILD);
            }
            BuildingModeManager.getInstance().quitEditorMode(player);
        } else if (handMaterial == HotbarItems.SHOP) {
            BuildingBuyGUI.open(player);
        } else if (handMaterial == HotbarItems.POINTER) {
            Block targetBlock = LocationUtil.getTargetBlock(event.getPlayer(), 35);
            if (targetBlock != null)
                ClashOfClubs.getInstance().getPlayerManager().clickBlock(player, targetBlock);
        } else if (handMaterial == HotbarItems.TUTORIAL_BOOK) {
            TutorialManager.getInstance().openHelpBook(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
        } else if (handMaterial == HotbarItems.START_ATTACK) {
            AttackLookingGUI.open(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
        } else if (handMaterial == HotbarItems.ATTACK_HISTORY) {
            AttackHistoryGUI.openHistory(FunUnityAPI.getInstance().getPlayerHandler().getPlayer(event.getPlayer()));
        }
    }


}
