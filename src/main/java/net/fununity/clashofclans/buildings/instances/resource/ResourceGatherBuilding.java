package net.fununity.clashofclans.buildings.instances.resource;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IResourceGatherBuilding;
import net.fununity.clashofclans.buildings.interfaces.data.ResourceGatherLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * The resource gather building class.
 * @see ResourceContainerBuilding
 * @author Niko
 * @since 0.0.1
 */
public class ResourceGatherBuilding extends ResourceContainerBuilding {

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param buildingUUID UUID - the uuid of the building.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public ResourceGatherBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, byte rotation, int level) {
        super(uuid, buildingUUID, building, coordinate, rotation, level);
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
    public ResourceGatherBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, byte rotation, int level, double amount) {
        super(uuid, buildingUUID, building, coordinate, rotation, level, amount);
    }

    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory inventory = super.getInventory(language);
        CustomInventory menu = new CustomInventory(getBuildingTitle(language), 9*5);
        menu.setSpecialHolder(inventory.getSpecialHolder());

        for (int i = 0; i < inventory.getInventory().getContents().length; i++) {
            ItemStack content = inventory.getInventory().getContents()[i];
            menu.setItem(i >= 9 * 3 ? i + 9 : i, content, inventory.getClickAction(i));
        }
        menu.fill(UsefulItems.BACKGROUND_GRAY);

        String resourceName = getContainingResourceType().getColoredName(language);
        menu.setItem(20, new ItemBuilder(Material.CLOCK)
                .setName(language.getTranslation(TranslationKeys.COC_GUI_GATHER_CLOCK_NAME, "${type}", resourceName))
                .setLore(language.getTranslation(TranslationKeys.COC_GUI_GATHER_CLOCK_LORE, "${resource}",
                        getContainingResourceType().getChatColor() + "" + getResourceGatheringPerHour()+" " + resourceName).split(";")).craft());

        menu.setItem(24, new ItemBuilder(getContainingResourceType().getRepresentativeMaterial())
                .setName(language.getTranslation(TranslationKeys.COC_GUI_GATHER_TAKE_NAME))
                .setLore(language.getTranslation(TranslationKeys.COC_GUI_GATHER_TAKE_LORE, "${resource}", getContainingResourceType().getChatColor() + ""+ ((int) getAmount()) + " " + resourceName).split(";")).craft(), new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                if (getAmount() < 1)
                    return;

                setCloseInventory(BuildingsManager.getInstance().emptyGatherer(List.of(ResourceGatherBuilding.this)));
            }

            @Override
            public void onShiftClick(APIPlayer apiPlayer, ItemStack itemStack, int slot) {
                CoCPlayer player = ClashOfClubs.getInstance().getPlayerManager().getPlayer(getOwnerUUID());
                setCloseInventory(BuildingsManager.getInstance().emptyGatherer(player.getResourceGatherBuildings(getContainingResourceType())));
            }
        });

        return menu;
    }


    /**
     * Adds the amount of gathering resource per second
     * @return boolean - needs a rebuild
     * @since 0.0.2
     */
    public boolean addAmountPerSecond() {
        ResourceGatherLevelData levelData = getBuilding().getBuildingLevelData()[getLevel() - 1];
        if (getAmount() < levelData.getMaximumResource())
            return setAmount(getAmount() + levelData.getResourceGatheringPerHour() / (3600.0)); // each s
        return false;
    }

    /**
     * Adds the amount to the building the player was gone (gatheringPerSecond * secondsGone)
     * @param secondsGone long - the amount the player was gone.
     * @since 0.0.2
     */
    public void addAmountPlayerWasGone(double secondsGone) {
        ResourceGatherLevelData levelData = getBuilding().getBuildingLevelData()[getLevel() - 1];
        if (getAmount() < levelData.getMaximumResource())
            setAmount(getAmount() + levelData.getResourceGatheringPerHour() / 3600.0 * secondsGone); // each s
    }

    /**
     * Get the resource gathering per hour on the current level.
     * @return int - resource gathering per hour.
     * @since 0.0.1
     */
    public int getResourceGatheringPerHour() {
        return getLevel() > 0 ? getBuilding().getBuildingLevelData()[getLevel()-1].getResourceGatheringPerHour() : 0;
    }

    @Override
    public IResourceGatherBuilding getBuilding() {
        return (IResourceGatherBuilding) super.getBuilding();
    }

}
