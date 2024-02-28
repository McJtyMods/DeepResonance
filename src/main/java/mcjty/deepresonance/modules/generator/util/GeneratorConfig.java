package mcjty.deepresonance.modules.generator.util;

import mcjty.deepresonance.setup.Config;
import net.neoforged.neoforge.common.ForgeConfigSpec;

public class GeneratorConfig {

    public static ForgeConfigSpec.IntValue STARTUP_TIME;
    public static ForgeConfigSpec.IntValue SHUTDOWN_TIME;

    public static ForgeConfigSpec.DoubleValue BASE_GENERATOR_VOLUME;
    public static ForgeConfigSpec.DoubleValue LOOP_VOLUME_FACTOR;

    public static ForgeConfigSpec.IntValue POWER_STORAGE_PER_BLOCK;
    public static ForgeConfigSpec.IntValue POWER_PER_TICKOUT;

    public static ForgeConfigSpec.IntValue MAX_CRYSTALS_PER_BLOCK;
    public static ForgeConfigSpec.IntValue MAX_POWER_INPUT_PER_BLOCK;

    public static void init() {
        STARTUP_TIME = Config.SERVER_BUILDER.comment("Startup time before the generator is active")
                .defineInRange("startupTime", 70, 20, 2000);
        SHUTDOWN_TIME = Config.SERVER_BUILDER.comment("Shutdown time")
                .defineInRange("shutdownTime", 70, 20, 2000);
        POWER_STORAGE_PER_BLOCK = Config.SERVER_BUILDER.comment("Maximum amount of power per generator block")
                .defineInRange("powerStoragePerBlock", 50000, 1000, 100000);
        POWER_PER_TICKOUT = Config.SERVER_BUILDER.comment("Output power per tick")
                .defineInRange("powerPerTickOut", 20000, 10, 100000);
        MAX_CRYSTALS_PER_BLOCK = Config.SERVER_BUILDER.comment("Maximum number of crystals supported per block")
                .defineInRange("maxCrystalsPerBlock", 2, 1, 8);
        MAX_POWER_INPUT_PER_BLOCK = Config.SERVER_BUILDER.comment("Maximum power input per block")
                .defineInRange("maxPowerInputPerBlock", 10000, 100, 50000);

        BASE_GENERATOR_VOLUME = Config.CLIENT_BUILDER.comment("Base generator volume")
                .defineInRange("baseGeneratorVolume", 1.0, 0, 1);
        LOOP_VOLUME_FACTOR = Config.CLIENT_BUILDER.comment("Loop volume factor")
                .defineInRange("loopVolumeFactor", 1.0, 0, 1);
    }

}
