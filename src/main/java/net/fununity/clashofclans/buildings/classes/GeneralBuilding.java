package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDifferentVersionBuildings;
import net.fununity.clashofclans.buildings.interfaces.IUpgradeDetails;
import net.fununity.clashofclans.buildings.list.Buildings;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.PlayerManager;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.common.util.SpecialChars;
import net.fununity.main.api.hologram.APIHologram;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.main.api.util.LocationUtil;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Class, which represents a general building.
 * Lookup all general buildings here: {@link net.fununity.clashofclans.buildings.list.Buildings}
 * @author Niko
 * @since 0.0.1
 */
public class GeneralBuilding {

    private final UUID uuid;
    private final IBuilding building;
    private byte rotation;
    private Location coordinate;
    private int level;
    private double currentHP;
    private APIHologram healthHologram;

    /**
     * Instantiates the class.
     * @param uuid UUID - uuid of owner.
     * @param building IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param rotation byte - the rotation of the building.
     * @param level int - the level of the building.
     * @since 0.0.1
     */
    public GeneralBuilding(UUID uuid, IBuilding building, Location coordinate, byte rotation, int level) {
        this.uuid = uuid;
        this.building = building;
        this.coordinate = coordinate;
        this.rotation = rotation;
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
        CustomInventory menu = new CustomInventory(getBuildingTitle(language), 9*3);
        menu.setSpecialHolder(getId() + "-" + getCoordinate().toString());
        menu.fill(UsefulItems.BACKGROUND_GRAY);
        menu.setItem(11, new ItemBuilder(Material.WRITABLE_BOOK).setName(language.getTranslation(getBuilding().getNameKey())).setLore(language.getTranslation(getBuilding().getDescriptionKey()).split(";")).craft());

        if (getLevel() != 0)
            menu.setItem(12, new ItemBuilder(Material.HEART_OF_THE_SEA).setName(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_HP_NAME)).setLore(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_HP_LORE, Arrays.asList("${max}", "${current}"), Arrays.asList(getMaxHP()+"", getCurrentHP()+"")).split(";")).craft());

        if (getBuilding() != Buildings.TOWN_HALL && PlayerManager.getInstance().getPlayer(getUuid()).getTownHallLevel() == 0) {
            menu.setItem(14, new ItemBuilder(UsefulItems.UP_ARROW).setName(language.getTranslation(TranslationKeys.COC_PLAYER_REPAIR_TOWNHALL_FIRST)).craft());
        } else if (getUpgradeCost() != -1) {
            List<String> upgradeLore = new ArrayList<>(Arrays.asList(language.getTranslation(getLevel() == 0 ? TranslationKeys.COC_GUI_BUILDING_REPAIR_LORE : TranslationKeys.COC_GUI_BUILDING_UPGRADE_LORE, "${cost}", "" + getBuilding().getResourceType().getChatColor() + getUpgradeCost() + " " + language.getTranslation(getBuilding().getResourceType().getNameKey())).split(";")));
            if (building instanceof IUpgradeDetails)
                upgradeLore.addAll(((IUpgradeDetails) building).getLoreDetails(building.getBuildingLevelData()[getLevel()], language));

            menu.setItem(14, new ItemBuilder(UsefulItems.UP_ARROW)
                    .setName(language.getTranslation(getLevel() == 0 ? TranslationKeys.COC_GUI_BUILDING_REPAIR_NAME : TranslationKeys.COC_GUI_BUILDING_UPGRADE_NAME))
                    .setLore(upgradeLore).craft(), new ClickAction() {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    if (BuildingsManager.getInstance().upgrade(GeneralBuilding.this))
                        setCloseInventory(true);
                }
            });
        }

        if(getLevel() != 0)
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
        if (level == 0)
            return 1;
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
    public int getMaxBuildingDuration() {
        return getBuilding().getBuildingLevelData().length > level ? getBuilding().getBuildingLevelData()[level].getBuildTime() : -1;
    }

    /**
     * Get the xp the player gets, when achieving this building.
     * @return int - getting xp.
     * @since 0.0.1
     */
    public int getExp() {
        return getBuilding().getBuildingLevelData().length > level ? getBuilding().getBuildingLevelData()[level].getBuildTime() : -1;
    }

    /**
     * Sets the current hp of the building.
     * @param currentHP double - the current hp.
     * @since 0.0.1
     */
    public void setCurrentHP(APIPlayer attacker, double currentHP) {
        this.currentHP = currentHP;
        if (this.healthHologram != null)
            attacker.hideHologram(healthHologram);
        Location centerCoordinate = getCenterCoordinate();
        centerCoordinate.setY(LocationUtil.getBlockHeight(centerCoordinate) + 2);
        DecimalFormat format = new DecimalFormat("0.0");
        this.healthHologram = new APIHologram(centerCoordinate, Collections.singletonList("§e" + format.format(currentHP) + "§7/§e" + getMaxHP() + " §c" + SpecialChars.HEART));
        attacker.showHologram(this.healthHologram);
    }

    /**
     * Get the current hp of the building.
     * @return int - the current hp.
     * @since 0.0.1
     */
    public double getCurrentHP() {
        return currentHP;
    }

    /**
     * Get the location of the coordinate. (Min location)
     * @return Location - the coordinate of the building.
     * @since 0.0.1
     */
    public Location getCoordinate() {
        return coordinate.clone();
    }

    /**
     * Get center coordinate of the building.
     * @return Location - center location.
     * @since 0.0.1
     */
    public Location getCenterCoordinate() {
        return getCoordinate().add(getBuilding().getSize()[0] / 2.0, 0, getBuilding().getSize()[1] / 2.0);
    }

    /**
     * Get the maximum location of the building. (Max location)
     * Note: Y is still on minimum level.
     * @return Location - the maximum coordinate of the building.
     * @since 0.0.1
     */
    public Location getMaxCoordinate() {
        int[] coordinateFromRotation = BuildingLocationUtil.getCoordinateFromRotation(getRotation(), getBuilding().getSize()[0], getBuilding().getSize()[1]);
        return getCoordinate().add(coordinateFromRotation[0], 0, coordinateFromRotation[1]);
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
     * Get the rotation of the building (0 - 3)
     * @return int - rotation of the building.
     * @since 0.0.1
     */
    public byte getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation of the building.
     * @param rotation byte - the rotation of the building.
     * @since 0.0.1
     */
    public void setRotation(byte rotation) {
        this.rotation = rotation;
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

    protected String getBuildingTitle(Language language) {
        return language.getTranslation(TranslationKeys.COC_GUI_BUILDING_NAME, Arrays.asList("${name}", "${level}"), Arrays.asList(language.getTranslation(getBuilding().getNameKey()), getLevel() + ""));
    }

    /**
     * Get the owner of this building.
     * @return UUID - uuid of player.
     * @since 0.0.1
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get the id of the building. name-level(-version)
     * @return String - the building id.
     * @since 0.0.1
     */
    public String getId() {
        return new StringBuilder().append(building.name()).append("-").append(level).append(this instanceof IDifferentVersionBuildings ? "-" + ((IDifferentVersionBuildings) this).getCurrentBuildingVersion() : "").toString();
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

    @Override
    public String toString() {
        return "GeneralBuilding{" +
                "building=" + building +
                ", coordinate=" + coordinate.getBlockX() + ":" + coordinate.getBlockZ() +
                '}';
    }

}
