package net.fununity.clashofclans.gui;

import net.fununity.clashofclans.buildings.BuildingsAmountUtil;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.list.*;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.Utils;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gui class for buying buildings.
 * @author Niko
 * @since 0.0.1
 */
public class BuildingBuyGUI {

    private BuildingBuyGUI() {
        throw new UnsupportedOperationException("BuildingBuyGUI is a utility class.");
    }

    /**
     * Open the main GUI.
     * @param player APIPlayer - the player to open the gui.
     * @param coCPlayer CoCPlayer - the coc player.
     * @since 0.0.1
     */
    public static void open(APIPlayer player, CoCPlayer coCPlayer) {
        Language lang = player.getLanguage();
        CustomInventory menu = new CustomInventory(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_NAME), 9*3);
        menu.fill(UsefulItems.BACKGROUND_BLACK);

        menu.setItem(10, new ItemBuilder(Material.OAK_WOOD).setName(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_BUILDINGS_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_BUILDINGS_LORE).split(";")).craft(), new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                openSpecified(player, coCPlayer, Buildings.values());
            }
        });
        menu.setItem(12, new ItemBuilder(Material.GLASS_BOTTLE).setName(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_RESOURCE_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_RESOURCE_LORE).split(";")).craft(), new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                openSpecified(player, coCPlayer, ResourceContainerBuildings.values());
            }
        });
        menu.setItem(14, new ItemBuilder(Material.GOLD_INGOT).setName(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_GATHERER_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_GATHERER_LORE).split(";")).craft(), new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                openSpecified(player, coCPlayer, ResourceGathererBuildings.values());
            }
        });
        menu.setItem(16, new ItemBuilder(Material.STONE_SWORD).setName(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_TROOP_NAME))
                .setLore(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_TROOP_LORE).split(";")).craft(), new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                List<IBuilding> buildings = Arrays.asList(TroopBuildings.values());
                buildings.addAll(Arrays.asList(TroopCreationBuildings.values()));

                openSpecified(player, coCPlayer, buildings.toArray(new IBuilding[0]));
            }
        });

        menu.open(player);
    }

    /**
     * Open a specified GUI.
     * @param player APIPlayer - the player to open the gui.
     * @param coCPlayer CoCPlayer - the coc player.
     * @param building IBuilding[] - the buildings in the gui.
     * @since 0.0.1
     */
    private static void openSpecified(APIPlayer player, CoCPlayer coCPlayer, IBuilding[] building) {
        Language lang = player.getLanguage();

        int townHallLevel = coCPlayer.getTownHallLevel();
        List<IBuilding> buildings = Arrays.stream(building).filter(b -> b.getBuildingLevelData()[0].getMinTownHall() <= townHallLevel).collect(Collectors.toList());

        CustomInventory menu = new CustomInventory(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_BUILDING_NAME), Utils.getPerfectInventorySize(buildings.size()));

        for (IBuilding iBuilding : buildings) {
            int amountOfBuilding = BuildingsAmountUtil.getAmountOfBuilding(iBuilding, townHallLevel);
            List<String> lore = new ArrayList<>(Arrays.asList(lang.getTranslation(iBuilding.getDescriptionKey()).split(";")));
            lore.addAll(Arrays.asList(lang.getTranslation(TranslationKeys.COC_GUI_CONSTRUCTION_BUILDING_LORE, Arrays.asList("${cost}", "${type}", "${townhall}", "${amount}"),
                    Arrays.asList(iBuilding.getBuildingLevelData()[0].getUpgradeCost()+"", iBuilding.getResourceType().getColoredName(lang), townHallLevel+"", amountOfBuilding+"")).split(";")));

            long buildingsPlayerHas = coCPlayer.getBuildings().stream().filter(b -> b.getBuilding() == iBuilding).count();
            menu.addItem(new ItemBuilder(iBuilding.getMaterial()).setName(lang.getTranslation(iBuilding.getNameKey())).setLore(lore).craft(), new ClickAction() {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    if (buildingsPlayerHas >= amountOfBuilding)
                        return;
                    BuildingsManager.getInstance().createBuilding(apiPlayer, iBuilding);
                    setCloseInventory(true);
                }
            });
        }

        menu.fill(UsefulItems.BACKGROUND_BLACK);
        menu.open(player);
    }
}
