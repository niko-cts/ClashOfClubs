package net.fununity.clashofclans.buildings.instances;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuildingWithHologram;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.hologram.APIHologram;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Location;

import java.util.*;

public abstract class GeneralHologramBuilding extends GeneralBuilding implements IBuildingWithHologram {

    private Location hologramLocation;

    /**
     * Instantiates the class.
     * @param uuid UUID - uuid of owner.
     * @param buildingUUID UUID - uuid of building.
     * @param building IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param rotation byte - the rotation of the building.
     * @param level int - the level of the building.
     * @since 0.0.1
     */
    public GeneralHologramBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, byte rotation, int level) {
        super(uuid, buildingUUID, building, coordinate, rotation, level);

        Location hologramLocation = getCoordinate().add(getBuilding().getSize()[0] / 2.0, 0, getBuilding().getSize()[1] / 2.0);
        hologramLocation.setY(BuildingLocationUtil.getHighestYCoordinate(hologramLocation) + 2);
        setLocation(hologramLocation);
    }

    /**
     * Sets the hologram location.
     * @param hologramLocation Location - location of the hologram.
     */
    public void setLocation(Location hologramLocation) {
        this.hologramLocation = hologramLocation;
    }

    /**
     * Updates the hologram for the player.
     * @param showText List<String> - displayed text in hologram
     * @since 0.0.1
     */
    @Override
    public void updateHologram(List<String> showText) {
        APIPlayer onlinePlayer = FunUnityAPI.getInstance().getPlayerHandler().getPlayer(getOwnerUUID());
        if (onlinePlayer != null) {
            onlinePlayer.hideHolograms(hologramLocation);
            onlinePlayer.showHologram(new APIHologram(hologramLocation, showText));
        }
    }

    @Override
    public void setCoordinate(Location coordinate) {
        super.setCoordinate(coordinate);

        FunUnityAPI.getInstance().getPlayerHandler().getPlayer(getOwnerUUID()).hideHolograms(hologramLocation);
        Location hologramLocation = getCoordinate().add(getBuilding().getSize()[0] / 2.0, 0, getBuilding().getSize()[1] / 2.0);
        hologramLocation.setY(BuildingLocationUtil.getHighestYCoordinate(hologramLocation) + 2);
        setLocation(hologramLocation);
        updateHologram(getShowText());
    }

    /**
     * Returns the list of hologram lines.
     * @return List<String> - hologram lines
     */
    public abstract List<String> getShowText();

    /**
     * Hides the hologram for the player.
     * @param player APIPlayer - the player to hide.
     * @since 0.0.1
     */
    @Override
    public void hideHologram(APIPlayer player) {
        player.hideHolograms(hologramLocation);
    }

}
