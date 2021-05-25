package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.ITroopCreateBuilding;
import net.fununity.clashofclans.gui.TroopsGUI;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * The troop creation building class.
 * @see TroopsBuilding
 * @author Niko
 * @since 0.0.1
 */
public class TroopsCreateBuilding extends TroopsBuilding {

    private Queue<ITroop> troopsQueue;
    private int trainSecondsLeft;

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @param troopAmount ConcurrentMap<ITroop, Integer> - the current amount of troops.
     * @since 0.0.1
     */
    public TroopsCreateBuilding(UUID uuid, IBuilding building, Location coordinate, byte rotation, int level, ConcurrentMap<ITroop, Integer> troopAmount) {
        super(uuid, building, coordinate, rotation, level, troopAmount);
        this.troopsQueue = new LinkedList<>();
        this.trainSecondsLeft = 0;
    }

    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory inventory = super.getInventory(language);
        CustomInventory menu = new CustomInventory(getBuildingTitle(language), 9 * 4);
        menu.setSpecialHolder(getId() + "-" + getCoordinate().toString());
        for (int i = 0; i < inventory.getInventory().getContents().length; i++)
            menu.setItem(i, inventory.getInventory().getItem(i), inventory.getClickAction(i));

        menu.setItem(24, new ItemBuilder(Material.IRON_SWORD)
                        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .setName(language.getTranslation(TranslationKeys.COC_GUI_TROOPS_TRAIN_NAME))
                        .setLore(language.getTranslation(TranslationKeys.COC_GUI_TROOPS_TRAIN_LORE).split(";")).craft(),
                new ClickAction() {
                    @Override
                    public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                        apiPlayer.getPlayer().closeInventory();
                        TroopsGUI.openTraining(apiPlayer, TroopsCreateBuilding.this);
                    }
                });

        menu.fill(UsefulItems.BACKGROUND_GRAY);

        return menu;
    }

    public void setTrainSecondsLeft(int secondsForNextTroop) {
        this.trainSecondsLeft = secondsForNextTroop;
    }

    public int getTrainSecondsLeft() {
        return trainSecondsLeft;
    }

    public void setTroopsQueue(Queue<ITroop> troopsQueue) {
        this.troopsQueue = troopsQueue;
    }

    /**
     * Get the queue of troops, which will be created.
     * @return Queue<Troop> - The troop creation queue.
     * @since 0.0.1
     */
    public Queue<ITroop> getTroopsQueue() {
        return troopsQueue;
    }

    @Override
    public ITroopCreateBuilding getBuilding() {
        return (ITroopCreateBuilding) super.getBuilding();
    }
}
