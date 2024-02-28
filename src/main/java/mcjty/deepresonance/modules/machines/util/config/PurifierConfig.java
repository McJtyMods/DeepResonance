package mcjty.deepresonance.modules.machines.util.config;

import mcjty.deepresonance.setup.Config;
import net.neoforged.neoforge.common.ForgeConfigSpec;

public class PurifierConfig {

    public static ForgeConfigSpec.IntValue TICKS_PER_PURIFY;
    public static ForgeConfigSpec.IntValue RCL_PER_PURIFY;
    public static ForgeConfigSpec.IntValue ADDED_PURITY;
    public static ForgeConfigSpec.IntValue MAX_PURITY;

    public static void init() {
        Config.SERVER_BUILDER.push("purifier");
        TICKS_PER_PURIFY = Config.SERVER_BUILDER.comment("Amount of ticks needed to purify one unit of RCL")
                .defineInRange("ticksPerPurify", 100, 1, 10000);
        RCL_PER_PURIFY = Config.SERVER_BUILDER.comment("The amount of RCL we purify as one unit")
                .defineInRange("rclPerPurify", 200, 1, 10000);
        ADDED_PURITY = Config.SERVER_BUILDER.comment("How much the purifier adds to the purity of a liquid (in %)")
                .defineInRange("addedPurity", 25, 1, 100);
        MAX_PURITY = Config.SERVER_BUILDER.comment("Maximum purity that the purifier can handle (in %)")
                .defineInRange("maxPurity", 85, 1, 100);
        Config.SERVER_BUILDER.pop();
    }

}
