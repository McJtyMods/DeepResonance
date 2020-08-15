package mcjty.deepresonance.modules.worldgen.util;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 9-7-2020
 */
public class WorldGenConfiguration {

    public final ForgeConfigSpec.BooleanValue retroGen;
    public final ForgeConfigSpec.BooleanValue verbose;

    public final ForgeConfigSpec.IntValue veinSize;
    public final ForgeConfigSpec.IntValue spawnChances;
    public final ForgeConfigSpec.IntValue minY;
    public final ForgeConfigSpec.IntValue maxY;

    public final ForgeConfigSpec.BooleanValue nether_ore;
    public final ForgeConfigSpec.BooleanValue end_ore;
    public final ForgeConfigSpec.BooleanValue other_ore;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> ore_blacklist;

    public final ForgeConfigSpec.DoubleValue crystalSpwanChance;
    public final ForgeConfigSpec.IntValue crystalSpawnTries;
    public final ForgeConfigSpec.BooleanValue nether_crystals;
    public final ForgeConfigSpec.BooleanValue other_crystals;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> crystal_blacklist;

    public WorldGenConfiguration(@Nonnull ForgeConfigSpec.Builder config) {
        retroGen = config.comment("Enable this if you want to get retrogen (generation of ores/crystals) for already existing chunks")
                .define("retrogen", true);
        verbose = config.comment("Enable this if you want to see in the log where crystals are spawned")
                .define("verboseSpawn", false);

        nether_ore = config.comment("Enable this if you want resonating ore in nether biomes")
                .define("generateOreNether", true);
        end_ore = config.comment("Enable this if you want resonating ore in end biomes")
                .define("generateOreEnd", false);
        other_ore = config.comment("Enable this if you want resonating ore in other biomes (if they have stone)")
                .define("generateOreOther", true);
        ore_blacklist = config.comment("Biome blacklist, resonant ore will not spawn in biomes listed here")
                .defineList("otherBiomeBlacklist", Lists.newArrayList("minecraft:the_void"), s -> s instanceof String);

        veinSize = config.comment("Size of resonant ore veins")
                .defineInRange("minOreVeinSize", 3, 1, 8);
        spawnChances = config.comment("Chances for the ore to spawn in a chunk")
                .defineInRange("chancesToSpawn", 5, 1, 16);
        minY = config.comment("Minimum y-level where resonant ore will spawn")
                .defineInRange("minY", 2, 1, 256);
        maxY = config.comment("Maximum y-level where resonant ore will spawn")
                .defineInRange("maxY", 32, 1, 256);

        nether_crystals = config.comment("Enable this if you want resonating crystals in nether biomes")
                .define("generateCrystalsNether", true);
        other_crystals = config.comment("Enable this if you want resonating crystals in other dimensions biomes (if they have stone caves)")
                .define("generateCrystalsOther", true);
        crystal_blacklist = config.comment("Biome blacklist, resonant crystals will not spawn in biomes listed here")
                .defineList("otherBiomeBlacklist", Lists.newArrayList("minecraft:the_void"), s -> s instanceof String);

        crystalSpwanChance = config.comment("The chance that a crystal will spawn in a chunk. (0 = never, 1 = every chunk")
                .defineInRange("crystalSpawnChance", 0.07, 0, 1);
        crystalSpawnTries = config.comment("The number of times that the worldgen will try to spawn a crystal in a chunk before it fails.")
                .defineInRange("crystalSpawnTries", 1, 10, 32);
    }

}
