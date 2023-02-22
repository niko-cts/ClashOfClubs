package net.fununity.clashofclans.buildings.instances.troops;

import net.fununity.clashofclans.buildings.TroopsBuildingManager;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.ITroopCreateBuilding;
import net.fununity.clashofclans.gui.TroopsGUI;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.troops.Troops;
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

/**
 * The troop creation building class.
 * @see TroopsBuilding
 * @author Niko
 * @since 0.0.1
 */
public class TroopsCreateBuilding extends TroopsBuilding {

    private final Queue<ITroop> troopsQueue;
    private long queueSecondsLast = Long.MAX_VALUE;

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param buildingUUID UUID - the uuid of the building.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public TroopsCreateBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, byte rotation, int level) {
        super(uuid, buildingUUID, building, coordinate, rotation, level);
        this.troopsQueue = new LinkedList<>();
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

    /**
     * Checks the queue. If the training time is finished, calls {@link TroopsBuildingManager#troopEducated(TroopsCreateBuilding)}.
     * @since 0.0.2
     */
    public void checkQueue() {
        if (troopsQueue.isEmpty())
            return;

        queueSecondsLast--;

        if (queueSecondsLast < 1) {
            TroopsBuildingManager.getInstance().troopEducated(this);
            if (!troopsQueue.isEmpty())
                queueSecondsLast = troopsQueue.peek().getTrainDuration();
        }
    }

    public void checkQueuePlayerWasGone(double secondsGone) {
        for (int i = 0; i < secondsGone; i++) {
            if (troopsQueue.isEmpty()) break;
            checkQueue();
        }
    }

    /**
     * Adds a troop to the queue.
     * @param troopsQueue ITroop - the troop
     * @return boolean - could be added to the queue
     * @since 0.0.2
     */
    public boolean addTroop(ITroop troopsQueue) {
        if (troopsQueue.getSize() + getCurrentSizeOfTroops() > getMaxAmountOfTroops())
            return false;
        if (this.troopsQueue.isEmpty())
            this.queueSecondsLast = troopsQueue.getTrainDuration();
        this.troopsQueue.add(troopsQueue);
        return true;
    }

    /**
     * Calculates the size of troops already educated and the size of troops in queue.
     * @return int - size of troops
     */
    @Override
    public int getCurrentSizeOfTroops() {
        int sizeOfTroops = super.getCurrentSizeOfTroops();
        Queue<ITroop> troops = new LinkedList<>(troopsQueue);
        while(!troops.isEmpty())
            sizeOfTroops += troops.poll().getSize();
        return sizeOfTroops;
    }

    /**
     * Gets a copied list of the queue.
     * @return Queue<ITroop> - the queue.
     */
    public Queue<ITroop> getTroopsQueue() {
        return new LinkedList<>(troopsQueue);
    }

    /**
     * Returns a string with every troop in queue.
     * Example: name,name2,...
     * @return String - all troop names in lowercase split by ','.
     */
    public String getTroopsQueueId() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<ITroop> iterator = getTroopsQueue().iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().name().toLowerCase());
            if(iterator.hasNext()) stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    @Override
    public ITroopCreateBuilding getBuilding() {
        return (ITroopCreateBuilding) super.getBuilding();
    }

    /**
     * Returns the seconds left till the troop is trained.
     * @return int - amount in seconds.
     * @since 0.0.2
     */
    public int getTrainSecondsLeft() {
        return (int) ((queueSecondsLast - System.currentTimeMillis()) / 1000);
    }

    public void insertQueue(String queue) {
        if (queue.isEmpty()) return;

        for (String troop : queue.split(",")) {
            addTroop(Troops.valueOf(troop.toUpperCase()));
        }
    }
}
