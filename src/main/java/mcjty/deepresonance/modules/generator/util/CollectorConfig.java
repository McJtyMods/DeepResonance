package mcjty.deepresonance.modules.generator.util;

import mcjty.deepresonance.setup.Config;
import net.neoforged.neoforge.common.ForgeConfigSpec;

public class CollectorConfig {

    public static ForgeConfigSpec.IntValue MAX_HORIZONTAL_CRYSTAL_DISTANCE;
    public static ForgeConfigSpec.IntValue MAX_VERTICAL_CRYSTAL_DISTANCE;

    public static void init() {
        MAX_HORIZONTAL_CRYSTAL_DISTANCE = Config.SERVER_BUILDER.comment("Maximum horizontal distance to look for crystals")
                .defineInRange("maxHorizontalCrystalDistance", 10, 1, 16);
        MAX_VERTICAL_CRYSTAL_DISTANCE = Config.SERVER_BUILDER.comment("Maximum vertical distance to look for crystals")
                .defineInRange("maxVerticalCrystalDistance", 1, 1, 16);
    }

}
