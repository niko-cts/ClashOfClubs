package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IResourceContainerBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.hologram.APIHologram;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * The resource container building class.
 * @see GeneralBuilding
 * @author Niko
 * @since 0.0.1
 */
public class ResourceContainerBuilding extends ContainerBuilding {

    private APIHologram hologram;

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public ResourceContainerBuilding(UUID uuid, IBuilding building, Location coordinate, int level) {
        super(uuid, building, coordinate, level);
        this.createHologram();
     }


    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory inventory = super.getInventory(language);
        CustomInventory menu = new CustomInventory(language.getTranslation(getBuilding().getNameKey()) + " - " + getLevel(), 9*4);
        menu.setSpecialHolder(inventory.getSpecialHolder());

        for (int i = 0; i < inventory.getInventory().getContents().length; i++) {
            ItemStack content = inventory.getInventory().getContents()[i];
            menu.addItem(content, inventory.getClickAction(i));
        }
        menu.fill(UsefulItems.BACKGROUND_BLACK);

        int fillTill = (int) (90 * getAmount() / getMaximumResource());

        String name = language.getTranslation(TranslationKeys.COC_GUI_CONTAINER_AMOUNT, Arrays.asList("${color}", "${max}", "${current}"), Arrays.asList(getResourceContaining().getChatColor() + "", getMaximumResource() + "", ((int)getAmount()) + ""));
        for (int i = 10, j=27; j < 36; i+=10, j++)
            menu.setItem(j, new ItemBuilder(fillTill >= i ? getResourceContaining().getGlass() : UsefulItems.BACKGROUND_GRAY).setName(name).craft());

        return menu;
    }

    @Override
    public void setAmount(double currentAmount) {
        boolean change = (int) getAmount() != (int) currentAmount;
        super.setAmount(currentAmount);
        if (!change) return;
        this.createHologram();
    }

    @Override
    public void setCoordinate(Location coordinate) {
        super.setCoordinate(coordinate);
        this.createHologram();
    }

    /**
     * Get the type of resource that the building contains.
     * @return {@link ResourceTypes} - the type of resource.
     * @since 0.0.1
     */
    public ResourceTypes getResourceContaining() {
        return getBuilding().getBuildingLevelData()[getLevel() - 1].getResourceTypes();
    }

    /**
     * Get the maximum resource of the building.
     * @return int - the maximum amount of possible resource.
     * @since 0.0.1
     */
    public int getMaximumResource() {
        return getBuilding().getBuildingLevelData()[getLevel() - 1].getMaximumResource();
    }

    /**
     * Creates a hologram, which shows the current building resources.
     * @since 0.0.1
     */
    private void createHologram() {
        List<APIPlayer> players = new ArrayList<>();
        if (this.hologram != null) {
            APIPlayer onlinePlayer = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(getUuid());
            if (onlinePlayer != null && onlinePlayer.getHolograms(this.hologram.getLocation()).contains(this.hologram)) {
                onlinePlayer.hideHolograms(this.hologram.getLocation());
                players.add(onlinePlayer);
            }
        }

        this.hologram = new APIHologram(getCoordinate().clone().add(0.1, 2.5, 0.1), Collections.singletonList("" + getResourceContaining().getChatColor() + ((int) getAmount()) + "§7/" + getResourceContaining().getChatColor() + getMaximumResource()));

        players.forEach(p -> p.showHologram(this.hologram));
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

    public APIHologram getHologram() {
        return hologram;
    }
}
