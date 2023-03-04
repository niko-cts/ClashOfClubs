package net.fununity.clashofclans.player.buildingmode;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Interface class for {@link ConstructionMode} and {@link MovingMode} to prevent code duplication in other class.
 * General methods for both building mode.
 * @author Niko
 * @since 0.0.2
 */
public interface IBuildingMode {

    /**
     * Sets the location of the current building.
     * @param location Location - new location.
     * @since 0.0.2
     */
    void setLocation(Location location);

    /**
     * Sets the rotation of the current building.
     * @param rotation byte - new rotation.
     * @since 0.0.2
     */
    void setRotation(byte rotation);

    /**
     * Gets the location of the current building.
     * @return Location - the current location.
     * @since 0.0.2
     */
    Location getLocation();

    /**
     * Gets the rotation of the current building.
     * @return byte - the current rotation.
     * @since 0.0.2
     */
    byte getRotation();

    /**
     * Get the building type.
     * @return {@link IBuilding} - the building.
     * @since 0.0.2
     */
    IBuilding getBuilding();

    /**
     * Gets the size dimension of the building.
     * @return int[] - the size dimension.
     * @since 0.0.2
     */
    int[] getSize();

    /**
     * Gets the building uuid.
     * @return UUID - the building uuid.
     * @since 0.0.2
     */
    UUID getBuildingUUID();

}
