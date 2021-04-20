package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IResourceGatherBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public ResourceGatherBuilding(UUID uuid, IBuilding building, Location coordinate, int level) {
        super(uuid, building, coordinate, level);
    }

    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory inventory = super.getInventory(language);
        CustomInventory menu = new CustomInventory(language.getTranslation(getBuilding().getNameKey()) + " - " + getLevel(), 9*5);
        menu.setSpecialHolder(inventory.getSpecialHolder());

        for (int i = 0; i < inventory.getInventory().getContents().length; i++) {
            ItemStack content = inventory.getInventory().getContents()[i];
            menu.setItem(i >= 9 * 3 ? i + 9 : i, content, inventory.getClickAction(i));
        }
        menu.fill(UsefulItems.BACKGROUND_BLACK);

        String resourceName = getResourceContaining().getColoredName(language);
        menu.setItem(20, new ItemBuilder(Material.CLOCK)
                .setName(language.getTranslation(TranslationKeys.COC_GUI_GATHER_CLOCK_NAME, "${type}", resourceName))
                .setLore(language.getTranslation(TranslationKeys.COC_GUI_GATHER_CLOCK_LORE, "${resource}",
                        getResourceContaining().getChatColor() + "" + getResourceGatheringPerHour()+" " + resourceName).split(";")).craft());

        menu.setItem(24, new ItemBuilder(getResourceContaining().getGlass())
                .setName(language.getTranslation(TranslationKeys.COC_GUI_GATHER_TAKE_NAME))
                .setLore(language.getTranslation(TranslationKeys.COC_GUI_GATHER_TAKE_LORE, "${resource}", getResourceContaining().getChatColor() + ""+ ((int) getAmount()) + " " + resourceName).split(";")).craft(), new ClickAction() {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                if (getAmount() < 1)
                    return;
                BuildingsManager.getInstance().fillResource(ResourceGatherBuilding.this);
            }
        });

        return menu;
    }

    public int getResourceGatheringPerHour() {
        return getBuilding().getBuildingLevelData()[getLevel()-1].getResourceGatheringPerHour();
    }

    @Override
    public IResourceGatherBuilding getBuilding() {
        return (IResourceGatherBuilding) super.getBuilding();
    }

}
