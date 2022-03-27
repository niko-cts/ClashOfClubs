package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDestroyableBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.actionbar.ActionbarMessage;
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

public class RandomWorldBuilding extends GeneralBuilding {

    /**
     * Instantiates the class.
     * @param uuid - UUID of the player
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param rotation int - rotation of the building
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public RandomWorldBuilding(UUID uuid, IBuilding building, Location coordinate, byte rotation, int level) {
        super(uuid, building, coordinate, rotation, level);
    }

    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory menu = new CustomInventory(language.getTranslation(getBuilding().getNameKey()), 9 * 3);
        menu.setSpecialHolder(getCoordinate().toString());
        menu.fill(UsefulItems.BACKGROUND_BLACK);
        menu.setItem(11, new ItemBuilder(Material.WRITABLE_BOOK).setName(language.getTranslation(getBuilding().getNameKey())).setLore(language.getTranslation(getBuilding().getDescriptionKey()).split(";")).craft());

        menu.setItem(15, new ItemBuilder(Material.BARRIER)
                .setName(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_DESTROY_NAME))
                .setLore(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_DESTROY_LORE, "${cost}", getRemoveCost() + " " + getBuilding().getResourceType().getColoredName(language)).split(";")).craft(), new ClickAction(true) {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                if (PlayerManager.getInstance().getPlayer(apiPlayer.getUniqueId()).getResource(getBuilding().getResourceType()) < getRemoveCost()) {
                    apiPlayer.sendActionbar(new ActionbarMessage(TranslationKeys.COC_PLAYER_NOT_ENOUGH_RESOURCE), "${type}", getBuilding().getResourceType().getColoredName(language));
                    return;
                }
                BuildingsManager.getInstance().removeBuilding(RandomWorldBuilding.this);
            }
        });
        return menu;
    }

    public int getRemoveCost() {
        return getBuilding().getBuildingLevelData()[0].getUpgradeCost();
    }

    @Override
    public IDestroyableBuilding getBuilding() {
        return (IDestroyableBuilding) super.getBuilding();
    }

}
