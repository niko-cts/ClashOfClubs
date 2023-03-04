package net.fununity.clashofclans.player.buildingmode;

import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public class BuildingData {

    private final UUID uuid;
    private Location location;
    private byte rotation;

    public BuildingData(UUID uuid, Location location, byte rotation) {
        this.uuid = uuid;
        this.location = location;
        this.rotation = rotation;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Location getLocation() {
        return location;
    }

    public byte getRotation() {
        return rotation;
    }


    public void setLocation(Location location) {
        this.location = location;
    }

    public void setRotation(byte rotation) {
        this.rotation = rotation;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingData that = (BuildingData) o;
        return rotation == that.rotation && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, rotation);
    }
}
