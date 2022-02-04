package mcjty.deepresonance.modules.core.util;

import mcjty.deepresonance.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class ResonatingPlateBlockConfig {

    public static ForgeConfigSpec.IntValue RADIATION_STRENGTH;
    public static ForgeConfigSpec.IntValue RADIATION_RADIUS;
    public static ForgeConfigSpec.IntValue RADIATION_TICKS;

    public static void init() {
        Config.SERVER_BUILDER.push("plate");

        RADIATION_STRENGTH = Config.SERVER_BUILDER.comment("Strength of radiation that a plate block gives when it has a redstone signal. 0 to disable")
                .defineInRange("radiationStrength", 20000, 0, 100000);
        RADIATION_RADIUS = Config.SERVER_BUILDER.comment("Radius of radiation that a plate block gives when it has a redstone signal")
                .defineInRange("radiationRadius", 10, 8, 128);
        RADIATION_TICKS = Config.SERVER_BUILDER.comment("Amount of ticks that the radiation from a plate block lasts")
                .defineInRange("radiationTicks", 100, 20, 72000);

        Config.SERVER_BUILDER.pop();
    }

}
