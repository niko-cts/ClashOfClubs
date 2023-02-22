package net.fununity.clashofclans.gui;

import net.fununity.clashofclans.buildings.BuildingsMoveManager;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IUpgradeDetails;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.util.BuildingDisplayUtil;
import net.fununity.clashofclans.util.BuildingsAmountUtil;
import net.fununity.main.api.actionbar.ActionbarMessage;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.Utils;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gui class for buying buildings.
 *
 * @author Niko
 * @since 0.0.1
 */
public class BuildingBuyGUI {

    private BuildingBuyGUI() {
        throw new UnsupportedOperationException("BuildingBuyGUI is a utility class.");
    }

    /**
     * Open the main GUI.
     *
     * @param coCPlayer CoCPlayer - the coc player.
     * @since 0.0.1
     */
    public static void open(CoCPlayer coCPlayer) {
        APIPlayer player = coCPlayer.getOwner();
        if (player == null) return;
        Language lang = player.getLanguage();
        CustomInventory menu = new CustomInventory(lang.getTranslation(TranslationKeys.COC_INV_CONSTRUCTION_NAME), 9 * 3);

        for (int slot : new int[]{10, 12, 14, 16}) {
            menu.setItem(slot, new ItemBuilder(BuildingDisplayUtil.getMaterial(slot))
                    .setName(lang.getTranslation(BuildingDisplayUtil.getNameKey(slot)))
                    .setLore(BuildingDisplayUtil.getLore(coCPlayer, slot)).craft(), new ClickAction() {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    openSpecified(player, coCPlayer, BuildingDisplayUtil.getBuildings(slot));
                }
            });
        }

        menu.fill(UsefulItems.BACKGROUND_BLACK);
        menu.open(player);
    }


    /**
     * Open a specified GUI.
     *
     * @param player              APIPlayer - the player to open the gui.
     * @param coCPlayer           CoCPlayer - the coc player.
     * @param displayingBuildings IBuilding[] - the buildings in the gui.
     * @since 0.0.1
     */
    private static void openSpecified(APIPlayer player, CoCPlayer coCPlayer, IBuilding[]... displayingBuildings) {
        player.getPlayer().closeInventory();
        Language lang = player.getLanguage();

        int townHallLevel = coCPlayer.getTownHallLevel();
        List<IBuilding> listedBuildings = new ArrayList<>();
        for (IBuilding[] buildings : displayingBuildings)
            listedBuildings.addAll(Arrays.stream(buildings).filter(b -> b.getBuildingLevelData()[0].getMinTownHall() <= townHallLevel).collect(Collectors.toList()));

        CustomInventory menu = new CustomInventory(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_BUILDING_NAME), Utils.getPerfectInventorySize(listedBuildings.size() + 2));

        menu.setItem(0, UsefulItems.LEFT_ARROW, new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                open(coCPlayer);
            }
        });
        menu.setItem(1, UsefulItems.BACKGROUND_GRAY);

        for (IBuilding building : listedBuildings) {
            int amountOfBuilding = BuildingsAmountUtil.getAmountOfBuilding(building, townHallLevel);
            long buildingsPlayerHas = coCPlayer.getNormalBuildings().stream().filter(b -> b.getBuilding() == building).count();
            List<String> lore = new ArrayList<>(Arrays.asList(lang.getTranslation(building.getDescriptionKey()).split(";")));
            String canStillBuild = amountOfBuilding <= buildingsPlayerHas ? ChatColor.RED + "" : ChatColor.GREEN + "";
            lore.addAll(Arrays.asList(lang.getTranslation(
                    TranslationKeys.COC_GUI_CONSTRUCTION_BUILDING_LORE,
                    Arrays.asList("${cost}", "${type}", "${max}", "${amount}", "${level}"),
                    Arrays.asList(building.getBuildingLevelData()[0].getUpgradeCost() + "", building.getResourceType().getColoredName(lang),
                            canStillBuild + amountOfBuilding, canStillBuild + buildingsPlayerHas, townHallLevel + "")).split(";")));
            if (building instanceof IUpgradeDetails)
                lore.addAll(((IUpgradeDetails) building).getLoreDetails(building.getBuildingLevelData()[0], lang));

            menu.addItem(new ItemBuilder(building.getMaterial()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setName(lang.getTranslation(building.getNameKey())).setLore(lore).craft(), new ClickAction() {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    if (building.getBuildingLevelData()[0].getUpgradeCost() > coCPlayer.getResourceAmount(building.getResourceType())) {
                        apiPlayer.sendActionbar(new ActionbarMessage(TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE), "${type}", building.getResourceType().getColoredName(lang));
                        return;
                    }

                    if (buildingsPlayerHas >= amountOfBuilding) {
                        apiPlayer.sendActionbar(new ActionbarMessage(TranslationKeys.COC_PLAYER_NO_MORE_BUILDINGS), "${max}", amountOfBuilding + "");
                        return;
                    }
                    BuildingsMoveManager.getInstance().enterCreationMode(coCPlayer, building);
                    setCloseInventory(true);
                }
            });
        }

        menu.fill(UsefulItems.BACKGROUND_GRAY);
        menu.open(player);
    }
}
