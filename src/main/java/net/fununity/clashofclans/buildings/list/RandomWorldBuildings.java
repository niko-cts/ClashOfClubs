package net.fununity.clashofclans.buildings.list;

import net.fununity.clashofclans.ResourceTypes;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.destroyables.RandomWorldBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDestroyableBuilding;
import net.fununity.clashofclans.buildings.interfaces.data.BuildingLevelData;
import net.fununity.clashofclans.language.TranslationKeys;
import org.bukkit.Material;

public enum RandomWorldBuildings implements IDestroyableBuilding {

    BUSH(TranslationKeys.COC_BUILDING_RANDOM_BUSH_NAME, TranslationKeys.COC_BUILDING_RANDOM_BUSH_DESCRIPTION, new int[]{3, 3}, 2, 10, new BuildingLevelData[]{new BuildingLevelData(10, 0, 1, 0)});

    private static final RandomWorldBuildings[] START_BUILDINGS = new RandomWorldBuildings[]{BUSH};

    /**
     * Get start buildings, that may appear at the start of the game.
     * @return RandomWorldBuildings[] - Array of start buildings.
     * @since 0.0.1
     */
    public static RandomWorldBuildings[] getStartBuildings() {
        return START_BUILDINGS;
    }

    private final String nameKey;
    private final String descriptionKey;
    private final int[] size;
    private final int gems;
    private final int exp;
    private final BuildingLevelData[] buildingLevelData;

    RandomWorldBuildings(String nameKey, String descriptionKey, int[] size, int gems, int exp, BuildingLevelData[] buildingLevelData) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.size = size;
        this.exp  = exp;
        this.gems = gems;
        this.buildingLevelData = buildingLevelData;
    }

    /**
     * Get the translation key of the building name.
     *
     * @return String - the name key.
     * @since 0.0.1
     */
    @Override
    public String getNameKey() {
        return nameKey;
    }

    /**
     * Get the translation key of the building description.
     *
     * @return String - the description key.
     * @since 0.0.1
     */
    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    /**
     * Get the size of the building. (x=size[0], z=size[1])
     *
     * @return int[] - the size of the building.
     * @since 0.0.1
     */
    @Override
    public int[] getSize() {
        return size;
    }

    /**
     * The resource type of the building.
     * Building cost type.
     *
     * @return {@link ResourceTypes} - the type of resource for the building.
     * @since 0.0.1
     */
    @Override
    public ResourceTypes getResourceType() {
        return ResourceTypes.FOOD;
    }

    /**
     * A list of the building level steps.
     *
     * @return {@link BuildingLevelData}[] - Each level step for upgrade stuff.
     * @since 0.0.1
     */
    @Override
    public BuildingLevelData[] getBuildingLevelData() {
        return buildingLevelData;
    }

    /**
     * Get the building class.
     * @return Class<? extends GeneralBuilding> - A class which extends the general building class.
     * @since 0.0.1
     */
    @Override
    public Class<? extends GeneralBuilding> getBuildingClass() {
        return RandomWorldBuilding.class;
    }

    /**
     * Get the material of the building.
     * @return Material - the displaying material.
     * @since 0.0.1
     */
    @Override
    public Material getMaterial() {
        return Material.AIR;
    }

    /**
     * Random amount how much gems a player gets from destroying the building
     * @return int - the amount of gems received.
     * @since 0.0.1
     */
    @Override
    public int getGems() {
        return gems;
    }

    /**
     * Amount of exp per destroy
     *
     * @return int - the amount of exp received.
     * @since 0.0.1
     */
    @Override
    public int getExp() {
        return exp;
    }

    /**
     * When the building is destroyed the player gets back the full money.
     * @return boolean - receive full money back.
     * @since 0.0.1
     */
    @Override
    public boolean receiveFullPayPrice() {
        return false;
    }
}
