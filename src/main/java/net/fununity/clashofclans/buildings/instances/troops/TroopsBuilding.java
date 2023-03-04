package net.fununity.clashofclans.buildings.instances.troops;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.ITroopBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.troops.ITroop;
import net.fununity.clashofclans.troops.Troops;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The troop building class.
 * @see GeneralBuilding
 * @author Niko
 * @since 0.0.1
 */
public class TroopsBuilding extends GeneralBuilding {

    private final ConcurrentMap<ITroop, Integer> troopAmount;

    /**
     * Instantiates the class.
     * @param uuid UUID - the uuid of the owner.
     * @param buildingUUID UUID - the uuid of the building.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public TroopsBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, byte rotation, int level) {
        this(uuid, buildingUUID, building, coordinate, rotation, level, new ConcurrentHashMap<>());
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
    public TroopsBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, byte rotation, int level, ConcurrentHashMap<ITroop, Integer> troopAmount) {
        super(uuid, buildingUUID, building, coordinate, rotation, level);
        this.troopAmount = troopAmount;
    }

    @Override
    public CustomInventory getInventory(Language language) {
        CustomInventory inventory = super.getInventory(language);
        CustomInventory menu = new CustomInventory(getBuildingTitle(language), 9 * 5);
        menu.setSpecialHolder(inventory.getSpecialHolder());

        for (int i = 0; i < inventory.getInventory().getContents().length; i++)
            menu.setItem(i, inventory.getInventory().getItem(i), inventory.getClickAction(i));

        menu.setItem(22, new ItemBuilder(Material.CHEST)
                        .addEnchantment(getCurrentSizeOfTroops() >= getMaxAmountOfTroops() ? Enchantment.ARROW_FIRE : null, 1, true, false)
                        .setName(language.getTranslation(TranslationKeys.COC_GUI_TROOPS_CONTAINER_NAME))
                        .setLore(language.getTranslation(TranslationKeys.COC_GUI_TROOPS_CONTAINER_LORE,
                                Arrays.asList("${current}", "${max}"), Arrays.asList(getCurrentSizeOfTroops() + "", getMaxAmountOfTroops()+"")).split(";")).craft());

        int i = 36 + (4 / Troops.values().length);
        for (Map.Entry<ITroop, Integer> entry : getTroopAmount().entrySet()) {
            List<String> lore = new ArrayList<>(Arrays.asList(entry.getKey().getDescription(language)));
            lore.addAll(Arrays.asList(language.getTranslation(TranslationKeys.COC_GUI_CONTAINER_LORE, "${amount}", entry.getValue()+"").split(";")));

            menu.setItem(i, new ItemBuilder(entry.getKey().getRepresentativeItem())
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .setName(entry.getKey().getName(language))
                    .setAmount(Math.min(Math.max(1, entry.getValue()), 64))
                    .setLore(lore).craft());
            i++;
        }

        menu.fill(UsefulItems.BACKGROUND_GRAY);
        return menu;
    }

    /**
     * Add the amount of troop.
     * @param troop ITroop - the troop.
     * @param amount int - the amount of troop.
     * @since 0.0.1
     */
    public void addTroopAmount(ITroop troop, int amount) {
        setTroopAmount(troop, Math.min(getMaxAmountOfTroops(), getTroopAmount(troop) + amount));
    }


    /**
     * Remove the amount of troop.
     * @param troop ITroop - the troop.
     * @param amount int - the amount of troop.
     * @since 0.0.1
     */
    public void removeTroopAmount(ITroop troop, int amount) {
        setTroopAmount(troop, Math.max(0, getTroopAmount(troop) - amount));
    }

    /**
     * Sets the amount of troop.
     * @param troop ITroop - the troop.
     * @param amount int - the amount of troop.
     * @since 0.0.1
     */
    private void setTroopAmount(ITroop troop, int amount) {
        troopAmount.put(troop, amount);
        ClashOfClubs.getInstance().getPlayerManager().forceUpdateInventory(this);
    }

    public int getTroopAmount(ITroop troop) {
        return troopAmount.getOrDefault(troop, 0);
    }

    /**
     * Get the amount of troop.
     * @return Map<ITroop, Integer> - Each troop with their amount
     * @since 0.0.1
     */
    public ConcurrentMap<ITroop, Integer> getTroopAmount() {
        return new ConcurrentHashMap<>(troopAmount);
    }

    @Override
    public ITroopBuilding getBuilding() {
        return (ITroopBuilding) super.getBuilding();
    }

    /**
     * Get the max amount of troops that fit in this building.
     * @return int - the troop size.
     * @since 0.0.1
     */
    public int getMaxAmountOfTroops() {
        return getBuilding().getBuildingLevelData()[getLevel() - 1].getMaxAmountOfTroops();
    }

    /**
     * Get the current amount * size of all troops in the building.
     * @return int - all troops
     * @since 0.0.1
     */
    public int getCurrentSizeOfTroops() {
        int amount = 0;
        for (Map.Entry<ITroop, Integer> entry : getTroopAmount().entrySet())
            amount += entry.getValue() * entry.getKey().getSize();
        return amount;
    }

}
