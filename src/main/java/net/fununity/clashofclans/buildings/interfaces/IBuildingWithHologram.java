package net.fununity.clashofclans.buildings.interfaces;

import net.fununity.main.api.hologram.APIHologram;

import java.util.List;

/**
 * Interface class for buildings with holograms on it.
 * @author Niko
 * @since 0.0.1
 */
public interface IBuildingWithHologram {

    /**
     * Get a list of holograms to display to the player.
     * @return List<APIHologram> - A list of all holograms on the building.
     * @since 0.0.1
     */
    List<APIHologram> getHolograms();

    /**
     * Updates the hologram for the player.
     * @since 0.0.1
     */
    void updateHologram();

}
