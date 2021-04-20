package net.fununity.clashofclans.buildings;

import net.fununity.clashofclans.ClashOfClans;
import net.fununity.clashofclans.buildings.classes.GeneralBuilding;
import net.fununity.clashofclans.buildings.interfaces.IBuilding;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.*;
import java.util.*;

public class Schematics {

    private Schematics() {
        throw new UnsupportedOperationException("Schematics is a utility class.");
    }

    private static final Map<String, List<String>> SCHEMATICS = new HashMap<>();

    public static boolean saveSchematic(Location[] minAndMax) {
        return saveSchematic("playerbase", minAndMax);
    }

    public static boolean saveSchematic(IBuilding building, int level, Location[] minAndMax) {
        return saveSchematic(building.name() + "-" + level, minAndMax);
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

        File file = new File(ClashOfClans.getInstance().getDataFolder().getAbsolutePath() + "/building-schematics", id + ".schematic");
        createPath();
        if (file.exists()) return false;
        try {
            if (!file.createNewFile())
                return false;
        } catch (IOException e) {
            ClashOfClans.getInstance().getLogger().warning(e.getMessage());
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
            ClashOfClans.getInstance().getLogger().warning(e.getMessage());
        }
        return false;
    }

    public static void removeBuilding(Location location, int[] size) {
        boolean allAir = false;
        int y = location.getBlockY();
        while (!allAir) {
            allAir = true;
            for (int x = location.getBlockX(); x <= location.getBlockX() + size[0]; x++) {
                for (int z = location.getBlockZ(); z <= location.getBlockZ() + size[1]; z++) {
                    Location breakLoc = new Location(location.getWorld(), x, y, z);
                    if (breakLoc.getBlock().getType() != Material.AIR)
                        allAir = false;
                    Bukkit.getScheduler().runTask(ClashOfClans.getInstance(), () -> {
                        if (location.getBlockY() + 2 > breakLoc.getBlockY())
                            breakLoc.getBlock().setType(Material.GRASS_BLOCK);
                        else {
                            breakLoc.getBlock().setType(Material.AIR);
                        }
                    });
                }
            }
            y++;
        }
    }

    public static void createBuilding(Location highestCoordinate) {
        createBuilding("playerbase", highestCoordinate);
    }

    public static void createConstruction(int[] size, Location highestCoordinate) {
        createBuilding("construction" + size[0] + "-" + size[1], highestCoordinate);
    }

    public static boolean createBuilding(GeneralBuilding building) {
        return createBuilding(building.getId(), building.getCoordinate());
    }

    public static boolean createBuilding(String id, Location coordinate) {
        if (!SCHEMATICS.containsKey(id) && !load(id))
            return false;

        List<String> list = SCHEMATICS.get(id);

        Bukkit.getScheduler().runTask(ClashOfClans.getInstance(), () -> {
            for (String str : list) {
                String[] array = str.split(";");
                int x = Integer.parseInt(array[0]);
                int y = Integer.parseInt(array[1]);
                int z = Integer.parseInt(array[2]);
                String blockData = array[3];
                try {
                    Block block = coordinate.clone().add(x, y, z).getBlock();
                    block.setBlockData(ClashOfClans.getInstance().getServer().createBlockData(blockData));
                } catch (NoSuchElementException ignored) {
                    // ignored
                }
            }
        });
        return true;
    }

    private static boolean load(String id) {
        List<String> list = getStringListFromBuilding(id);
        if (list.isEmpty()) return false;
        SCHEMATICS.put(id, list);
        return true;
    }

    private static List<String> getStringListFromBuilding(String id) {
        File file = new File(ClashOfClans.getInstance().getDataFolder().getAbsolutePath() + "/building-schematics", id + ".schematic");
        List<String> list = new ArrayList<>();
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.lines().forEach(list::add);
        } catch (IOException e) {
            ClashOfClans.getInstance().getLogger().warning(e.getMessage());
        }
        return list;
    }


    private static void createPath() {
        File file = new File(ClashOfClans.getInstance().getDataFolder().getAbsolutePath() + "/building-schematics");
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
