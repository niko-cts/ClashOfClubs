package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.interfaces.IBuildingWithHologram;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.common.util.FormatterUtil;
import net.fununity.main.api.hologram.APIHologram;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ConstructionBuilding extends GeneralBuilding implements IBuildingWithHologram {

    private final GeneralBuilding generalBuilding;
    private int buildingDuration;
    private final Location hologramLocation;
    private APIHologram hologram;

    /**
     * Instantiates the construction building class.
     * @param generalBuilding {@link GeneralBuilding} - the constructed building.
     * @param buildingDuration int - the building duration in seconds.
     * @since 0.0.1
     */
    public ConstructionBuilding(GeneralBuilding generalBuilding, int buildingDuration) {
        super(generalBuilding.getUuid(), generalBuilding.getBuilding(), generalBuilding.getCoordinate(), generalBuilding.getRotation(), generalBuilding.getLevel());
        this.generalBuilding = generalBuilding;
        this.buildingDuration = buildingDuration;

        hologramLocation = getCoordinate().add(getBuilding().getSize()[0] / 2.0, 0, getBuilding().getSize()[1] / 2.0);
        hologramLocation.setY(BuildingLocationUtil.getHighestYCoordinate(hologramLocation) + 2);
        this.hologram = new APIHologram(this.hologramLocation, Collections.singletonList(FormatterUtil.getDuration(getBuildingDuration())));
    }

    @Override
    public void setCoordinate(Location coordinate) {
        super.setCoordinate(coordinate);
        this.generalBuilding.setCoordinate(coordinate);
    }

    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory inventory = super.getInventory(language);
        CustomInventory menu = new CustomInventory(getBuildingTitle(language), 9 * 4);
        menu.setSpecialHolder(getId() + "-" + getCoordinate().toString());
        for (int i = 0; i < inventory.getInventory().getSize() && i < 15; i++)
            menu.addItem(inventory.getInventory().getItem(i), inventory.getClickAction(i));

        menu.setItem(14, UsefulItems.BACKGROUND_GRAY);

        String name = language.getTranslation(TranslationKeys.COC_GUI_BUILDING_UNDERCONSTRUCTION, "${left}", FormatterUtil.getDuration(getBuildingDuration()));
        int finished = getCurrentBuildingVersion();
        for (int i = 9, j = 27; j < menu.getInventory().getSize(); i += 9, j++)
            menu.setItem(j, new ItemBuilder(i <= finished ? UsefulItems.BACKGROUND_GREEN : UsefulItems.BACKGROUND_BLACK).setName(name).craft());

        menu.fill(UsefulItems.BACKGROUND_GRAY);
        return menu;
    }

    /**
     * Updates the hologram for the player.
     * @since 0.0.1
     */
    @Override
    public void updateHologram() {
        APIPlayer onlinePlayer = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(getUuid());
        if (onlinePlayer != null) {
            if (this.hologram != null)
                onlinePlayer.hideHolograms(this.hologram.getLocation());

            this.hologram = new APIHologram(this.hologramLocation, Collections.singletonList(FormatterUtil.getDuration(getBuildingDuration())));
            getHolograms().forEach(onlinePlayer::showHologram);
        }
    }

    /**
     * Get the constructed building.
     * @return GeneralBuilding - the constructed building.
     */
    public GeneralBuilding getConstructionBuilding() {
        return generalBuilding;
    }

    /**
     * Set the left building time.
     * @param buildTime int - new build time.
     * @since 0.0.1
     */
    public void setBuildingDuration(int buildTime) {
        this.buildingDuration = buildTime;
        if (this.buildingDuration == 0) {
            BuildingsManager.getInstance().finishedBuilding(this);
        } else if (this.buildingDuration > 0) {
            updateHologram();
            Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> PlayerManager.getInstance().forceUpdateInventory(this));
        } else
            System.out.println("Construction building below 0s " + getBuilding().getNameKey());
    }

    /**
     * Get the left building time in seconds.
     * @return int - left building seconds
     * @since 0.0.1
     */
    public int getBuildingDuration() {
        return buildingDuration;
    }

    @Override
    public List<APIHologram> getHolograms() {
        return this.hologram != null ? Collections.singletonList(this.hologram) : Collections.emptyList();
    }

    /**
     * Gets the current version of the building.
     * E.g. percentage of fill.
     * @return int - building version
     * @since 0.0.1
     */
    public int getCurrentBuildingVersion() {
        return 100 * this.buildingDuration / getMaxBuildingDuration();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConstructionBuilding that = (ConstructionBuilding) o;
        return buildingDuration == that.buildingDuration && Objects.equals(generalBuilding, that.generalBuilding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), generalBuilding, buildingDuration);
    }
}
