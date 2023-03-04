package net.fununity.clashofclans.player.buildingmode;

import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Helper class for moving old buildings.
 * @author Niko
 * @since 0.0.2
 */
public class MovingMode implements IBuildingMode {

    private final GeneralBuilding movingBuilding;
    private final BuildingData buildingData;

    public MovingMode(GeneralBuilding movingBuilding, Location location) {
        this.movingBuilding = movingBuilding;
        this.buildingData = new BuildingData(movingBuilding.getBuildingUUID(), location, movingBuilding.getRotation());
    }

    public void setLocation(Location location) {
        this.buildingData.setLocation(location);
    }

    public void setRotation(byte rotation) {
        this.buildingData.setRotation(rotation);
    }

    /**
     * Gets the location of the current building.
     *
     * @return Location - the current location.
     * @since 0.0.2
     */
    @Override
    public Location getLocation() {
        return this.buildingData.getLocation();
    }

    /**
     * Gets the rotation of the current building.
     *
     * @return byte - the current rotation.
     * @since 0.0.2
     */
    @Override
    public byte getRotation() {
        return this.buildingData.getRotation();
    }

    /**
     * Get the building type.
     *
     * @return {@link IBuilding} - the building.
     * @since 0.0.2
     */
    @Override
    public IBuilding getBuilding() {
        return getMovingBuilding().getBuilding();
    }

    /**
     * Gets the size dimension of the building.
     *
     * @return int[] - the size dimension.
     * @since 0.0.2
     */
    @Override
    public int[] getSize() {
        return getMovingBuilding().getBuilding().getSize();
    }

    /**
     * Gets the building uuid.
     *
     * @return UUID - the building uuid.
     * @since 0.0.2
     */
    @Override
    public UUID getBuildingUUID() {
        return getMovingBuilding().getBuildingUUID();
    }

    public GeneralBuilding getMovingBuilding() {
        return movingBuilding;
    }
}
