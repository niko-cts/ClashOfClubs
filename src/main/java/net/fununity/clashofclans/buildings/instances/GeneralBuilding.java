package net.fununity.clashofclans.buildings.instances;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.BuildingModeManager;
import net.fununity.clashofclans.buildings.BuildingsManager;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDifferentVersionBuildings;
import net.fununity.clashofclans.buildings.interfaces.IUpgradeDetails;
import net.fununity.clashofclans.buildings.list.Buildings;
import net.fununity.clashofclans.language.TranslationKeys;
import net.fununity.clashofclans.player.TutorialManager;
import net.fununity.main.api.inventory.ClickAction;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.main.api.item.ItemBuilder;
import net.fununity.main.api.item.UsefulItems;
import net.fununity.main.api.player.APIPlayer;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Class, which represents a general building.
 * Lookup all general buildings here: {@link net.fununity.clashofclans.buildings.list.Buildings}
 *
 * @author Niko
 * @since 0.0.1
 */
public class GeneralBuilding {

    private final UUID ownerUUID;
    private final UUID buildingUUID;
    private final IBuilding building;
    private byte rotation;
    private Location coordinate;
    private int[] baseRelative;
    private int level;
    private double currentHP;

    /**
     * Instantiates the class.
     *
     * @param uuid         UUID - uuid of owner.
     * @param buildingUUID UUID - uuid of building.
     * @param building     IBuilding - the building class.
     * @param baseLocation   Location - the location of the base.
     * @param baseRelative int[] - the x,z coordinate from the player base.
     * @param rotation     byte - the rotation of the building.
     * @param level        int - the level of the building.
     * @since 0.0.1
     */
    public GeneralBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location baseLocation, int[] baseRelative, byte rotation, int level) {
        this.ownerUUID = uuid;
        this.buildingUUID = buildingUUID;
        this.building = building;
        this.baseRelative = baseRelative;
        this.coordinate = baseLocation.clone().add(baseRelative[0], 0, baseRelative[1]);
        this.rotation = rotation;
        this.level = level;
        this.currentHP = getMaxHP();
    }

    /**
     * Returns the standard inventory of the building.
     * @param language Language - the language of the gui.
     * @return CustomInventory - the gui of the building.
     * @since 0.0.1
     */
    public CustomInventory getInventory(Language language) {
        CustomInventory menu = new CustomInventory(getBuildingTitle(language), 9 * 3);
        menu.setSpecialHolder(getBuildingUUID());
        menu.fill(UsefulItems.BACKGROUND_BLACK);

        List<String> lore = new ArrayList<>(Arrays.asList(language.getTranslation(getBuilding().getDescriptionKey()).split(";")));
        if (getBuilding() instanceof IUpgradeDetails)
            lore.addAll(((IUpgradeDetails) getBuilding()).getLoreDetails(getBuilding().getBuildingLevelData()[level - 1], language));

        menu.setItem(11, new ItemBuilder(Material.WRITABLE_BOOK).setName(language.getTranslation(getBuilding().getNameKey()))
                .setLore(lore).craft());

        if (getUpgradeCost() != -1) {
            List<String> upgradeLore = new ArrayList<>(Arrays.asList(language.getTranslation(getLevel() == 0 ? TranslationKeys.COC_GUI_BUILDING_REPAIR_LORE : TranslationKeys.COC_GUI_BUILDING_UPGRADE_LORE, "${cost}", "" + getBuilding().getBuildingCostType().getChatColor() + getUpgradeCost() + " " + language.getTranslation(getBuilding().getBuildingCostType().getNameKey())).split(";")));
            if (building instanceof IUpgradeDetails)
                upgradeLore.addAll(((IUpgradeDetails) building).getLoreDetails(building.getBuildingLevelData()[getLevel()], language));

            menu.setItem(15, new ItemBuilder(UsefulItems.UP_ARROW)
                    .setName(language.getTranslation(getLevel() == 0 ? TranslationKeys.COC_GUI_BUILDING_REPAIR_NAME : TranslationKeys.COC_GUI_BUILDING_UPGRADE_NAME))
                    .setLore(upgradeLore).craft(), new ClickAction() {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    if (BuildingsManager.getInstance().upgrade(GeneralBuilding.this)) {
                        setCloseInventory(true);
                        apiPlayer.playSound(Sound.ENTITY_VILLAGER_YES);
                    } else
                        apiPlayer.playSound(Sound.ENTITY_VILLAGER_NO);
                }
            });
        } else {
            menu.setItem(15, new ItemBuilder(UsefulItems.SKELETON_SKULL)
                    .setName(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_NO_UPGRADE_NAME)).craft());
        }

        if (getLevel() > 0) {
            menu.setItem(12, new ItemBuilder(Material.HEART_OF_THE_SEA).setName(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_HP_NAME)).setLore(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_HP_LORE, Arrays.asList("${max}", "${current}"), Arrays.asList(getMaxHP() + "", getCurrentHP() + "")).split(";")).craft());

            menu.setItem(14, new ItemBuilder(Material.PISTON).setName(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_MOVING_NAME)).setLore(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_MOVING_LORE)).craft(), new ClickAction(true) {
                @Override
                public void onClick(APIPlayer apiPlayer, ItemStack itemStack, int i) {
                    BuildingModeManager.getInstance().enterMovingMode(ClashOfClubs.getInstance().getPlayerManager().getPlayer(apiPlayer.getUniqueId()), apiPlayer, GeneralBuilding.this);
                }
            });


            if (getBuilding() == Buildings.TOWN_HALL) {
                List<IBuilding> needsToBuild = ClashOfClubs.getInstance().getPlayerManager().getPlayer(getOwnerUUID()).buildableBuildings();
                if (!needsToBuild.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    needsToBuild.forEach(b -> builder.append(ChatColor.GRAY).append("- ").append(language.getTranslation(b.getNameKey())).append(";"));
                    menu.setItem(15, new ItemBuilder(Material.BARRIER).setName(language.getTranslation(TranslationKeys.COC_GUI_BUILDING_CANTUPGRADEYET)).setLore(builder.toString().split(";")).craft());
                }
            }
        }

        if (getBuilding() != Buildings.TOWN_HALL && TutorialManager.getInstance().cantBuild(getOwnerUUID())) {
            for (int i = 14; i <= 15; i++) {
                if (menu.getInventory().getItem(i) != UsefulItems.BACKGROUND_BLACK) {
                    menu.setItem(i, new ItemBuilder(Material.BARRIER).setName(language.getTranslation(TranslationKeys.COC_PLAYER_REPAIR_TOWNHALL_FIRST_MESSAGE)).craft());
                    menu.setClickAction(i, null);
                }
            }
        }

        return menu;
    }

    /**
     * Get the building interface.
     *
     * @return {@link IBuilding} - the building class.
     * @since 0.0.1
     */
    public IBuilding getBuilding() {
        return building;
    }

    /**
     * Get the max hp of the building.
     *
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
     *
     * @return int - the upgrade cost.
     * @since 0.0.1
     */
    public int getUpgradeCost() {
        return getBuilding().getBuildingLevelData().length > level ? getBuilding().getBuildingLevelData()[level].getUpgradeCost() : -1;
    }

    /**
     * The build time of the building.
     * (-1 with no further build time)
     *
     * @return int - the upgrade cost.
     * @since 0.0.1
     */
    public int getBuildingDuration() {
        return getBuilding().getBuildingLevelData().length > level ? getBuilding().getBuildingLevelData()[level].getBuildTime() : 0;
    }

    /**
     * Get the xp the player gets, when achieving this building.
     *
     * @return int - getting xp.
     * @since 0.0.1
     */
    public int getExp() {
        return getBuilding().getBuildingLevelData().length > level ? getBuilding().getBuildingLevelData()[level].getBuildTime() : -1;
    }

    /**
     * Sets the current hp of the building.
     *
     * @param currentHP double - the current hp.
     * @since 0.0.1
     */
    public void setCurrentHP(double currentHP) {
        this.currentHP = currentHP;
    }

    /**
     * Get the current hp of the building.
     *
     * @return int - the current hp.
     * @since 0.0.1
     */
    public double getCurrentHP() {
        return currentHP;
    }

    /**
     * Get the location of the coordinate. (Min location)
     *
     * @return Location - the coordinate of the building.
     * @since 0.0.1
     */
    public Location getCoordinate() {
        return coordinate.clone();
    }

    /**
     * Get the base relative from the player base.
     * @return int[] - the x,z point from the player base.
     * @since 1.0.1
     */
    public int[] getBaseRelative() {
        return baseRelative;
    }


    /**
     * Set the location and base relative of the building. (Min location)
     *
     * @param baseLocation Location - the coordinate of the player base.
     * @param baseRelative int[] - the base relative position (x, z)
     * @since 0.0.1
     */
    public void setBaseRelative(Location baseLocation, int[] baseRelative) {
        this.baseRelative = baseRelative;
        this.coordinate = baseLocation.clone().add(baseRelative[0], 0, baseRelative[1]);
    }

    /**
     * Get center coordinate of the building.
     *
     * @return Location - center location.
     * @since 0.0.1
     */
    public Location getCenterCoordinate() {
        return new Location(coordinate.getWorld(),
                coordinate.getX() + getBuilding().getSize()[rotation == 1 || rotation == 3 ? 1 : 0] * 0.5,
                ClashOfClubs.getBaseYCoordinate() + 15,
                coordinate.getZ() + getBuilding().getSize()[rotation == 1 || rotation == 3 ? 0 : 1] * 0.5);
    }

    /**
     * Get the maximum location of the building. (Max location)
     * Note: Y is still on minimum level.
     *
     * @return Location - the maximum coordinate of the building.
     * @since 0.0.1
     */
    public Location getMaxCoordinate() {
        return getCoordinate().add(getBuilding().getSize()[rotation == 1 || rotation == 3 ? 1 : 0] - 1, ClashOfClubs.getBaseYCoordinate() + 30,
                getBuilding().getSize()[rotation == 1 || rotation == 3 ? 0 : 1] - 1);
    }

    /**
     * Get the rotation of the building (0 - 3)
     *
     * @return int - rotation of the building.
     * @since 0.0.1
     */
    public byte getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation of the building.
     *
     * @param rotation byte - the rotation of the building.
     * @since 0.0.1
     */
    public void setRotation(byte rotation) {
        this.rotation = rotation;
    }

    /**
     * The level of the building.
     *
     * @param level int - the new building level.
     * @since 0.0.1
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * The level of the building.
     *
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
     *
     * @return UUID - uuid of player.
     * @since 0.0.1
     */
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    /**
     * Get the uuid of this building.
     *
     * @return UUID - uuid of building.
     * @since 0.0.1
     */
    public UUID getBuildingUUID() {
        return buildingUUID;
    }

    /**
     * Get the id of the building. name-level(-version)
     *
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
        return level == that.level && building == that.building &&
                coordinate.getBlockX() == that.coordinate.getBlockX() && coordinate.getBlockY() == that.coordinate.getBlockY() && coordinate.getBlockZ() == that.coordinate.getBlockZ();
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
