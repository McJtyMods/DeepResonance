package mcjty.deepresonance.worldgen;

import net.minecraftforge.common.config.Configuration;

public class WorldGenConfiguration {

    public static final String CATEGORY_WORLDGEN = "worldgen";

    public static boolean retrogen = false;
    public static boolean verboseSpawn = false;

    public static int minVeinSize = 5;
    public static int maxVeinSize = 8;
    public static int chancesToSpawn = 3;
    public static int minY = 2;
    public static int maxY = 30;

    public static int crystalSpawnChance = 100;
    public static int crystalSpawnTries = 10;

    public static void init(Configuration cfg) {
        retrogen = cfg.get(CATEGORY_WORLDGEN, "retrogen", retrogen, "Enable this if you want to get retrogen (generation of ores/crystals) for already existing chunks").getBoolean();
        verboseSpawn = cfg.get(CATEGORY_WORLDGEN, "verboseSpawn", verboseSpawn, "Enable this if you want to see in the log where crystals are spawned").getBoolean();

        minVeinSize = cfg.get(CATEGORY_WORLDGEN, "minVeinSize", minVeinSize, "Minimum size of the ore veines").getInt();
        maxVeinSize = cfg.get(CATEGORY_WORLDGEN, "maxVeinSize", maxVeinSize, "Maximum size of the ore veines").getInt();
        chancesToSpawn = cfg.get(CATEGORY_WORLDGEN, "chancesToSpawn", chancesToSpawn, "Chances for the ore to spawn in a chunk").getInt();
        minY = cfg.get(CATEGORY_WORLDGEN, "minY", minY, "Minimum ore height").getInt();
        maxY = cfg.get(CATEGORY_WORLDGEN, "maxY", maxY, "Maximum ore height").getInt();

        crystalSpawnChance = cfg.get(CATEGORY_WORLDGEN, "crystalSpawnChance", crystalSpawnChance, "The chance that a crystal will spawn in a chunk. Higher number means less chance. 0 means no crystal will ever spawn.").getInt();
        crystalSpawnTries = cfg.get(CATEGORY_WORLDGEN, "crystalSpawnTries", crystalSpawnTries, "The number of times that the worldgen will try to spawn a crystal in a chunk before it fails.").getInt();
    }

}
