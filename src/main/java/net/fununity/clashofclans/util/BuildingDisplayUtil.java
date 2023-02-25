package net.fununity.clashofclans.util;

import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.list.*;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildingDisplayUtil {

    private BuildingDisplayUtil() {
        throw new UnsupportedOperationException("BuildingDisplayUtil is a utility class.");
    }

    public static Material getMaterial(int slot) {
        switch (slot) {
            case 10:
                return Material.GOLD_INGOT;
            case 12:
                return Material.IRON_CHESTPLATE;
            case 14:
                return Material.DIAMOND_SWORD;
            case 16:
                return Material.JUNGLE_SAPLING;
            default:
                return Material.AIR;
        }
    }

    public static IBuilding[][] getBuildingsForBuyGUI(int slot) {
        switch (slot) {
            case 10:
                return new IBuilding[][]{new IBuilding[]{ResourceGathererBuildings.FARM, ResourceContainerBuildings.BARN_STOCK},
                        {ResourceGathererBuildings.GOLD_MINER, ResourceContainerBuildings.GOLD_STOCK},
                        {ResourceGathererBuildings.COAL_MINER, ResourceContainerBuildings.GENERATOR}};
            case 12:
                return new IBuilding[][]{DefenseBuildings.values()};
            case 14:
                return new IBuilding[][]{TroopBuildings.values(), TroopCreationBuildings.values()};
            case 16:
                return new IBuilding[][]{new IBuilding[]{Buildings.BUILDER}, DecorativeBuildings.values(), WallBuildings.values()};
            default:
                return new IBuilding[0][0];
        }
    }

    private static IBuilding[][] getBuildingsOverviewForLore(int slot) {
        switch (slot) {
            case 10:
                return new IBuilding[][]{new IBuilding[]{ResourceGathererBuildings.FARM, ResourceContainerBuildings.BARN_STOCK},
                        {ResourceGathererBuildings.GOLD_MINER, ResourceContainerBuildings.GOLD_STOCK},
                        {ResourceGathererBuildings.COAL_MINER, ResourceContainerBuildings.GENERATOR}};
            case 12:
                return new IBuilding[][]{DefenseBuildings.values()};
            case 14:
                return new IBuilding[][]{TroopBuildings.values(), TroopCreationBuildings.values()};
            case 16:
                return new IBuilding[][]{new IBuilding[]{Buildings.BUILDER}, DecorativeBuildings.values(), WallBuildings.values()};
            default:
                return new IBuilding[0][0];
        }
    }

    public static String getNameKey(int slot) {
        switch (slot) {
            case 10:
                return TranslationKeys.COC_GUI_CONSTRUCTION_RESOURCE_NAME;
            case 12:
                return TranslationKeys.COC_GUI_CONSTRUCTION_DEFENSE_NAME;
            case 14:
                return TranslationKeys.COC_GUI_CONSTRUCTION_TROOP_NAME;
            case 16:
                return TranslationKeys.COC_GUI_CONSTRUCTION_DECORATIVE_NAME;
            default:
                return "";
        }
    }

    private static String getLoreKey(int slot) {
        switch (slot) {
            case 10:
                return TranslationKeys.COC_GUI_CONSTRUCTION_RESOURCE_LORE;
            case 12:
                return TranslationKeys.COC_GUI_CONSTRUCTION_DEFENSE_LORE;
            case 14:
                return TranslationKeys.COC_GUI_CONSTRUCTION_TROOP_LORE;
            case 16:
                return TranslationKeys.COC_GUI_CONSTRUCTION_DECORATIVE_LORE;
            default:
                return "";
        }
    }

    /**
     * Returns the lore for the building overview.
     * @param coCPlayer {@link CoCPlayer} - to display.
     * @param slot int - the slot.
     * @return List<String> - The lore for the item.
     * @since 1.0
     */
    public static List<String> getLore(CoCPlayer coCPlayer, int slot) {
        Language lang = coCPlayer.getOwner().getLanguage();
        List<String> lore = new ArrayList<>(Arrays.asList(lang.getTranslation(getLoreKey(slot)).split(";")));
        int townHallLevel = coCPlayer.getTownHallLevel();
        List<GeneralBuilding> allBuildings = coCPlayer.getAllBuildings();

        for (IBuilding[] buildings : getBuildingsForBuyGUI(slot)) {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < buildings.length; i++) {
                IBuilding building = buildings[i];
                if (building.getBuildingLevelData()[0].getMinTownHall() > townHallLevel) continue;

                if (i % 3 == 0 && !builder.isEmpty()) {
                    lore.add(builder.toString());
                    builder = new StringBuilder();
                }

                int amountOfBuilding = BuildingsAmountUtil.getAmountOfBuilding(building, townHallLevel);
                long buildingsPlayerHas = allBuildings.stream().filter(b -> b.getBuilding() == building).count();
                if (builder.isEmpty())
                    builder.append(ChatColor.GRAY).append("- ");
                builder.append(buildingsPlayerHas >= amountOfBuilding ? ChatColor.RED : ChatColor.GREEN).append("§m").append(lang.getTranslation(building.getNameKey()));
                if (i + 1 < buildings.length)
                    builder.append(ChatColor.GRAY).append(", ");
            }
            if (!builder.isEmpty())
                lore.add(builder.toString());

        }
        return lore;
    }
}
