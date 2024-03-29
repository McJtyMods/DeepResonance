package mcjty.deepresonance.modules.worldgen.util;

import com.google.common.collect.Lists;
import mcjty.deepresonance.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class WorldGenConfiguration {

    public static ForgeConfigSpec.BooleanValue RETROGEN;
    public static ForgeConfigSpec.BooleanValue VERBOSE;

    public static ForgeConfigSpec.IntValue OVERWORLD_VEIN_SIZE;
    public static ForgeConfigSpec.IntValue OVERWORLD_SPAWN_CHANCES;
    public static ForgeConfigSpec.IntValue OVERWORLD_MINY;
    public static ForgeConfigSpec.IntValue OVERWORLD_MAXY;

    public static ForgeConfigSpec.IntValue NETHER_VEIN_SIZE;
    public static ForgeConfigSpec.IntValue NETHER_SPAWN_CHANCES;
    public static ForgeConfigSpec.IntValue NETHER_MINY;
    public static ForgeConfigSpec.IntValue NETHER_MAXY;

    public static ForgeConfigSpec.IntValue END_VEIN_SIZE;
    public static ForgeConfigSpec.IntValue END_SPAWN_CHANCES;
    public static ForgeConfigSpec.IntValue END_MINY;
    public static ForgeConfigSpec.IntValue END_MAXY;

    public static ForgeConfigSpec.BooleanValue NETHER_ORE;
    public static ForgeConfigSpec.BooleanValue END_ORE;
    public static ForgeConfigSpec.BooleanValue OTHER_ORE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ORE_BLACKLIST;

    public static ForgeConfigSpec.DoubleValue CRYSTAL_SPAWN_CHANCE;
    public static ForgeConfigSpec.IntValue CRYSTAL_SPAWN_TRIES;
    public static ForgeConfigSpec.BooleanValue NETHER_CRYSTALS;
    public static ForgeConfigSpec.BooleanValue OTHER_CRYSTALS;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CRYSTAL_BLACKLIST;

    public static void init() {

        RETROGEN = Config.COMMON_BUILDER.comment("Enable this if you want to get retrogen (generation of ores/crystals) for already existing chunks")
                .define("retrogen", true);
        VERBOSE = Config.COMMON_BUILDER.comment("Enable this if you want to see in the log where crystals are spawned")
                .define("verboseSpawn", false);

        NETHER_ORE = Config.COMMON_BUILDER.comment("Enable this if you want resonating ore in nether biomes")
                .define("generateOreNether", true);
        END_ORE = Config.COMMON_BUILDER.comment("Enable this if you want resonating ore in end biomes")
                .define("generateOreEnd", false);
        OTHER_ORE = Config.COMMON_BUILDER.comment("Enable this if you want resonating ore in other biomes (if they have stone)")
                .define("generateOreOther", true);
        ORE_BLACKLIST = Config.COMMON_BUILDER.comment("Biome blacklist, resonant ore will not spawn in biomes listed here")
                .defineList("otherBiomeBlacklist", Lists.newArrayList("minecraft:the_void"), s -> s instanceof String);

        Config.COMMON_BUILDER.push("overworld");
        OVERWORLD_VEIN_SIZE = Config.COMMON_BUILDER.comment("Size of resonant ore veins")
                .defineInRange("minOreVeinSize", 3, 1, 8);
        OVERWORLD_SPAWN_CHANCES = Config.COMMON_BUILDER.comment("Chances for the ore to spawn in a chunk")
                .defineInRange("chancesToSpawn", 5, 1, 16);
        OVERWORLD_MINY = Config.COMMON_BUILDER.comment("Minimum y-level where resonant ore will spawn")
                .defineInRange("minY", 2, 1, 256);
        OVERWORLD_MAXY = Config.COMMON_BUILDER.comment("Maximum y-level where resonant ore will spawn")
                .defineInRange("maxY", 32, 1, 256);
        Config.COMMON_BUILDER.pop();

        Config.COMMON_BUILDER.push("nether");
        NETHER_VEIN_SIZE = Config.COMMON_BUILDER.comment("Size of resonant ore veins")
                .defineInRange("minOreVeinSize", 3, 1, 8);
        NETHER_SPAWN_CHANCES = Config.COMMON_BUILDER.comment("Chances for the ore to spawn in a chunk")
                .defineInRange("chancesToSpawn", 5, 1, 16);
        NETHER_MINY = Config.COMMON_BUILDER.comment("Minimum y-level where resonant ore will spawn")
                .defineInRange("minY", 2, 1, 256);
        NETHER_MAXY = Config.COMMON_BUILDER.comment("Maximum y-level where resonant ore will spawn")
                .defineInRange("maxY", 32, 1, 256);
        Config.COMMON_BUILDER.pop();

        Config.COMMON_BUILDER.push("end");
        END_VEIN_SIZE = Config.COMMON_BUILDER.comment("Size of resonant ore veins")
                .defineInRange("minOreVeinSize", 3, 1, 8);
        END_SPAWN_CHANCES = Config.COMMON_BUILDER.comment("Chances for the ore to spawn in a chunk")
                .defineInRange("chancesToSpawn", 5, 1, 16);
        END_MINY = Config.COMMON_BUILDER.comment("Minimum y-level where resonant ore will spawn")
                .defineInRange("minY", 2, 1, 256);
        END_MAXY = Config.COMMON_BUILDER.comment("Maximum y-level where resonant ore will spawn")
                .defineInRange("maxY", 32, 1, 256);
        Config.COMMON_BUILDER.pop();

        NETHER_CRYSTALS = Config.COMMON_BUILDER.comment("Enable this if you want resonating crystals in nether biomes")
                .define("generateCrystalsNether", true);
        OTHER_CRYSTALS = Config.COMMON_BUILDER.comment("Enable this if you want resonating crystals in other dimensions biomes (if they have stone caves)")
                .define("generateCrystalsOther", true);
        CRYSTAL_BLACKLIST = Config.COMMON_BUILDER.comment("Biome blacklist, resonant crystals will not spawn in biomes listed here")
                .defineList("otherBiomeBlacklist", Lists.newArrayList("minecraft:the_void"), s -> s instanceof String);

        CRYSTAL_SPAWN_CHANCE = Config.COMMON_BUILDER.comment("The chance that a crystal will spawn in a chunk. (0 = never, 1 = every chunk")
                .defineInRange("crystalSpawnChance", 0.3, 0, 1);
        CRYSTAL_SPAWN_TRIES = Config.COMMON_BUILDER.comment("The number of times that the worldgen will try to spawn a crystal in a chunk before it fails.")
                .defineInRange("crystalSpawnTries", 20, 1, 32);
    }

}
