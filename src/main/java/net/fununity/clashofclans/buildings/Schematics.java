package net.fununity.clashofclans.buildings;

import com.google.common.collect.Lists;
import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.ConstructionBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Schematics {

    private Schematics() {
        throw new UnsupportedOperationException("Schematics is a utility class.");
    }

    private static final int PARTITION_SIZE = 5000;
    private static final long TICK_PER_PARTITION = 5L;
    private static final Map<String, List<String>> SCHEMATICS = new HashMap<>();

    public static void removeBuilding(Location location, int[] size, byte rotation) {
        List<Location> areaBlocks = BuildingLocationUtil.getAllLocationsOnGround(location,
                new int[]{size[rotation == 1 || rotation == 3 ? 1 : 0], size[rotation == 1 || rotation == 3 ? 0 : 1]});

        Map<Location, Material> blockSettingMap = new HashMap<>();
        for (Location blockLoc : areaBlocks) {
            Location loc = blockLoc.clone();
            for (int y = BuildingLocationUtil.getHighestYCoordinate(blockLoc); y >= ClashOfClubs.getBaseYCoordinate(); y--) {
                loc.setY(y);

                Material material = y <= ClashOfClubs.getBaseYCoordinate() + 1 ? GroundMaterials.getRandomMaterial() : Material.AIR;
                if (material != loc.getBlock().getType())
                    blockSettingMap.put(loc.clone(), material);
            }
        }

        Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> blockSettingMap.forEach((key, value) -> key.getBlock().setType(value)));
    }

    public static long createPlayerBase(Location coordinate, List<GeneralBuilding> buildings) {
        Map<Location, Object[]> blockSettingMap = new HashMap<>();

        for (String str : SCHEMATICS.get("playerbase")) {
            String[] array = str.split(";");
            blockSettingMap.put(coordinate.clone().add(Integer.parseInt(array[0]), Integer.parseInt(array[1]), Integer.parseInt(array[2])), new Object[]{array[3], (byte) 0});
        }

        for (GeneralBuilding building : buildings) {
            Location buildingLocation = BuildingLocationUtil.getReversedCoordinate(building);
            for (String str : SCHEMATICS.get(building.getId())) {
                String[] array = str.split(";");
                int[] coords = BuildingLocationUtil.getCoordinateFromRotation(building.getRotation(), Integer.parseInt(array[0]), Integer.parseInt(array[2]));
                blockSettingMap.put(buildingLocation.clone().add(coords[0], Integer.parseInt(array[1]), coords[1]), new Object[]{array[3], building.getRotation()});
            }
        }

        return placeBlocks(blockSettingMap);
    }

    public static void createBuilding(GeneralBuilding building) {
        if (building instanceof ConstructionBuilding)
            createBuilding("construction" + building.getBuilding().getSize()[0] + "-" + building.getBuilding().getSize()[1], BuildingLocationUtil.getReversedCoordinate(building), building.getRotation());
        else
            createBuilding(building.getId(), BuildingLocationUtil.getReversedCoordinate(building), building.getRotation());
    }

    private static void createBuilding(String id, Location coordinate, byte rotation) {
        if (!SCHEMATICS.containsKey(id))
            return;

        Map<Location, Object[]> blockSettingMap = new HashMap<>();
        for (String str : SCHEMATICS.get(id)) {
            String[] array = str.split(";");
            int[] coords = BuildingLocationUtil.getCoordinateFromRotation(rotation, Integer.parseInt(array[0]), Integer.parseInt(array[2]));
            blockSettingMap.put(coordinate.clone().add(coords[0], Integer.parseInt(array[1]), coords[1]), new Object[]{array[3], rotation});
        }

        placeBlocks(blockSettingMap);
    }

    /**
     * Place blocks with delays.
     * @param blockSettingMap Map<Location, BlockData> - the map of all blocks to set.
     * @return long - the ticks it will take.
     */
    private static long placeBlocks(Map<Location, Object[]> blockSettingMap) {
        if (blockSettingMap.size() > PARTITION_SIZE) {
            List<List<Location>> partition = Lists.partition(blockSettingMap.keySet().stream().sorted(Comparator.comparingInt(Location::getBlockY)).collect(Collectors.toList()), PARTITION_SIZE);
            ClashOfClubs.getInstance().getLogger().log(Level.INFO, "Will finish construction of {0} items in {1} partitions in {2}s", new String[]{blockSettingMap.size() + "", partition.size() + "", (TICK_PER_PARTITION / 20.0 * partition.size()) + ""});
            for (int i = 0; i < partition.size(); i++) {
                int finalI = i;
                Bukkit.getScheduler().runTaskLater(ClashOfClubs.getInstance(), () -> {
                    for (Location location : partition.get(finalI)) {
                        Object[] data = blockSettingMap.get(location);
                        BlockData blockData = getBlockData((String) data[0], (byte) data[1]);
                        Block block = location.getBlock();
                        if (!blockData.matches(block.getBlockData()))
                            block.setBlockData(blockData, false);
                    }
                }, i * TICK_PER_PARTITION);
            }
            return (partition.size() - 1) * TICK_PER_PARTITION;
        }

        Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> {
            for (Map.Entry<Location, Object[]> entry : blockSettingMap.entrySet()) {
                Object[] data = entry.getValue();
                BlockData blockData = getBlockData((String) data[0], (byte) data[1]);
                Block block = entry.getKey().getBlock();
                if (!blockData.matches(block.getBlockData()))
                    block.setBlockData(blockData, false);
            }
        });
        return 0L;
    }

    /**
     * Creates a blockdata
     * @param data String - the block data
     * @param rotation byte - the rotation to get the blockdata
     */
    private static BlockData getBlockData(String data, byte rotation) {
        Material material = Material.valueOf(data.split("\\[")[0].replace("minecraft:", "").toUpperCase());
        BlockData blockData;
        if ((data.contains("wall") && !data.contains("face=wall") && !data.contains("wall_sign")) || material == Material.AIR || material == Material.DIRT || data.contains("fence"))
            blockData = material.createBlockData();
        else
            blockData = ClashOfClubs.getInstance().getServer().createBlockData(BuildingLocationUtil.getBlockDataFromRotation(data, rotation));
        return blockData;
    }

    // FOR SCHEMATIC SAVING

    public static boolean saveSchematic(Location[] minAndMax) {
        return saveSchematic("playerbase", minAndMax);
    }

    public static void saveSchematic(IBuilding building, int level, Location[] minAndMax) {
        saveSchematic(building.name() + "-" + level, minAndMax);
    }

    public static void saveSchematic(IBuilding building, int level, Location[] minAndMax, String version) {
        saveSchematic(building.name() + "-" + level + "-" + version, minAndMax);
    }

    public static boolean saveSchematic(String id, Location[] minAndMax) {
        Location min = minAndMax[0];
        Location max = minAndMax[1];

        List<Block> blockList = new ArrayList<>();
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    blockList.add(new Location(min.getWorld(), x, y, z).getBlock());
                }
            }
        }

        File file = new File(ClashOfClubs.getInstance().getDataFolder().getAbsolutePath() + "/building-schematics", id + ".schematic");
        createPath();
        if (file.exists())
            file.delete();

        try {
            if (!file.createNewFile())
                return false;
        } catch (IOException e) {
            ClashOfClubs.getInstance().getLogger().warning(e.getMessage());
        }

        String newLine = System.getProperty("line.separator");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String s : fromBlocklistToStringList(blockList, min)) {
                bw.write(s);
                bw.write(newLine);
            }
            bw.close();
            return true;
        } catch (IOException e) {
            ClashOfClubs.getInstance().getLogger().warning(e.getMessage());
        }
        return false;
    }



    /**
     * Caches all schematics that can be found in the building-schematics folder in the data folder of the plugin.
     * @since 0.0.1
     */
    public static void cacheAllSchematics() {
        Bukkit.getScheduler().runTaskAsynchronously(ClashOfClubs.getInstance(), () -> {
            File schematics = new File(ClashOfClubs.getInstance().getDataFolder() + "/building-schematics/");
            if (schematics.exists()) {
                String[] list = schematics.list();
                if (list != null) {
                    for (String s : list)
                        load(s.replace(".schematic", ""));
                    ClashOfClubs.getInstance().getLogger().log(Level.INFO, "Cached {0} schematics.", SCHEMATICS.size());
                }
            }
        });
    }

    private static boolean load(String id) {
        List<String> list = getStringListFromBuilding(id);
        if (list.isEmpty()) return false;
        SCHEMATICS.put(id, list);
        return true;
    }

    private static List<String> getStringListFromBuilding(String id) {
        File file = new File(ClashOfClubs.getInstance().getDataFolder().getAbsolutePath() + "/building-schematics", id + ".schematic");
        List<String> list = new ArrayList<>();
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.lines().forEach(list::add);
        } catch (IOException e) {
            ClashOfClubs.getInstance().getLogger().warning(e.getMessage());
        }
        return list;
    }


    private static void createPath() {
        File file = new File(ClashOfClubs.getInstance().getDataFolder().getAbsolutePath() + "/building-schematics");
        if (!file.exists())
            file.mkdirs();
    }

    /**
     * Converts a list of blocks into a list of strings.
     *
     * @param blocks List<Blocks> - List of blocks
     * @param start  Location - To start from
     * @return List<String> - Converted List
     * @since 0.0.1
     */
    private static List<String> fromBlocklistToStringList(List<Block> blocks, Location start) {
        List<String> list = new ArrayList<>();
        blocks.forEach(b -> list.add(getStringFromBlock(b, start)));
        return list;
    }

    private static String getStringFromBlock(Block b, Location start) {
        int x = b.getX() - start.getBlockX();
        int y = b.getY() - start.getBlockY();
        int z = b.getZ() - start.getBlockZ();
        String data = b.getBlockData().getAsString();

        return x + ";" + y + ";" + z + ";" + data;
    }

}
