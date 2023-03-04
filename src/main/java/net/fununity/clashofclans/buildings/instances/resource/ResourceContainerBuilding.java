package net.fununity.clashofclans.buildings.instances.resource;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralHologramBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDifferentVersionBuildings;
import net.fununity.clashofclans.buildings.interfaces.IResourceContainerBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The resource container building class.
 * @see GeneralBuilding
 * @author Niko
 * @since 0.0.1
 */
public class ResourceContainerBuilding extends GeneralHologramBuilding implements IDifferentVersionBuildings {

    private double currentAmount;

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param buildingUUID UUID - the uuid of the building.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public ResourceContainerBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, byte rotation, int level) {
        this(uuid, buildingUUID, building, coordinate, rotation, level, 0);
    }

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param buildingUUID UUID - the uuid of the building.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @param amount     double - amount of building
     * @since 0.0.1
     */
    public ResourceContainerBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, byte rotation, int level, double amount) {
        super(uuid, buildingUUID, building, coordinate, rotation, level);
        this.currentAmount = amount;
    }


    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory inventory = super.getInventory(language);
        CustomInventory menu = new CustomInventory(getBuildingTitle(language), 9 * 4);
        menu.setSpecialHolder(inventory.getSpecialHolder());

        for (int i = 0; i < inventory.getInventory().getContents().length; i++) {
            ItemStack content = inventory.getInventory().getContents()[i];
            menu.addItem(content, inventory.getClickAction(i));
        }

        menu.fill(UsefulItems.BACKGROUND_BLACK);

        String name = language.getTranslation(TranslationKeys.COC_GUI_CONTAINER_AMOUNT, Arrays.asList("${color}", "${max}", "${current}"), Arrays.asList(getContainingResourceType().getChatColor() + "", getMaximumResource() + "", ((int)getAmount()) + ""));

        double fillTill = 90.0 * getAmount() / getMaximumResource();
        for (int i = 9, j = 27; j < 36; i += 9, j++) {
            menu.setItem(j, new ItemBuilder(fillTill > i ? getContainingResourceType().getGlass() : UsefulItems.BACKGROUND_GRAY).setName(name).craft());
        }

        return menu;
    }

    /**
     * Set the amount of resource in the building.
     * @param currentAmount int - the amount of resource.
     * @return boolean - needs a rebuild
     * @since 0.0.1
     */
    public boolean setAmount(double currentAmount) {
        boolean change = (int) getAmount() != (int) currentAmount;
        int oldVersion = getCurrentBuildingVersion();
        this.currentAmount = Math.min(currentAmount, getMaximumResource());
        if (!change)
            return false;
        updateHologram(getShowText());
        return this.updateVersion(oldVersion != getCurrentBuildingVersion());
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);
        updateHologram(getShowText());
    }


    /**
     * Get the amount of the building.
     * @return int - the amount of thing
     * @since 0.0.1
     */
    public double getAmount() {
        return currentAmount;
    }

    /**
     * Called when the version was updated.
     * @param schematic boolean - schematic change
     * @since 0.0.1
     */
    @Override
    public boolean updateVersion(boolean schematic) {
        ClashOfClubs.getInstance().getPlayerManager().forceUpdateInventory(this);
        return schematic;
    }

    /**
     * Gets the current version of the building.
     * E.g. percentage of fill.
     * @return int - building version
     * @since 0.0.1
     */
    @Override
    public int getCurrentBuildingVersion() {
        double percentage = getAmount() / getMaximumResource();
        if (percentage < 0.2)
            return 0;
        if (percentage < 0.4)
            return 1;
        if (percentage < 0.6)
            return 2;
        if (percentage < 0.8)
            return 3;
        return 4;
    }

    /**
     * Get the type of resource that the building contains.
     * @return {@link ResourceTypes} - the type of resource.
     * @since 0.0.1
     */
    public ResourceTypes getContainingResourceType() {
        return getBuilding().getContainingResourceType();
    }

    /**
     * Get the maximum resource of the building.
     * @return int - the maximum amount of possible resource.
     * @since 0.0.1
     */
    public int getMaximumResource() {
        return getLevel() > 0 ? getBuilding().getBuildingLevelData()[getLevel() - 1].getMaximumResource() : 0;
    }

    /**
     * Get the resource container building interface.
     * @return {@link IResourceContainerBuilding} - the container instance.
     * @since 0.0.1
     */
    @Override
    public IResourceContainerBuilding getBuilding() {
        return (IResourceContainerBuilding) super.getBuilding();
    }

    /**
     * Returns the list of hologram lines.
     *
     * @return List<String> - hologram lines
     */
    @Override
    public List<String> getShowText() {
        return Collections.singletonList("" + getContainingResourceType().getChatColor() + ((int) getAmount()) + ChatColor.GRAY + "/" + getContainingResourceType().getChatColor() + getMaximumResource());
    }
}
