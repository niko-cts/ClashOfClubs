package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuildingWithHologram;
import net.fununity.clashofclans.buildings.interfaces.IDifferentVersionBuildings;
import net.fununity.clashofclans.buildings.interfaces.IResourceContainerBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.hologram.APIHologram;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Bukkit;
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
public class ResourceContainerBuilding extends GeneralBuilding implements IBuildingWithHologram, IDifferentVersionBuildings {

    private APIHologram hologram;
    private double currentAmount;

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public ResourceContainerBuilding(UUID uuid, IBuilding building, Location coordinate, byte rotation, int level) {
        this(uuid, building, coordinate, rotation, level, 0);
     }
    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public ResourceContainerBuilding(UUID uuid, IBuilding building, Location coordinate, byte rotation, int level, double amount) {
        super(uuid, building, coordinate, rotation, level);
        this.currentAmount = amount;
        this.updateHologram();
    }


    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory inventory = super.getInventory(language);
        CustomInventory menu = new CustomInventory(getBuildingTitle(language), 9*4);
        menu.setSpecialHolder(inventory.getSpecialHolder());

        for (int i = 0; i < inventory.getInventory().getContents().length; i++) {
            ItemStack content = inventory.getInventory().getContents()[i];
            menu.addItem(content, inventory.getClickAction(i));
        }
        menu.fill(UsefulItems.BACKGROUND_GRAY);

        String name = language.getTranslation(TranslationKeys.COC_GUI_CONTAINER_AMOUNT, Arrays.asList("${color}", "${max}", "${current}"), Arrays.asList(getContainingResourceType().getChatColor() + "", getMaximumResource() + "", ((int)getAmount()) + ""));

        double fillTill = 90.0 * getAmount() / getMaximumResource();
        for (double i = 9.0, j = 27; j < 36; i += 9.0, j++)
            menu.setItem((int) j, new ItemBuilder(fillTill > i ? getContainingResourceType().getGlass() : UsefulItems.BACKGROUND_GRAY).setName(name).craft());

        return menu;
    }

    /**
     * Set the amount of the building.
     * @param currentAmount int - the amount of troops.
     * @since 0.0.1
     */
    public void setAmount(double currentAmount) {
        boolean change = (int) getAmount() != (int) currentAmount;
        int oldVersion = getCurrentBuildingVersion();
        this.currentAmount = currentAmount;
        if (!change) return;
        updateVersion(oldVersion != getCurrentBuildingVersion());
        this.updateHologram();
    }
    /**
     * Get the amount of the building.
     * @return int - the amount of thing
     * @since 0.0.1
     */
    public double getAmount() {
        return currentAmount;
    }

    @Override
    public void setCoordinate(Location coordinate) {
        super.setCoordinate(coordinate);
        this.updateHologram();
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

            this.hologram = new APIHologram(getCoordinate().clone().add(0.75, 2.5, 0.75), Collections.singletonList("" + getContainingResourceType().getChatColor() + ((int) getAmount()) + "§7/" + getContainingResourceType().getChatColor() + getMaximumResource()));
            onlinePlayer.showHologram(this.hologram);
        }
    }

    /**
     * Called when the version was updated.
     * @param schematic boolean - schematic change
     * @since 0.0.1
     */
    @Override
    public void updateVersion(boolean schematic) {
        Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> PlayerManager.getInstance().forceUpdateInventory(this));;
        if (schematic)
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> Schematics.createBuilding(this));
    }

    /**
     * Gets the current version of the building.
     * E.g. percentage of fill.
     * @return int - building version
     * @since 0.0.1
     */
    @Override
    public int getCurrentBuildingVersion() {
        double percentage = 100 * getAmount() / getMaximumResource();
        if (percentage < 20)
            return 0;
        if (percentage < 40)
            return 1;
        if (percentage < 60)
            return 2;
        if (percentage < 80)
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

    @Override
    public List<APIHologram> getHolograms() {
        return Collections.singletonList(this.hologram);
    }
}
