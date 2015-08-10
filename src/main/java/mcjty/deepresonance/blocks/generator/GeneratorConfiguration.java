package mcjty.deepresonance.blocks.generator;

import net.minecraftforge.common.config.Configuration;

public class GeneratorConfiguration {
    public static final String CATEGORY_GENERATOR = "generator";

    // Engine startup/shutdown time
    public static int startupTime = 70;
    public static int shutdownTime = 70;

    public static float baseGeneratorVolume = 1.0f;     // Use 0 to turn off generator sounds
    public static float loopVolumeFactor = 1.0f;        // How much to decrease volume of the looping sound.

    public static int rfPerGeneratorBlock = 500000;              // Amount of RF stored per block
    public static int rfPerTickGenerator = 100000;               // Maximum RF/t output per side of the block

    public static void init(Configuration cfg) {
        startupTime = cfg.get(CATEGORY_GENERATOR, "startupTime", startupTime, "Startup time of the generator (in ticks)").getInt();
        shutdownTime = cfg.get(CATEGORY_GENERATOR, "shutdownTime", shutdownTime, "Shutdown time of the generator (in ticks)").getInt();

        baseGeneratorVolume = (float) cfg.get(CATEGORY_GENERATOR, "baseGeneratorVolume", baseGeneratorVolume,
                "The volume for the generator sound (1.0 is default, 0.0 is off)").getDouble();
        loopVolumeFactor = (float) cfg.get(CATEGORY_GENERATOR, "loopVolumeFactor", loopVolumeFactor,
                "Relative volume of the generator looping sound. With 1.0 the looping sound has equal loudness as the generator base volume").getDouble();

        rfPerGeneratorBlock = cfg.get(CATEGORY_GENERATOR, "rfPerGeneratorBlock", rfPerGeneratorBlock, "The RF storage per generator block").getInt();
        rfPerTickGenerator = cfg.get(CATEGORY_GENERATOR, "rfPerTickGenerator", rfPerTickGenerator, "The RF/t output for a given side of the generator block").getInt();
    }
}
