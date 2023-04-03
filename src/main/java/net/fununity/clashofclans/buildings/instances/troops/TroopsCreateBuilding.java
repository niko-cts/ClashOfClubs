package net.fununity.clashofclans.buildings.instances.troops;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.Schematics;
import net.fununity.clashofclans.buildings.TroopsBuildingManager;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDifferentVersionBuildings;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The troop creation building class.
 * @see TroopsBuilding
 * @author Niko
 * @since 0.0.1
 */
public class TroopsCreateBuilding extends TroopsBuilding implements IDifferentVersionBuildings {

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
    public TroopsCreateBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, int[] baseRelatives, byte rotation, int level) {
        this (uuid, buildingUUID, building, coordinate, baseRelatives, rotation, level, new ConcurrentHashMap<>());
    }

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param buildingUUID UUID - the uuid of the building.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @param troopAmount ConcurrentHashMap - the troops and amount
     * @since 0.0.1
     */
    public TroopsCreateBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, int[] baseRelatives, byte rotation, int level, ConcurrentHashMap<ITroop, Integer> troopAmount) {
        super(uuid, buildingUUID, building, coordinate, baseRelatives, rotation, level, troopAmount);
        this.troopsQueue = new LinkedList<>();
    }


    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory menu = new CustomInventory(super.getInventory(language), 9 * (getCurrentSizeOfTroops() > 0 ? 5 : 4));

        if (getCurrentSizeOfTroops() == 0)
            menu.setItem(22, UsefulItems.BACKGROUND_BLACK);

        menu.setItem(24, new ItemBuilder(Material.IRON_SWORD)
                        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .setName(language.getTranslation(TranslationKeys.COC_GUI_TROOPS_TRAIN_NAME))
                        .setLore(language.getTranslation(TranslationKeys.COC_GUI_TROOPS_TRAIN_LORE).split(";")).craft(),
                new ClickAction(true) {
                    @Override
                    public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                        Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () ->
                                TroopsGUI.openTraining(apiPlayer, TroopsCreateBuilding.this), 1L);
                    }
                });

        menu.fill(UsefulItems.BACKGROUND_BLACK);

        return menu;
    }

    /**
     * Checks the queue. If the training time is finished, calls {@link TroopsBuildingManager#troopEducated(TroopsCreateBuilding, ITroop)}.
     * @since 0.0.2
     */
    public void checkQueue() {
        if (troopsQueue.isEmpty())
            return;

        queueSecondsLast--;

        if (queueSecondsLast < 1) {
            TroopsBuildingManager.getInstance().troopEducated(this, troopsQueue.poll());
            if (!troopsQueue.isEmpty())
                queueSecondsLast = troopsQueue.peek().getTrainDuration();
            else
                queueEmptied();
        }

        ClashOfClubs.getInstance().getPlayerManager().forceUpdateInventory(this);
    }

    /**
     * Skips the queue for the amount of seconds player was gone.
     * @param secondsGone int - the seconds the player was gone.
     */
    public void checkQueuePlayerWasGone(int secondsGone) {
        do {
            if (troopsQueue.isEmpty())
                break;

            if (queueSecondsLast <= secondsGone) {
                TroopsBuildingManager.getInstance().troopEducated(this, troopsQueue.poll());
                if (!troopsQueue.isEmpty())
                    queueSecondsLast = troopsQueue.peek().getTrainDuration();
                else
                    queueEmptied();
                secondsGone -= queueSecondsLast;
            } else {
                queueSecondsLast -= secondsGone;
                secondsGone = 0;
            }
        } while (secondsGone > 0);
    }

    /**
     * Adds a troop to the queue.
     * @param troop ITroop - the troop
     * @return boolean - could be added to the queue
     * @since 0.0.2
     */
    public boolean addTroop(ITroop troop) {
        if (getCurrentSizeOfTroops() >= getMaxAmountOfTroops())
            return false;
        if (this.troopsQueue.isEmpty()) {
            this.queueSecondsLast = troop.getTrainDuration();
        }
        this.troopsQueue.add(troop);
        return true;
    }


    public void removeTroop(ITroop troop) {
        if (this.troopsQueue.isEmpty()) return;
        if (this.troopsQueue.peek().equals(troop)) {
            this.troopsQueue.poll();
            if (!this.troopsQueue.isEmpty())
                this.queueSecondsLast = troopsQueue.peek().getTrainDuration();
            else
                queueEmptied();
        } else {
            this.troopsQueue.remove(troop);
        }
    }

    /**
     * Will call {@link Schematics#createBuilding(GeneralBuilding)} to update the version.
     * @since 0.0.2
     */
    private void queueEmptied() {
        if (getCurrentBuildingVersion() == 0)
            Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> Schematics.createBuilding(this));
    }


    /**
     * Calculates the size of troops already educated and the size of troops in queue.
     * @return int - size of troops
     */
    @Override
    public int getCurrentSizeOfTroops() {
        int sizeOfTroops = super.getCurrentSizeOfTroops();
        for (ITroop iTroop : getTroopsQueue()) {
            sizeOfTroops += iTroop.getSize();
        }
        return sizeOfTroops;
    }

    /**
     * Gets a copied list of the queue.
     * @return Queue<ITroop> - the queue.
     */
    public Queue<ITroop> getTroopsQueue() {
        return new LinkedList<>(troopsQueue);
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
        return (int) queueSecondsLast;
    }

    /**
     * Returns a string with every troop in queue.
     * Example: ordinal1,ordinal2,...
     * @return String - all troop ordinals split by ','.
     */
    public String getTroopsQueueId() {
        StringBuilder stringBuilder = new StringBuilder().append("[");
        Iterator<ITroop> iterator = getTroopsQueue().iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().ordinal());
            if (iterator.hasNext())
                stringBuilder.append(",");
        }
        return stringBuilder.append("]").toString();
    }
    public void insertQueue(String queue) {
        queue = queue.replace("[", "").replace("]", "");
        if (queue.isBlank()) return;
        for (String troop : queue.split(",")) {
            try {
                addTroop(Troops.values()[Integer.parseInt(troop)]);
            } catch (NumberFormatException ignored) {}
        }
    }

    /**
     * Gets the current version of the building.
     * E.g. percentage of fill.
     *
     * @return int - building version
     * @since 0.0.1
     */
    @Override
    public int getCurrentBuildingVersion() {
        return troopsQueue.isEmpty() ? 0 : 1;
    }
}
