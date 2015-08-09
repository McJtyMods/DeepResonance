package mcjty.deepresonance.blocks.generator;

import net.minecraftforge.common.config.Configuration;

public class GeneratorConfiguration {
    public static final String CATEGORY_GENERATOR = "generator";

    public static int startupTime = 70;
    public static int shutdownTime = 70;

    public static void init(Configuration cfg) {
        startupTime = cfg.get(CATEGORY_GENERATOR, "startupTime", startupTime, "Startup time of the generator (in ticks)").getInt();
        shutdownTime = cfg.get(CATEGORY_GENERATOR, "shutdownTime", shutdownTime, "Shutdown time of the generator (in ticks)").getInt();
    }
}
