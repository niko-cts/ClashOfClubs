package net.fununity.clashofclans.buildings;

import net.fununity.main.api.common.util.RandomUtil;
import org.bukkit.Material;

import java.util.Map;
import java.util.TreeMap;

public enum GroundMaterials {

    GRASS_BLOCK(Material.GRASS_BLOCK, 75),
    GRASS_PATH(Material.GRASS_PATH, 15),
    COARSE_DIRT(Material.COARSE_DIRT, 10),
    GRAVEL(Material.GRAVEL, 5);

    private final Material material;
    private final int percentage;

    GroundMaterials(Material material, int percentage) {
        this.material = material;
        this.percentage = percentage;
    }

    public static Material getRandomMaterial() {
        TreeMap<Integer, Material> ceiling = new TreeMap<>();
        for (GroundMaterials groundMats : values()) {
            ceiling.put(groundMats.percentage, groundMats.material);
        }

        int i = 0;

        do {
            int random = RandomUtil.getRandomInt(100) + 1;
            Map.Entry<Integer, Material> entry = ceiling.ceilingEntry(random);
            if (entry != null)
                return entry.getValue();
            i++;
        } while (i < 20);
        return Material.GRASS_BLOCK;
    }

}
