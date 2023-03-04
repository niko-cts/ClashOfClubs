package net.fununity.clashofclans.buildings.instances;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDifferentVersionBuildings;
import net.fununity.clashofclans.buildings.list.WallBuildings;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Class, which represents a wall/gate.
 * Lookup all wall buildings here: {@link net.fununity.clashofclans.buildings.list.WallBuildings}
 * @author Niko
 * @since 0.0.1
 */
public class WallBuilding extends GeneralBuilding implements IDifferentVersionBuildings {

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
    public WallBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, byte rotation, int level) {
        super(uuid, buildingUUID, building, coordinate, rotation, level);
    }

    @Override
    public WallBuildings getBuilding() {
        return (WallBuildings) super.getBuilding();
    }

    /**
     * Called when the version was updated.
     * @param schematic boolean - schematic change
     * @return boolean - needs to rebuild building
     * @since 0.0.1
     */
    @Override
    public boolean updateVersion(boolean schematic) {
        return schematic;
    }

    /**
     * Gets the current version of the building.
     * E.g. percentage of fill.
     * @return int - building version
     * @since 0.0.1
     */
    @Override
    public int getCurrentBuildingVersion() {
        return getBuilding() == WallBuildings.GATE && ClashOfClubs.getInstance().isAttackingServer() ? 1 : 0;
    }
}
