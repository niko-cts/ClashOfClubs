package net.fununity.clashofclans.buildings.interfaces;

import net.fununity.main.api.hologram.APIHologram;
import net.fununity.main.api.player.APIPlayer;

import java.util.List;

/**
 * Interface class for buildings with holograms on it.
 * @author Niko
 * @since 0.0.1
 */
public interface IBuildingWithHologram {

    /**
     * Updates the hologram for the player.
     * @param showText List<String> - displayed text in hologram
     * @since 0.0.1
     */
    void updateHologram(List<String> showText);

    /**
     * Hides the hologram for the player.
     * @param player APIPlayer - the player to hide.
     * @since 0.0.1
     */
    void hideHologram(APIPlayer player);

}
