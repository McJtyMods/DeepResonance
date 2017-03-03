package mcjty.deepresonance.worldgen;

import net.minecraftforge.common.config.Configuration;

public class WorldGenConfiguration {

    public static final String CATEGORY_WORLDGEN = "worldgen";

    public static boolean retrogen = true;
    public static boolean verboseSpawn = false;

    public static int minVeinSize = 2;
    public static int maxVeinSize = 4;
    public static int chancesToSpawn = 5;
    public static int minY = 2;
    public static int maxY = 30;

    public static int crystalSpawnChance = 15;
    public static int crystalSpawnTries = 10;

    public static boolean generateOverworldOre = true;
    public static boolean generateNetherOre = true;
    public static boolean generateEndOre = false;
    public static boolean generateOreOtherDimensions = true;

    public static boolean generateOverworldCrystals = true;
    public static boolean generateNetherCrystals = true;
    public static boolean generateCrystalsOtherDimensions = true;

    public static void init(Configuration cfg) {
        retrogen = cfg.get(CATEGORY_WORLDGEN, "retrogen", retrogen, "Enable this if you want to get retrogen (generation of ores/crystals) for already existing chunks").getBoolean();
        verboseSpawn = cfg.get(CATEGORY_WORLDGEN, "verboseSpawn", verboseSpawn, "Enable this if you want to see in the log where crystals are spawned").getBoolean();

        generateOverworldOre = cfg.get(CATEGORY_WORLDGEN, "generateOverworldOre", generateOverworldOre, "Enable this if you want resonating ore in the overworld").getBoolean();
        generateNetherOre = cfg.get(CATEGORY_WORLDGEN, "generateNetherOre", generateNetherOre, "Enable this if you want resonating ore in the nether").getBoolean();
        generateEndOre = cfg.get(CATEGORY_WORLDGEN, "generateEndOre", generateEndOre, "Enable this if you want resonating ore in the end").getBoolean();
        generateOreOtherDimensions = cfg.get(CATEGORY_WORLDGEN, "generateOreOtherDimensions", generateOreOtherDimensions, "Enable this if you want resonating ore in other dimensions (if they have stone)").getBoolean();

        generateOverworldCrystals = cfg.get(CATEGORY_WORLDGEN, "generateOverworldCrystals", generateOverworldCrystals, "Enable this if you want resonating crystals in the overworld").getBoolean();
        generateNetherCrystals = cfg.get(CATEGORY_WORLDGEN, "generateNetherCrystals", generateNetherCrystals, "Enable this if you want resonating crystals in the nether").getBoolean();
        generateCrystalsOtherDimensions = cfg.get(CATEGORY_WORLDGEN, "generateCrystalsOtherDimensions", generateCrystalsOtherDimensions, "Enable this if you want resonating crystals in other dimensions (if they have stone caves)").getBoolean();

        minVeinSize = cfg.get(CATEGORY_WORLDGEN, "minOreVeinSize", minVeinSize, "Minimum size of the ore veines").getInt();
        maxVeinSize = cfg.get(CATEGORY_WORLDGEN, "maxOreVeinSize", maxVeinSize, "Maximum size of the ore veines").getInt();
        chancesToSpawn = cfg.get(CATEGORY_WORLDGEN, "chancesToSpawn", chancesToSpawn, "Chances for the ore to spawn in a chunk").getInt();
        minY = cfg.get(CATEGORY_WORLDGEN, "minY", minY, "Minimum ore height").getInt();
        maxY = cfg.get(CATEGORY_WORLDGEN, "maxY", maxY, "Maximum ore height").getInt();

        crystalSpawnChance = cfg.get(CATEGORY_WORLDGEN, "crystalSpawnChance", crystalSpawnChance, "The chance that a crystal will spawn in a chunk. Higher number means less chance. 0 means no crystal will ever spawn.").getInt();
        crystalSpawnTries = cfg.get(CATEGORY_WORLDGEN, "crystalSpawnTries", crystalSpawnTries, "The number of times that the worldgen will try to spawn a crystal in a chunk before it fails.").getInt();
    }

}
