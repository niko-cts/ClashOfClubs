package net.fununity.clashofclans.player.buildingmode;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Helper class for constructing new buildings.
 * @author Niko
 * @since 0.0.2
 */
public class ConstructionMode implements IBuildingMode {

    private final IBuilding constructingBuilding;
    private final List<BuildingData> buildings;


    /**
     * Instantiate the class with the constructing building and the first location and rotation
     * @param constructingBuilding {@link IBuilding} - the building to create.
     * @param location Location - the first location to create.
     */
    public ConstructionMode(IBuilding constructingBuilding, Location location) {
        this.constructingBuilding = constructingBuilding;
        this.buildings = new ArrayList<>(List.of(new BuildingData(UUID.randomUUID(), location, (byte) 0)));
    }

    /**
     * Sets the location of the last building in the list.
     * @param location Location - location the last building in list should be.
     */
    public void setLocation(Location location) {
        this.buildings.get(buildings.size() - 1).setLocation(location);
    }

    /**
     * Sets the rotation of the last building in the list.
     * @param rotation byte - rotation the last building in list should be.
     */
    public void setRotation(byte rotation) {
        this.buildings.get(buildings.size() - 1).setRotation(rotation);
    }

    /**
     * Gets the location of the current building.
     *
     * @return Location - the current location.
     * @since 0.0.2
     */
    @Override
    public Location getLocation() {
        return this.buildings.get(buildings.size() - 1).getLocation();
    }

    /**
     * Gets the rotation of the current building.
     *
     * @return byte - the current rotation.
     * @since 0.0.2
     */
    @Override
    public byte getRotation() {
        return this.buildings.get(buildings.size() - 1).getRotation();
    }

    /**
     * Gets the size dimension of the building.
     *
     * @return int[] - the size dimension.
     * @since 0.0.2
     */
    @Override
    public int[] getSize() {
        return this.getBuilding().getSize();
    }

    /**
     * Gets the building uuid.
     *
     * @return UUID - the building uuid.
     * @since 0.0.2
     */
    @Override
    public UUID getBuildingUUID() {
        return this.buildings.get(buildings.size() - 1).getUniqueId();
    }

    /**
     * Get a list of all buildings to construct
     * @return List<{@link BuildingData}> - list of all constructing buildings
     */
    public List<BuildingData> getBuildings() {
        return buildings;
    }


    /**
     * Get the constructing building type.
     * @return IBuilding - the building to construct.
     */
    public IBuilding getBuilding() {
        return constructingBuilding;
    }
}
