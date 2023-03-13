package net.fununity.clashofclans.buildings.instances.destroyables;

import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DecorativeBuilding extends GeneralBuilding {

    /**
     * Instantiates the class.
     * @param uuid - UUID of the player
     * @param buildingUUID - UUID of the building
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param rotation int - rotation of the building
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public DecorativeBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, int[] baseRelatives, byte rotation, int level) {
        super(uuid, buildingUUID, building, coordinate, baseRelatives, rotation, level);
    }

    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory inventory = super.getInventory(language);
        inventory.setItem(13, new ItemBuilder(Material.BARRIER)
                .setName(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_DESTROY_NAME))
                .setLore(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_DESTROY_LORE).split(";")).craft(), new ClickAction(true) {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                BuildingsManager.getInstance().removeBuilding(DecorativeBuilding.this);
            }
        });
        return inventory;
    }

}
