package mcjty.deepresonance.worldgen;

import net.minecraftforge.common.config.Configuration;

public class WorldGenConfiguration {

    public static final String CATEGORY_WORLDGEN = "worldgen";

    public static int minVeinSize = 5;
    public static int maxVeinSize = 8;
    public static int chancesToSpawn = 3;
    public static int minY = 2;
    public static int maxY = 30;

    public static void init(Configuration cfg) {
        minVeinSize = cfg.get(CATEGORY_WORLDGEN, "minVeinSize", minVeinSize, "Minimum size of the veines").getInt();
        maxVeinSize = cfg.get(CATEGORY_WORLDGEN, "maxVeinSize", maxVeinSize, "Maximum size of the veines").getInt();
        chancesToSpawn = cfg.get(CATEGORY_WORLDGEN, "chancesToSpawn", chancesToSpawn, "Chances to spawn in a chunk").getInt();
        minY = cfg.get(CATEGORY_WORLDGEN, "minY", minY, "Minimum height").getInt();
        maxY = cfg.get(CATEGORY_WORLDGEN, "maxY", maxY, "Maximum height").getInt();
    }

}
