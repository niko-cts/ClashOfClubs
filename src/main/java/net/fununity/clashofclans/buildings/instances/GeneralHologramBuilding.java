package net.fununity.clashofclans.buildings.instances;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuildingWithHologram;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.FunUnityAPI;
import net.fununity.main.api.hologram.APIHologram;
import net.fununity.main.api.player.APIPlayer;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

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
    public GeneralHologramBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, int[] baseRelatives, byte rotation, int level) {
        super(uuid, buildingUUID, building, coordinate, baseRelatives, rotation, level);

        setLocation(getCenterCoordinate());
        setY(BuildingLocationUtil.getHighestYCoordinate(hologramLocation) + 2);
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
            setY(BuildingLocationUtil.getHighestYCoordinate(hologramLocation) + 2);
            onlinePlayer.showHologram(new APIHologram(hologramLocation, showText));
        }
    }

    @Override
    public void setBaseRelative(Location baseLocation, int[] baseRelative) {
        super.setBaseRelative(baseLocation, baseRelative);

        FunUnityAPI.getInstance().getPlayerHandler().getPlayer(getOwnerUUID()).hideHolograms(hologramLocation);
        setLocation(getCenterCoordinate());
        setY(BuildingLocationUtil.getHighestYCoordinate(hologramLocation) + 2);
        updateHologram(getShowText());
    }

    public void setY(int y) {
        if (y != hologramLocation.getBlockY())
            hologramLocation.setY(y);
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
