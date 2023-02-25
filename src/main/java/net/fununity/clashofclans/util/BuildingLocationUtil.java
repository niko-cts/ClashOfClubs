package net.fununity.clashofclans.util;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.destroyables.RandomWorldBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.main.api.common.util.RandomUtil;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for displaying etc.
 * @author Niko
 * @since 0.0.
 */
public class BuildingLocationUtil {

    private BuildingLocationUtil() {
        throw new UnsupportedOperationException("BuildingLocationUtil is a utility class.");
    }

    /**
     * Removes the changed building ground.
     * @param player Player - the player.
     * @param buildingMode Object[] - the building mode of the player.
     * @see net.fununity.clashofclans.player.CoCPlayer#getBuildingMode()
     * @since 0.0.1
     */
    public static void removeBuildingGround(Player player, Object[] buildingMode) {
        getAllLocationsOnGround(buildingMode).forEach(b -> player.sendBlockChange(b, b.getBlock().getBlockData()));
    }

    /**
     * Creates a fake block ground for the given player.
     * @param player Player - the player.
     * @param coCPlayer CoCPlayer - CoCPlayer instance.
     * @since 0.0.1
     */
    public static void createFakeGround(Player player, CoCPlayer coCPlayer) {
        Object[] buildingMode = coCPlayer.getBuildingMode();
        GeneralBuilding building = buildingMode[1] instanceof GeneralBuilding ? (GeneralBuilding) buildingMode[1] : null;

        List<Location> blocks = getAllLocationsOnGround(buildingMode);

        int[] size = building == null ? ((IBuilding) buildingMode[1]).getSize() : building.getBuilding().getSize();
        Location originalCoordinate = getCoordinate(size, (byte) buildingMode[2], (Location) buildingMode[0]);
        BlockData data = BuildingLocationUtil.otherBuildingInWay(coCPlayer) ? Material.REDSTONE_BLOCK.createBlockData() : Material.EMERALD_BLOCK.createBlockData();
        blocks.forEach(b -> player.sendBlockChange(b, LocationUtil.equalsLocationBlock(originalCoordinate, b) ? Material.LAPIS_BLOCK.createBlockData() : data));
    }


    /**
     * Get all building blocks from the current building.
     * @param buildingMode Object[] - the building mode of the player.
     * @see net.fununity.clashofclans.player.CoCPlayer#getBuildingMode()
     * @return List<Location> - all blocks in the building area.
     * @since 0.0.1
     */
    public static List<Location> getAllLocationsOnGround(Object[] buildingMode) {
        Location location = (Location) buildingMode[0];
        if (location == null) return new ArrayList<>();
        return getAllLocationsOnGround(location, getSize(buildingMode));
    }

    /**
     * Get all building blocks from the current building.
     * @param location Location - the start location.
     * @param size int[] - the size of the building.
     * @return List<Location> - all Location in the building area.
     * @since 0.0.1
     */
    public static List<Location> getAllLocationsOnGround(Location location, int[] size) {
        List<Location> blocks = new ArrayList<>();
        int maxX = location.getBlockX() + size[0];
        boolean maxXBigger = location.getBlockX() < maxX;
        int maxZ = location.getBlockZ() + size[1];
        boolean maxZBigger = location.getBlockZ() < maxZ;

        for (int x = location.getBlockX(); maxXBigger ? x < maxX : x > maxX; x = (maxXBigger ? x + 1 : x - 1)) {
            for (int z = location.getBlockZ(); maxZBigger ? z < maxZ : z > maxZ; z = (maxZBigger ? z + 1 : z - 1))
                blocks.add(new Location(location.getWorld(), x, location.getBlockY(), z));
        }
        return blocks;
    }



    /**
     * Get the size of the building. (Can be negative)
     * @param buildingMode Object[] - the building mode of the player.
     * @see net.fununity.clashofclans.player.CoCPlayer#getBuildingMode()
     * @return int[] - the size of the building with rotation.
     * @since 0.0.1
     */
    private static int[] getSize(Object[] buildingMode) {
        byte rotation = (byte) buildingMode[2];
        int[] size;
        if (buildingMode[1] instanceof GeneralBuilding) {
            size = ((GeneralBuilding) buildingMode[1]).getBuilding().getSize();
        } else if (buildingMode[1] instanceof IBuilding) {
            size = ((IBuilding) buildingMode[1]).getSize();
        } else
            return new int[0];
        return getCoordinateFromRotation(rotation, size[0], size[1]);
    }


    /**
     * Returns an array with the rotated x and z coords.
     * @param rotation byte - the rotation (0 - 3)
     * @param x int - alter x coordinate
     * @param z int - alter z coordinate
     * @return int[] - new x, z values
     * @since 0.0.1
     */
    public static int[] getCoordinateFromRotation(byte rotation, int x, int z) {
        switch (rotation) {
            case 1:
                return new int[]{z, -x};
            case 2:
                return new int[]{-x, -z};
            case 3:
                return new int[]{-z, x};
            default:
                return new int[]{x, z};
        }
    }

    public static Location getCoordinate(GeneralBuilding building) {
        return getCoordinate(building.getBuilding().getSize(), building.getRotation(), building.getCoordinate().clone());
    }

    /**
     * Subtracts the coordinate to the new minimum coordinate.
     * @param size int[] - x,z size of the building.
     * @param rotation byte - the rotation 0-3
     * @param coordinate Location - the old location.
     * @return Location - the new location subtracted with the rotation.
     * @since 0.0.1
     */
    public static Location getCoordinate(int[] size, byte rotation, Location coordinate) {
        switch (rotation) {
            case 1:
                return coordinate.clone().subtract(0, 0, size[0]-1);
            case 2:
                return coordinate.clone().subtract(size[0]-1, 0, size[1]-1);
            case 3:
                return coordinate.clone().subtract(size[1]-1, 0, 0);
            default:
                return coordinate.clone();
        }
    }

    /**
     * Reverses the location back to the original minimum location the building was saved..
     * @see net.fununity.clashofclans.buildings.Schematics#createBuilding(GeneralBuilding)
     * @param building {@link GeneralBuilding} - the building.
     * @return Location - the original location of the building.
     * @since 0.0.1
     */
    public static Location getReversedCoordinate(GeneralBuilding building) {
        int[] size = building.getBuilding().getSize();
        switch (building.getRotation()) {
            case 1:
                return building.getCoordinate().add(0, 0, size[0]-1);
            case 2:
                return building.getCoordinate().add(size[0]-1, 0, size[1]-1);
            case 3:
                return building.getCoordinate().add(size[1]-1, 0, 0);
            default:
                return building.getCoordinate();
        }
    }


    private static final int RDM_SPACE = 2;

    /**
     * Get a random location for a {@link RandomWorldBuilding}.
     * @param playerBase Location - the player base location.
     * @param startBuildings List<GeneralBuilding> - the buildings to check.
     * @param size int[] - the size of the building.
     * @return Location - the random location.
     * @since 0.0.1
     */
    public static Location getRandomBuildingLocation(Location playerBase, List<GeneralBuilding> startBuildings, int[] size) {
        for (int i = 0; i < 100; i++) {
            Location rdm = playerBase.clone().add(ClashOfClubs.getBaseBackground() + 1, 0, ClashOfClubs.getBaseBackground() + 1)
                    .add(RandomUtil.getRandomInt(ClashOfClubs.getBaseSize() - ClashOfClubs.getBaseBackground()), 0, RandomUtil.getRandomInt(ClashOfClubs.getBaseSize() - ClashOfClubs.getBaseBackground()));

            List<Location> blocks = getAllLocationsOnGround(rdm.clone().subtract(RDM_SPACE, 0, RDM_SPACE), new int[]{size[0] + RDM_SPACE, size[1] + RDM_SPACE});

            if (startBuildings.stream().anyMatch(b -> blocks.stream().noneMatch(block -> LocationUtil.isBetween(b.getCoordinate(), block, b.getMaxCoordinate()))))
                return rdm;
        }
        return null;
    }

    /**
     * Get if there is a other building in way, while player is in building mode.
     * @param player CoCPlayer - the player to check.
     * @return boolean - the building the player want to interact can not be placed there.
     * @since 0.0.1
     */
    public static boolean otherBuildingInWay(CoCPlayer player) {
        Location minBuildable = player.getBaseStartLocation().add(ClashOfClubs.getBaseBackground(), 0, ClashOfClubs.getBaseBackground());
        Location maxBuildable = player.getBaseEndLocation().subtract(ClashOfClubs.getBaseBackground(), 0, ClashOfClubs.getBaseBackground());
        GeneralBuilding building = player.getBuildingMode()[1] instanceof GeneralBuilding ? (GeneralBuilding) player.getBuildingMode()[1] : null;

        for (Location blockLoc : BuildingLocationUtil.getAllLocationsOnGround(player.getBuildingMode())) {
            if (!LocationUtil.isBetween(minBuildable, blockLoc, maxBuildable)) {
                return true;
            }

            GeneralBuilding generalBuilding = player.getAllBuildings().stream().filter(b -> LocationUtil.isBetween(b.getCoordinate(), blockLoc,
                    b.getMaxCoordinate())).findFirst().orElse(null);
            if (building == null && generalBuilding != null ||

                    building != null && generalBuilding != null && generalBuilding.getBuilding() != building.getBuilding()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Transfers the 'facing' of a block.
     * @param blockData String - block data the original block data.
     * @param rotation byte - the rotation of the building.
     * @return String - the rotated blockdata.
     * @since 0.0.1
     */
    public static String getBlockDataFromRotation(String blockData, byte rotation) {
        if (!blockData.contains("facing=") || rotation == 0)
            return blockData;

        StringBuilder currentFacing = new StringBuilder();
        int i = blockData.lastIndexOf("facing=") + 7;
        while (i < blockData.length()) {
            if (blockData.charAt(i) == ',' || blockData.charAt(i) == ']')
                break;
            currentFacing.append(blockData.charAt(i));
            i++;
        }

        switch (rotation) {
            case 1:
                switch (currentFacing.toString()) {
                    case "south":
                        return blockData.replace("facing=" + currentFacing, "facing=east");
                    case "west":
                        return blockData.replace("facing=" + currentFacing, "facing=south");
                    case "north":
                        return blockData.replace("facing=" + currentFacing, "facing=west");
                    case "east":
                        return blockData.replace("facing=" + currentFacing, "facing=north");
                    default:
                        return blockData;
                }
            case 2:
                switch (currentFacing.toString()) {
                    case "south":
                        return blockData.replace("facing=" + currentFacing, "facing=north");
                    case "west":
                        return blockData.replace("facing=" + currentFacing, "facing=east");
                    case "north":
                        return blockData.replace("facing=" + currentFacing, "facing=south");
                    case "east":
                        return blockData.replace("facing=" + currentFacing, "facing=west");
                    default:
                        return blockData;
                }
            default:
                switch (currentFacing.toString()) {
                    case "south":
                        return blockData.replace("facing=" + currentFacing, "facing=west");
                    case "west":
                        return blockData.replace("facing=" + currentFacing, "facing=north");
                    case "north":
                        return blockData.replace("facing=" + currentFacing, "facing=east");
                    case "east":
                        return blockData.replace("facing=" + currentFacing, "facing=south");
                    default:
                        return blockData;
                }
        }
    }

    /**
     * Teleports any player next to the given building, if standing in there
     * @param building {@link GeneralBuilding} - the building to check.
     * @since 0.0.1
     */
    public static void savePlayerFromBuilding(GeneralBuilding building) {
        for (Player visitor : Bukkit.getOnlinePlayers()) {
            if (LocationUtil.isBetween(building.getCoordinate(), visitor.getLocation(), building.getMaxCoordinate().add(0, 80, 0))) {
                Location teleport = building.getCoordinate().subtract(1, 0, 1);
                teleport.setY(BuildingLocationUtil.getHighestYCoordinate(teleport) + 1);
                Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> visitor.teleport(teleport));
            }
        }
    }

    /**
     * Get the highest y coordinate of the given location without the barriers.
     * @param location Location - the location.
     * @return int - highest y coordinate without barriers.
     * @since 0.0.1
     */
    public static int getHighestYCoordinate(Location location) {
        Location highestLoc = location.getWorld().getHighestBlockAt(location).getLocation();
        while (highestLoc.getBlock().getType() == Material.AIR || highestLoc.getBlock().getType() == Material.BARRIER) {
            highestLoc.subtract(0, 1, 0);
            if (highestLoc.getBlockY() < ClashOfClubs.getBaseYCoordinate())
                break;
        }
        return highestLoc.getBlockY();
    }
}
