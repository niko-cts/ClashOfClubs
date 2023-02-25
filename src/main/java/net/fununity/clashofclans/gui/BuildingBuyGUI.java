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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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
                    .setLore(BuildingDisplayUtil.getLore(coCPlayer, slot))
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES).craft(), new ClickAction() {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    openSpecified(player, coCPlayer, BuildingDisplayUtil.getBuildingsForBuyGUI(slot));
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
     * @param allDisplayingBuildings IBuilding[][] - the buildings in the gui.
     * @since 0.0.1
     */
    private static void openSpecified(APIPlayer player, CoCPlayer coCPlayer, IBuilding[][] allDisplayingBuildings) {
        player.getPlayer().closeInventory();
        Language lang = player.getLanguage();

        int townHallLevel = coCPlayer.getTownHallLevel();

        Map<Integer, List<IBuilding>> displayingBuildings = new HashMap<>();
        int size = 0;

        for (int i = 0; i < allDisplayingBuildings.length; i++) {
            for (int j = 0; j < allDisplayingBuildings[i].length; j++) {
                IBuilding building = allDisplayingBuildings[i][j];
                if (building.getBuildingLevelData()[0].getMinTownHall() <= townHallLevel) {
                    List<IBuilding> list = displayingBuildings.getOrDefault(i, new ArrayList<>());
                    list.add(building);
                    displayingBuildings.put(i, list);
                    if (size < i)
                        size = i;
                }
            }
        }


        CustomInventory menu = new CustomInventory(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_BUILDING_NAME),
                Utils.getPerfectInventorySize(size * 9 + 10));

        for (Map.Entry<Integer, List<IBuilding>> entry : displayingBuildings.entrySet()) {
            int startX = 4 - entry.getValue().size() / 2 + entry.getKey() * 9;
            for (IBuilding building : entry.getValue()) {
                int amountOfBuilding = BuildingsAmountUtil.getAmountOfBuilding(building, townHallLevel);
                long buildingsPlayerHas = coCPlayer.getAllBuildings().stream().filter(b -> b.getBuilding() == building).count();
                List<String> lore = new ArrayList<>(Arrays.asList(lang.getTranslation(building.getDescriptionKey()).split(";")));
                String canStillBuild = amountOfBuilding <= buildingsPlayerHas ? ChatColor.RED + "" : ChatColor.GREEN + "";
                lore.addAll(Arrays.asList(lang.getTranslation(
                        TranslationKeys.COC_GUI_CONSTRUCTION_BUILDING_LORE,
                        Arrays.asList("${cost}", "${type}", "${max}", "${amount}", "${level}"),
                        Arrays.asList(building.getBuildingLevelData()[0].getUpgradeCost() + "", building.getResourceType().getColoredName(lang),
                                canStillBuild + amountOfBuilding, canStillBuild + buildingsPlayerHas, townHallLevel + "")).split(";")));
                if (building instanceof IUpgradeDetails)
                    lore.addAll(((IUpgradeDetails) building).getLoreDetails(building.getBuildingLevelData()[0], lang));

                menu.setItem(startX, new ItemBuilder(building.getMaterial())
                        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .setName(lang.getTranslation(building.getNameKey()))
                                .addEnchantment(buildingsPlayerHas == amountOfBuilding ? Enchantment.ARROW_FIRE : null, 1, true, false)
                                .setAmount(buildingsPlayerHas > 0 ? (int) buildingsPlayerHas : 1)
                        .setLore(lore).craft(), new ClickAction() {
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
                startX += entry.getValue().size() == 2 ? 2 : 1 ;
            }
        }

        menu.setItem(menu.getInventory().getSize() - 5, UsefulItems.LEFT_ARROW, new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                open(coCPlayer);
            }
        });

        menu.fill(UsefulItems.BACKGROUND_BLACK);

        menu.open(player);
    }
}
