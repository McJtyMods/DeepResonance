package mcjty.deepresonance.modules.machines.util.config;

import mcjty.deepresonance.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class CrystallizerConfig {

    public static ForgeConfigSpec.IntValue POWER_PER_TICK_IN;
    public static ForgeConfigSpec.IntValue POWER_MAXIMUM;
    public static ForgeConfigSpec.IntValue POWER_PER_TICK;
    public static ForgeConfigSpec.IntValue RCL_PER_CRYSTAL;
    public static ForgeConfigSpec.IntValue RCL_PER_TICK;

    public static void init() {
        POWER_PER_TICK_IN = Config.SERVER_BUILDER.comment("How much power/t this machine can input from a generator/capacitor")
                .defineInRange("powerPerTickIn", 200, 0, 1000);
        POWER_PER_TICK = Config.SERVER_BUILDER.comment("How much power this machine consumes per tick while crystalizing")
                .defineInRange("powerPerTick", 20, 0, 1000);
        POWER_MAXIMUM = Config.SERVER_BUILDER.comment("Maximum power that can be stored in this machine")
                .defineInRange("powerMaximum", 10000, 0, 100000);
        RCL_PER_CRYSTAL = Config.SERVER_BUILDER.comment("The amount of RCL that is needed for one crystal")
                .defineInRange("rclPerCrystal", 6000, 100, 80000);
        RCL_PER_TICK = Config.SERVER_BUILDER.comment("The amount of RCL/t that is consumed during crystalizing")
                .defineInRange("rclPerTick", 1, 1, 100000);
    }

}
