package net.fununity.clashofclans.buildings.instances;

import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.buildings.interfaces.IDefenseBuilding;
import net.fununity.clashofclans.util.CircleParticleUtil;
import net.fununity.main.api.inventory.CustomInventory;
import net.fununity.misc.translationhandler.translations.Language;
import org.bukkit.Location;

import java.util.UUID;

public class DefenseBuilding extends GeneralBuilding {


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
    public DefenseBuilding(UUID uuid, UUID buildingUUID, IBuilding building, Location coordinate, int[] baseRelatives, byte rotation, int level) {
        super(uuid, buildingUUID, building, coordinate, baseRelatives, rotation, level);
    }

    @Override
    public CustomInventory getInventory(Language language) {
        CircleParticleUtil.displayRadius(getBuildingUUID(), getCenterCoordinate(), getBuilding().getRadius(), 15);
        return super.getInventory(language);
    }

    @Override
    public IDefenseBuilding getBuilding() {
        return (IDefenseBuilding) super.getBuilding();
    }


    /**
     * Gets the damage to the building.
     * @return double - amount of damage.
     * @since 0.0.1
     */
    public float getDamage() {
        return getBuilding().getBuildingLevelData()[getLevel()-1].getDamage();
    }

    public double getRadius() {
        return getBuilding().getRadius();
    }
}
