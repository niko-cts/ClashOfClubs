package net.fununity.clashofclans.buildings.instances;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.common.util.FormatterUtil;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class ConstructionBuilding extends GeneralHologramBuilding {

    private final GeneralBuilding generalBuilding;
    private final long buildingFinishTime;
    private String timeLeftText;

    /**
     * Instantiates the construction building class.
     * @param generalBuilding {@link GeneralBuilding} - the constructed building.
     * @param buildingFinishTime long - the milliseconds time the building is finished.
     * @since 0.0.1
     */
    public ConstructionBuilding(GeneralBuilding generalBuilding, long buildingFinishTime) {
        super(generalBuilding.getOwnerUUID(), generalBuilding.getBuildingUUID(), generalBuilding.getBuilding(), generalBuilding.getCoordinate(), generalBuilding.getRotation(), generalBuilding.getLevel());
        this.generalBuilding = generalBuilding;
        this.buildingFinishTime = buildingFinishTime;
    }

    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory inventory = super.getInventory(language);
        CustomInventory menu = new CustomInventory(getBuildingTitle(language), 9 * 4);
        menu.setSpecialHolder(inventory.getSpecialHolder());

        for (int i = 0; i < inventory.getInventory().getSize() && i < 15; i++)
            menu.addItem(inventory.getInventory().getItem(i), inventory.getClickAction(i));

        menu.setItem(14, UsefulItems.BACKGROUND_GRAY);

        String name = language.getTranslation(TranslationKeys.COC_GUI_BUILDING_UNDERCONSTRUCTION, "${left}", FormatterUtil.getDuration(getBuildingDurationLeft()));
        int finished = getCurrentBuildingVersion();
        for (int i = 9, j = 27; j < menu.getInventory().getSize(); i += 9, j++)
            menu.setItem(j, new ItemBuilder(i <= finished ? UsefulItems.BACKGROUND_GREEN : UsefulItems.BACKGROUND_BLACK).setName(name).craft());

        menu.fill(UsefulItems.BACKGROUND_GRAY);
        return menu;
    }

    /**
     * Set the left building time.
     * @since 0.0.1
     */
    public boolean updateBuildingDuration() {
        if (System.currentTimeMillis() < buildingFinishTime) {
            updateHologram(getShowText());
            ClashOfClubs.getInstance().getPlayerManager().forceUpdateInventory(this);
            return false;
        }
        return true;
    }

    /**
     * Get the left building time in seconds.
     * @return int - left building seconds
     * @since 0.0.1
     */
    public long getBuildingDurationLeft() {
        return (buildingFinishTime - System.currentTimeMillis()) / 1000;
    }

    public long getBuildingFinishTime() {
        return buildingFinishTime;
    }

    /**
     * Gets the current version of the building.
     * E.g. percentage of fill.
     * @return int - building version
     * @since 0.0.1
     */
    public int getCurrentBuildingVersion() {
        return (int) (100 * getBuildingDurationLeft() / getBuildingDuration());
    }

    public GeneralBuilding getConstructedBuilding() {
        return generalBuilding;
    }

    /**
     * Returns the list of hologram lines.
     * @return List<String> - hologram lines
     */
    @Override
    public List<String> getShowText() {
        if (timeLeftText == null)
            this.timeLeftText = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(getOwnerUUID()).getLanguage().getTranslation(TranslationKeys.COC_CONSTRUCTION_TIMELEFT);

        return Arrays.asList(timeLeftText, ChatColor.YELLOW + FormatterUtil.getDuration(getBuildingDurationLeft()));
    }
}
