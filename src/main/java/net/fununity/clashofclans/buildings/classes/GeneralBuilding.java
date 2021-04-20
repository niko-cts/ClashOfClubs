package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * The general building class.
 * @author Niko
 * @since 0.0.1
 */
public class GeneralBuilding {

    private final UUID uuid;
    private final IBuilding building;
    private Location coordinate;
    private int level;
    private int currentHP;

    /**
     * Instantiates the class.
     * @param building IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param level int - the level of the building.
     * @since 0.0.1
     */
    public GeneralBuilding(UUID uuid, IBuilding building, Location coordinate, int level) {
        this.uuid = uuid;
        this.building = building;
        this.coordinate = coordinate;
        this.level = level;
        this.currentHP = getMaxHP();
    }

    /**
     * The standard inventory of the building.
     * @param language Language - the language of the gui.
     * @return CustomInventory - the gui of the building.
     * @since 0.0.1
     */
    public CustomInventory getInventory(Language language) {
        CustomInventory menu = new CustomInventory(language.getTranslation(getBuilding().getNameKey()) + " - " + getLevel(), 9*3);
        menu.setSpecialHolder(getId() + "-" + getCoordinate().toString());
        menu.fill(UsefulItems.BACKGROUND_BLACK);
        menu.setItem(11, new ItemBuilder(Material.WRITABLE_BOOK).setName(language.getTranslation(getBuilding().getNameKey())).setLore(language.getTranslation(getBuilding().getDescriptionKey()).split(";")).craft());
        menu.setItem(12, new ItemBuilder(Material.HEART_OF_THE_SEA).setName(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_HP_NAME)).setLore(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_HP_LORE, Arrays.asList("${max}", "${current}"), Arrays.asList(getMaxHP()+"", getCurrentHP()+"")).split(";")).craft());
        if (getUpgradeCost() != -1) {
            menu.setItem(14, new ItemBuilder(UsefulItems.UP_ARROW).setName(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_UPGRADE_NAME))
                    .setLore(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_UPGRADE_LORE, "${cost}", ""+getUpgradeCost() + " " + language.getTranslation(getBuilding().getResourceType().getNameKey())).split(";")).craft(), new ClickAction() {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    if(getUpgradeCost() > PlayerManager.getInstance().getPlayer(getUuid()).getResource(getBuilding().getResourceType())) return;
                    BuildingsManager.getInstance().upgrade(getUuid(), GeneralBuilding.this);
                    setCloseInventory(true);
                }
            });
        }
        menu.setItem(15, new ItemBuilder(Material.PISTON).setName(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_MOVING_NAME)).setLore(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_MOVING_LORE)).craft(), new ClickAction(true) {
            @Override
            public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                BuildingsManager.getInstance().enterMovingMode(apiPlayer, GeneralBuilding.this);
            }
        });

        return menu;
    }

    /**
     * Get the building interface.
     * @return {@link IBuilding} - the building class.
     * @since 0.0.1
     */
    public IBuilding getBuilding() {
        return building;
    }

    /**
     * Get the max hp of the building.
     * @return int - the hp of the building.
     * @since 0.0.1
     */
    public int getMaxHP() {
        return getBuilding().getBuildingLevelData().length > level - 1 ? getBuilding().getBuildingLevelData()[level - 1].getMaxHP() : -1;
    }

    /**
     * The upgrade cost of the building.
     * (-1 with no further upgrade)
     * @return int - the upgrade cost.
     * @since 0.0.1
     */
    public int getUpgradeCost() {
        return getBuilding().getBuildingLevelData().length > level ? getBuilding().getBuildingLevelData()[level].getUpgradeCost() : -1;
    }
    /**
     * The build time of the building.
     * (-1 with no further build time)
     * @return int - the upgrade cost.
     * @since 0.0.1
     */
    public int getBuildTime() {
        return getBuilding().getBuildingLevelData().length > level - 1 ? getBuilding().getBuildingLevelData()[level - 1].getBuildTime() : -1;
    }

    /**
     * Sets the current hp of the building.
     * @param currentHP int - the current hp.
     * @since 0.0.1
     */
    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    /**
     * Get the current hp of the building.
     * @return int - the current hp.
     * @since 0.0.1
     */
    public int getCurrentHP() {
        return currentHP;
    }

    /**
     * Get the location of the coordinate. (Min location)
     * @return Location - the coordinate of the building.
     * @since 0.0.1
     */
    public Location getCoordinate() {
        return coordinate;
    }

    /**
     * Set the location of the building. (Min location)
     * @param coordinate Location - the coordinate of the building.
     * @since 0.0.1
     */
    public void setCoordinate(Location coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * The level of the building.
     * @param level int - the new building level.
     * @since 0.0.1
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * The level of the building.
     * @return int - building level.
     * @since 0.0.1
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get the owner of this building.
     * @return UUID - uuid of player.
     * @since 0.0.1
     */
    public UUID getUuid() {
        return uuid;
    }

    public String getId() {
        return new StringBuilder().append(building.name()).append("-").append(level).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneralBuilding that = (GeneralBuilding) o;
        return level == that.level && building == that.building && Objects.equals(coordinate, that.coordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(building, coordinate, level);
    }

}
