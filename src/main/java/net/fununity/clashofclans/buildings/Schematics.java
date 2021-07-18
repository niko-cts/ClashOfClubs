package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.classes.ConstructionBuilding;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import net.fununity.main.api.common.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class Schematics {

    private Schematics() {
        throw new UnsupportedOperationException("Schematics is a utility class.");
    }

    private static final Map<String, List<String>> SCHEMATICS = new HashMap<>();

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

    private static final List<Material> RANDOM_FLOOR = Arrays.asList(Material.GRASS_BLOCK, Material.GRASS_PATH);

    public static void removeBuilding(Location location, int[] size, byte rotation) {
        List<Block> areaBlocks = BuildingLocationUtil.getBlocksInBuildingGround(location,
                new int[]{size[rotation == 1 || rotation == 3 ? 1 : 0], size[rotation == 1 || rotation == 3 ? 0 : 1]});

        for (Block block : areaBlocks) {
            for (int y = BuildingLocationUtil.getHighestYCoordinate(block.getLocation()); y >= ClashOfClubs.getBaseYCoordinate(); y--) {
                Location breakLoc = block.getLocation().clone();
                breakLoc.setY(y);

                if (breakLoc.getBlock().getType() != Material.AIR) {
                    Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> {
                        if (ClashOfClubs.getBaseYCoordinate() + 1 >= breakLoc.getBlockY())
                            breakLoc.getBlock().setType(RANDOM_FLOOR.get(RandomUtil.getRandomInt(RANDOM_FLOOR.size())));
                        else
                            breakLoc.getBlock().setType(Material.AIR);
                    });
                }
            }
        }
    }

    public static void createPlayerBase(Location location) {
        createBuilding("playerbase", location, (byte) 0);
    }

    public static void createBuilding(GeneralBuilding building) {
        if (building instanceof ConstructionBuilding)
            createBuilding("construction" + building.getBuilding().getSize()[0] + "-" + building.getBuilding().getSize()[1], BuildingLocationUtil.getReversedCoordinate(building), building.getRotation());
        else
            createBuilding(building.getId(), BuildingLocationUtil.getReversedCoordinate(building), building.getRotation());
    }

    private static void createBuilding(String id, Location coordinate, byte rotation) {
        if (!SCHEMATICS.containsKey(id) && !load(id))
            return;

        for (String str : SCHEMATICS.get(id)) {
            String[] array = str.split(";");
            int[] coords = BuildingLocationUtil.getCoordinateFromRotation(rotation, Integer.parseInt(array[0]), Integer.parseInt(array[2]));
            int x = coords[0];
            int y = Integer.parseInt(array[1]);
            int z = coords[1];

            Material material = Material.valueOf(array[3].split("\\[")[0].replace("minecraft:", "").toUpperCase());
            BlockData blockData;

            if ((array[3].contains("wall") && !array[3].contains("face=wall") && !array[3].contains("wall_sign")) || array[3].contains("fence"))
                blockData = material.createBlockData();
            else {
                blockData = ClashOfClubs.getInstance().getServer().createBlockData(BuildingLocationUtil.getBlockDataFromRotation(array[3], rotation));
            }

            Block blockToChange = coordinate.clone().add(x, y, z).getBlock();
            if (!blockData.equals(blockToChange.getBlockData())) {
                Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> {
                    blockToChange.setType(material);
                    blockToChange.setBlockData(blockData);
                    blockToChange.getState().update();
                });
            }
        }
    }

    /**
     * Caches all schematics that can be find in the building-schematics folder in the data folder of the plugin.
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
                    ClashOfClubs.getInstance().getLogger().log(Level.INFO, "Cached {0} schematics.", list.length);
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
