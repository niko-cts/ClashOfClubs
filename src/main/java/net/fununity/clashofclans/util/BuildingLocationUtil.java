package net.fununity.clashofclans.util;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.instances.destroyables.RandomWorldBuilding;
import net.fununity.clashofclans.player.CoCPlayer;
import net.fununity.clashofclans.player.buildingmode.BuildingData;
import net.fununity.clashofclans.player.buildingmode.ConstructionMode;
import net.fununity.clashofclans.player.buildingmode.IBuildingMode;
import net.fununity.main.api.common.util.RandomUtil;
import net.fununity.main.api.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A utility class for displaying etc.
 *
 * @author Niko
 * @since 0.0.
 */
public class BuildingLocationUtil {

    private BuildingLocationUtil() {
        throw new UnsupportedOperationException("BuildingLocationUtil is a utility class.");
    }

    /**
     * Removes the changed building ground.
     *
     * @param player       Player - the player.
     * @param buildingMode Object[] - the building mode of the player.
     * @see net.fununity.clashofclans.player.CoCPlayer#getBuildingMode()
     * @since 0.0.1
     */
    public static void removeBuildingModeDecorations(Player player, IBuildingMode buildingMode) {
        CircleParticleUtil.deleteParticleTask(buildingMode);
        getAllLocationsOnGround(buildingMode).forEach(b -> player.sendBlockChange(b, b.getBlock().getBlockData()));
    }

    /**
     * Creates a fake block ground for the given player.
     *
     * @param player    Player - the player.
     * @param coCPlayer CoCPlayer - CoCPlayer instance.
     * @since 0.0.1
     */
    public static void createBuildingModeDecoration(Player player, CoCPlayer coCPlayer) {
        IBuildingMode buildingMode = coCPlayer.getBuildingMode();

        CircleParticleUtil.createParticleTask(buildingMode); // creates the particle circle if it is a defense building

        List<Location> original = new ArrayList<>();
        if (buildingMode instanceof ConstructionMode) {
            for (BuildingData building : ((ConstructionMode) coCPlayer.getBuildingMode()).getBuildings()) {
                original.add(getRealMinimum(buildingMode.getSize(), building.getRotation(), building.getLocation()));
             }
        } else {
            original.add(getRealMinimum(buildingMode.getSize(), buildingMode.getRotation(), buildingMode.getLocation()));
        }

        List<Location> blocks = getAllLocationsOnGround(buildingMode);
        BlockData data = BuildingLocationUtil.otherBuildingInWay(coCPlayer, blocks) ? Material.REDSTONE_BLOCK.createBlockData() : Material.EMERALD_BLOCK.createBlockData();
        blocks.forEach(b -> player.sendBlockChange(b, containsLocation(original, b) ? Material.LAPIS_BLOCK.createBlockData() : data));
    }

    private static boolean containsLocation(List<Location> original, Location location) {
        return original.stream().anyMatch(o -> o.getBlockX() == location.getBlockX() && o.getBlockZ() == location.getBlockZ());
    }

    /**
     * Gets the center location from the building mode.
     *
     * @param buildingMode IBuildingMode - the building mode of the player.
     * @return Location - the center location.
     */
    public static Location getCenterLocation(IBuildingMode buildingMode) {
        int[] size = buildingMode.getSize();
        byte rotation = buildingMode.getRotation();
        Location min = getRealMinimum(size, rotation, buildingMode.getLocation());
        Location max = min.clone().add(size[rotation == 1 || rotation == 3 ? 1 : 0] - 1, 0, size[rotation == 1 || rotation == 3 ? 0 : 1] - 1);
        return new Location(min.getWorld(), (min.getBlockX() + max.getBlockX()) * 0.5, ClashOfClubs.getBaseYCoordinate() + 2, (min.getBlockZ() + max.getBlockZ()) * 0.5);
    }

    /**
     * Get all building blocks from the current building.
     *
     * @param buildingMode IBuildingMode - the building mode of the player.
     * @return List<Location> - all blocks in the building area.
     * @see net.fununity.clashofclans.player.CoCPlayer#getBuildingMode()
     * @since 0.0.1
     */
    public static List<Location> getAllLocationsOnGround(IBuildingMode buildingMode) {
        List<Location> locations = new ArrayList<>();
        if (buildingMode instanceof ConstructionMode) {
            ((ConstructionMode) buildingMode).getBuildings().forEach(buildingData -> locations.addAll(getAllLocationsOnGround(buildingData.getLocation(),
                    getXZDimensionFromRotation(buildingData.getRotation(), buildingMode.getSize()[0], buildingMode.getSize()[1]))));
        } else {
            locations.addAll(getAllLocationsOnGround(buildingMode.getLocation(),
                    getXZDimensionFromRotation(buildingMode.getRotation(), buildingMode.getSize()[0], buildingMode.getSize()[1])));
        }
        return locations;
    }

    /**
     * Get all building blocks from the current building.
     *
     * @param minimumLocation Location - the start location.
     * @param xzTransfer     int[] - the size of the building.
     * @return List<Location> - all Location in the building area.
     * @since 0.0.1
     */
    public static List<Location> getAllLocationsOnGround(Location minimumLocation, int[] xzTransfer) {
        List<Location> blocks = new ArrayList<>();
        int maxX = minimumLocation.getBlockX() + xzTransfer[0];
        boolean maxXBigger = minimumLocation.getBlockX() < maxX;
        int maxZ = minimumLocation.getBlockZ() + xzTransfer[1];
        boolean maxZBigger = minimumLocation.getBlockZ() < maxZ;

        for (int x = minimumLocation.getBlockX(); maxXBigger ? x < maxX : x > maxX; x = (maxXBigger ? x + 1 : x - 1)) {
            for (int z = minimumLocation.getBlockZ(); maxZBigger ? z < maxZ : z > maxZ; z = (maxZBigger ? z + 1 : z - 1))
                blocks.add(new Location(minimumLocation.getWorld(), x, minimumLocation.getBlockY(), z));
        }
        return blocks;
    }


    /**
     * Returns an array with the rotated x and z coordinates.
     * To get the real minimum location.
     *
     * @param rotation byte - the rotation (0 - 3)
     * @param x        int - alter x coordinate
     * @param z        int - alter z coordinate
     * @return int[] - new x, z values
     * @since 0.0.1
     */
    public static int[] getXZDimensionFromRotation(byte rotation, int x, int z) {
        return switch (rotation) {
            case 1 -> new int[]{z, -x};
            case 2 -> new int[]{-x, -z};
            case 3 -> new int[]{-z, x};
            default -> new int[]{x, z};
        };
    }

    /**
     * Subtracts the coordinate to the new minimum coordinate.
     * Uses the rotation, the size(x,y) to calculate the new minimum location based on the given coordinate.
     *
     * @param size       int[] - x,z size of the building.
     * @param rotation   byte - the rotation 0-3
     * @param coordinate Location - the old location.
     * @return Location - the new location subtracted with the rotation.
     * @since 0.0.1
     */
    public static Location getRealMinimum(int[] size, byte rotation, Location coordinate) {
        return switch (rotation) {
            case 1 -> coordinate.clone().subtract(0, 0, size[0] - 1);
            case 2 -> coordinate.clone().subtract(size[0] - 1, 0, size[1] - 1);
            case 3 -> coordinate.clone().subtract(size[1] - 1, 0, 0);
            default -> coordinate.clone();
        };
    }

    /**
     * Subtracts the coordinate to the new minimum coordinate.
     * Uses the rotation, the size(x,y) to calculate the new minimum location based on the given coordinate.
     * Returns the x,z values of the coordinate respected from the base.
     * @param baseCoordinate Location - the player base location.
     * @param size       int[] - x,z size of the building.
     * @param rotation   byte - the rotation 0-3
     * @param current Location - the old location.
     * @return int[] - the x,z coordinates from the base location.
     * @since 1.0.1
     */
    public static int[] transferInRelatives(Location baseCoordinate, int[] size, byte rotation, Location current) {
        Location coordinate = getRealMinimum(size, rotation, current);
        return new int[]{coordinate.getBlockX() - baseCoordinate.getBlockX(), coordinate.getBlockZ() - baseCoordinate.getBlockZ()};
    }

    /**
     * Returns the x,z values of the coordinate respected from the base.
     * @param baseCoordinate Location - the player base location.
     * @param coordinate Location - the minimum building location.
     * @return int[] - the x,z coordinates from the base location.
     * @since 1.0.1
     */
    public static int[] transferInRelatives(Location baseCoordinate, Location coordinate) {
        return new int[]{coordinate.getBlockX() - baseCoordinate.getBlockX(), coordinate.getBlockZ() - baseCoordinate.getBlockZ()};
    }

    /**
     * Reverses the location back to the original minimum location the building was saved.
     *
     * @param building {@link GeneralBuilding} - the building.
     * @return Location - the original location of the building.
     * @see net.fununity.clashofclans.buildings.Schematics#createBuildings(List)
     * @since 0.0.1
     */
    public static Location getReversedCoordinate(GeneralBuilding building) {
        int[] size = building.getBuilding().getSize();
        return switch (building.getRotation()) {
            case 1 -> building.getCoordinate().add(0, 0, size[0] - 1);
            case 2 -> building.getCoordinate().add(size[0] - 1, 0, size[1] - 1);
            case 3 -> building.getCoordinate().add(size[1] - 1, 0, 0);
            default -> building.getCoordinate();
        };
    }


    private static final int RDM_SPACE = 2;

    /**
     * Get a random location for a {@link RandomWorldBuilding}.
     *
     * @param playerBase     Location - the player base location.
     * @param startBuildings List<GeneralBuilding> - the buildings to check.
     * @param size           int[] - the size of the building.
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
     * Check if there is another building in the way.
     *
     * @param player            CoCPlayer - the player to check.
     * @param allBlocksOnGround List<Location> - all locations to check.
     * @return boolean - the given locations interfere with a building.
     * @since 0.0.1
     */
    public static boolean otherBuildingInWay(CoCPlayer player, List<Location> allBlocksOnGround) {
        Location minBuildable = player.getBaseStartLocation().add(ClashOfClubs.getBaseBackground(), 0, ClashOfClubs.getBaseBackground());
        Location maxBuildable = player.getBaseEndLocation();

        // list has duplicates
        if (new HashSet<>(allBlocksOnGround).size() < allBlocksOnGround.size())
            return true;

        for (Location blockLoc : allBlocksOnGround) {
            if (!LocationUtil.isBetween(minBuildable, blockLoc, maxBuildable)) {
                return true;
            }

            GeneralBuilding generalBuilding = player.getAllBuildings().stream().filter(b -> LocationUtil.isBetween(b.getCoordinate(), blockLoc,
                    b.getMaxCoordinate())).findFirst().orElse(null);
            if (generalBuilding != null) {
               if (player.getBuildingMode() == null || !player.getBuildingMode().getBuildingUUID().equals(generalBuilding.getBuildingUUID()))
                return true;
            }
        }
        return false;
    }

    /**
     * Transfers the 'facing' of a block.
     *
     * @param blockData String - block data the original block data.
     * @param rotation  byte - the rotation of the building.
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

        return switch (rotation) {
            case 1 -> switch (currentFacing.toString()) {
                case "south" -> blockData.replace("facing=" + currentFacing, "facing=east");
                case "west" -> blockData.replace("facing=" + currentFacing, "facing=south");
                case "north" -> blockData.replace("facing=" + currentFacing, "facing=west");
                case "east" -> blockData.replace("facing=" + currentFacing, "facing=north");
                default -> blockData;
            };
            case 2 -> switch (currentFacing.toString()) {
                case "south" -> blockData.replace("facing=" + currentFacing, "facing=north");
                case "west" -> blockData.replace("facing=" + currentFacing, "facing=east");
                case "north" -> blockData.replace("facing=" + currentFacing, "facing=south");
                case "east" -> blockData.replace("facing=" + currentFacing, "facing=west");
                default -> blockData;
            };
            default -> switch (currentFacing.toString()) {
                case "south" -> blockData.replace("facing=" + currentFacing, "facing=west");
                case "west" -> blockData.replace("facing=" + currentFacing, "facing=north");
                case "north" -> blockData.replace("facing=" + currentFacing, "facing=east");
                case "east" -> blockData.replace("facing=" + currentFacing, "facing=south");
                default -> blockData;
            };
        };
    }

    /**
     * Teleports any player next to the given building, if standing in there
     *
     * @param building {@link GeneralBuilding} - the building to check.
     * @since 0.0.1
     */
    public static void savePlayerFromBuilding(GeneralBuilding building) {
        for (Player visitor : Bukkit.getOnlinePlayers()) {
            if (LocationUtil.isBetween(building.getCoordinate(), visitor.getLocation(), building.getMaxCoordinate().add(0, 10, 0))) {
                Location playerLocation = visitor.getLocation().clone();
                playerLocation.setY(ClashOfClubs.getBaseYCoordinate() + 15);
                Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> visitor.teleport(playerLocation));
            }
        }
    }

    /**
     * Get the highest y coordinate of the given location without the barriers.
     *
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
