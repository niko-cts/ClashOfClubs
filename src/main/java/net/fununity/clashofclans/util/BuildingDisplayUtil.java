package net.fununity.clashofclans.util;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.list.*;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
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

    public static IBuilding[][] getBuildings(int slot) {
        switch (slot) {
            case 10:
                return new IBuilding[][]{ResourceContainerBuildings.values(), ResourceGathererBuildings.values()};
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
        List<String> lore = new ArrayList<>(Arrays.asList(coCPlayer.getOwner().getLanguage().getTranslation(getLoreKey(slot)).split(";")));
        int townHallLevel = coCPlayer.getTownHallLevel();
        for (IBuilding[] buildings : getBuildings(slot)) {
            for (IBuilding building : buildings) {
                int amountOfBuilding = BuildingsAmountUtil.getAmountOfBuilding(building, townHallLevel);
                long buildingsPlayerHas = coCPlayer.getBuildings().stream().filter(b -> b.getBuilding() == building).count();
                if (buildingsPlayerHas >= amountOfBuilding)
                    lore.add("§7- §c§m"+ coCPlayer.getOwner().getLanguage().getTranslation(building.getNameKey()));
                else
                    lore.add("§7- §a" + coCPlayer.getOwner().getLanguage().getTranslation(building.getNameKey()));
            }
        }
        return lore;
    }
}
