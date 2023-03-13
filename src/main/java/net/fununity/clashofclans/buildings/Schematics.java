package net.fununity.clashofclans.buildings;

import com.google.common.collect.Lists;
import net.fununity.clashofclans.ClashOfClubs;
import net.fununity.clashofclans.buildings.instances.ConstructionBuilding;
import net.fununity.clashofclans.buildings.instances.GeneralBuilding;
import net.fununity.clashofclans.util.BuildingLocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * This class is used for loading building schematics from the file folder.
 * @author Niko
 * @since 0.0.1
 */
public class Schematics {

    private Schematics() {
        throw new UnsupportedOperationException("Schematics is a utility class.");
    }

    private static final int PARTITION_SIZE = 3000;
    private static final long TICK_PER_PARTITION = 10L;
    private static final Map<String, List<String>> SCHEMATICS = new HashMap<>();

    public static void removeBuilding(Location location, int[] size, byte rotation) {
        List<Location> areaBlocks = BuildingLocationUtil.getAllLocationsOnGround(location,
                new int[]{size[rotation == 1 || rotation == 3 ? 1 : 0], size[rotation == 1 || rotation == 3 ? 0 : 1]});

        Map<Location, Material> blockSettingMap = new HashMap<>();
        for (Location blockLoc : areaBlocks) {
            Location loc = blockLoc.clone();
            for (int y = BuildingLocationUtil.getHighestYCoordinate(blockLoc); y >= ClashOfClubs.getBaseYCoordinate(); y--) {
                loc.setY(y);

                Material material = Material.AIR;
                if (y == ClashOfClubs.getBaseYCoordinate() + 1)
                    material = GroundMaterials.getRandomMaterial();
                else if (y == ClashOfClubs.getBaseYCoordinate())
                    material = Material.STONE;

                if (material != loc.getBlock().getType())
                    blockSettingMap.put(loc.clone(), material);
            }
        }

        Bukkit.getScheduler().runTask(ClashOfClubs.getInstance(), () -> blockSettingMap.forEach((key, value) -> key.getBlock().setType(value)));
    }

    /**
     * Creates all given buildings + the player base in optional partitions.
     * @param coordinate Location - the player base location.
     * @param buildings List<GeneralBuilding> - all buildings to create.
     * @return long - the long in ticks it takes.
     * @since 0.0.2
     */
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
                int[] coords = BuildingLocationUtil.getXZDimensionFromRotation(building.getRotation(), Integer.parseInt(array[0]), Integer.parseInt(array[2]));
                blockSettingMap.put(buildingLocation.clone().add(coords[0], Integer.parseInt(array[1]), coords[1]), new Object[]{array[3], building.getRotation()});
            }
        }

        return placeBlocks(blockSettingMap);
    }

    /**
     * Creates all given constructions in optional partitions.
     *
     * @param constructionBuildings List<ConstructionsBuilding> - all buildings to create.
     * @since 0.0.2
     */
    public static void createConstruction(List<ConstructionBuilding> constructionBuildings) {
        Map<Location, Object[]> buildingMap = new HashMap<>();
        for (ConstructionBuilding building : constructionBuildings) {
            if (building.getBuildingFinishTime() + 500 > System.currentTimeMillis())
                buildingMap.putAll(createBuilding("construction" + building.getBuilding().getSize()[0] + "-" + building.getBuilding().getSize()[1], BuildingLocationUtil.getReversedCoordinate(building), building.getRotation()));
        }
        placeBlocks(buildingMap);
    }

    /**
     * Creates all given buildings in optional partitions.
     * @param generalBuildings List<GeneralBuilding> - all buildings to create.
     * @return long - the long in ticks it takes.
     * @since 0.0.2
     */
    public static long createBuildings(List<GeneralBuilding> generalBuildings) {
        Map<Location, Object[]> buildingMap = new HashMap<>();
        for (GeneralBuilding building : generalBuildings) {
            buildingMap.putAll(createBuilding(building.getId(), BuildingLocationUtil.getReversedCoordinate(building), building.getRotation()));
        }
        return placeBlocks(buildingMap);
    }

    public static void createBuilding(GeneralBuilding building) {
        placeBlocks(createBuilding(building.getId(), BuildingLocationUtil.getReversedCoordinate(building), building.getRotation()));
    }

    /**
     * Returns a map with the location and material data.
     * @param id String - the id of the building to create.
     * @param coordinate Location - minimum coordinate.
     * @param rotation byte - rotation of the building.
     * @return Map<Location, Object[]> - all locations and material and rotation data.
     */
    private static Map<Location, Object[]> createBuilding(String id, Location coordinate, byte rotation) {
        if (!SCHEMATICS.containsKey(id)) {
            ClashOfClubs.getInstance().getLogger().log(Level.WARNING, "Schematic {0} was not found.", id);
            return new HashMap<>();
        }

        Map<Location, Object[]> blockSettingMap = new HashMap<>();
        for (String str : SCHEMATICS.get(id)) {
            String[] array = str.split(";");
            int[] coords = BuildingLocationUtil.getXZDimensionFromRotation(rotation, Integer.parseInt(array[0]), Integer.parseInt(array[2]));
            blockSettingMap.put(coordinate.clone().add(coords[0], Integer.parseInt(array[1]), coords[1]), new Object[]{array[3], rotation});
        }

        return blockSettingMap;
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

    private static void load(String id) {
        File file = new File(ClashOfClubs.getInstance().getDataFolder().getAbsolutePath() + "/building-schematics", id + ".schematic");
        if (!file.exists())
            return;

        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.lines().forEach(list::add);
        } catch (IOException e) {
            ClashOfClubs.getInstance().getLogger().warning(e.getMessage());
        }

        if (!list.isEmpty())
            SCHEMATICS.put(id, list);
    }
}
