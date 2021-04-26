package net.fununity.clashofclans.buildings.classes;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDefenseBuilding;
import org.bukkit.Location;

import java.util.UUID;

public class DefenseBuilding extends GeneralBuilding {

    /**
     * Instantiates the class.
     * @param uuid       UUID - uuid of owner.
     * @param building   IBuilding - the building class.
     * @param coordinate Location - the location of the building.
     * @param rotation   byte - the rotation of the building.
     * @param level      int - the level of the building.
     * @since 0.0.1
     */
    public DefenseBuilding(UUID uuid, IBuilding building, Location coordinate, byte rotation, int level) {
        super(uuid, building, coordinate, rotation, level);
    }

    @Override
    public IDefenseBuilding getBuilding() {
        return (IDefenseBuilding) super.getBuilding();
    }
}
