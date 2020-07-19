package mcjty.deepresonance.modules.worldgen.util;

import com.google.common.collect.Lists;
import elec332.core.api.config.IConfigurableElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 9-7-2020
 */
public class WorldGenConfiguration implements IConfigurableElement {

    public static ForgeConfigSpec.BooleanValue RETROGEN;
    public static ForgeConfigSpec.BooleanValue VERBOSE;

    public static ForgeConfigSpec.IntValue VEIN_SIZE;
    public static ForgeConfigSpec.IntValue SPAWN_CHANCES;
    public static ForgeConfigSpec.IntValue MIN_Y;
    public static ForgeConfigSpec.IntValue MAX_Y;

    public static ForgeConfigSpec.BooleanValue NETHER_ORE;
    public static ForgeConfigSpec.BooleanValue END_ORE;
    public static ForgeConfigSpec.BooleanValue OTHER_ORE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ORE_BLACKLIST;

    public static ForgeConfigSpec.DoubleValue SPAWN_CHANCE_CRYSTALS;
    public static ForgeConfigSpec.IntValue SPAWN_TRIES_CRYSTAL;
    public static ForgeConfigSpec.BooleanValue NETHER_CRYSTALS;
    public static ForgeConfigSpec.BooleanValue OTHER_CRYSTALS;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CRYSTAL_BLACKLIST;

    @Override
    public void registerProperties(@Nonnull ForgeConfigSpec.Builder config, ModConfig.Type type) {

        RETROGEN = config.comment("Enable this if you want to get retrogen (generation of ores/crystals) for already existing chunks")
                .define("retrogen", true);
        VERBOSE = config.comment("Enable this if you want to see in the log where crystals are spawned")
                .define("verboseSpawn", false);

        NETHER_ORE = config.comment("Enable this if you want resonating ore in nether biomes")
                .define("generateOreNether", true);
        END_ORE = config.comment("Enable this if you want resonating ore in end biomes")
                .define("generateOreEnd", false);
        OTHER_ORE = config.comment("Enable this if you want resonating ore in other biomes (if they have stone)")
                .define("generateOreOther", true);
        ORE_BLACKLIST = config.comment("Biome blacklist, resonant ore will not spawn in biomes listed here")
                .defineList("otherBiomeBlacklist", Lists.newArrayList("minecraft:the_void"), s -> s instanceof String);

        VEIN_SIZE = config.comment("Size of resonant ore veins")
                .defineInRange("minOreVeinSize", 3, 1, 8);
        SPAWN_CHANCES = config.comment("Chances for the ore to spawn in a chunk")
                .defineInRange("chancesToSpawn", 5, 1, 16);
        MIN_Y = config.comment("Minimum y-level where resonant ore will spawn")
                .defineInRange("minY", 2, 1, 256);
        MAX_Y = config.comment("Maximum y-level where resonant ore will spawn")
                .defineInRange("maxY", 32, 1, 256);

        NETHER_CRYSTALS = config.comment("Enable this if you want resonating crystals in nether biomes")
                .define("generateCrystalsNether", true);
        OTHER_CRYSTALS = config.comment("Enable this if you want resonating crystals in other dimensions biomes (if they have stone caves)")
                .define("generateCrystalsOther", true);
        CRYSTAL_BLACKLIST = config.comment("Biome blacklist, resonant crystals will not spawn in biomes listed here")
                .defineList("otherBiomeBlacklist", Lists.newArrayList("minecraft:the_void"), s -> s instanceof String);

        SPAWN_CHANCE_CRYSTALS = config.comment("The chance that a crystal will spawn in a chunk. (0 = never, 1 = every chunk")
                .defineInRange("crystalSpawnChance", 0.07, 0, 1);
        SPAWN_TRIES_CRYSTAL = config.comment("The number of times that the worldgen will try to spawn a crystal in a chunk before it fails.")
                .defineInRange("crystalSpawnTries", 1, 10, 32);

    }

}
