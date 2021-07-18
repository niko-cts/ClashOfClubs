package net.fununity.clashofclans.buildings;

import net.fununity.main.api.common.util.RandomUtil;
import org.bukkit.Material;

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
        int random = RandomUtil.getRandomInt(100) + 1;
        int current = 0;
        for (GroundMaterials materials : values()) {
            if (materials.percentage + current < random)
                return materials.material;
            current += materials.percentage;
        }
        return Material.GRASS_BLOCK;
    }

}
