package mcjty.deepresonance.modules.machines.util.config;

import mcjty.deepresonance.setup.Config;
import net.neoforged.neoforge.common.ModConfigSpec;

public class SmelterConfig {

    public static ModConfigSpec.IntValue POWER_PER_TICK_IN;
    public static ModConfigSpec.IntValue POWER_PER_ORE_TICK;
    public static ModConfigSpec.IntValue POWER_MAXIMUM;
    public static ModConfigSpec.IntValue PROCESS_TIME;
    public static ModConfigSpec.IntValue LAVA_COST;
    public static ModConfigSpec.IntValue RCL_PER_ORE;


    public static void init() {
        Config.SERVER_BUILDER.push("smelter");
        POWER_PER_TICK_IN = Config.SERVER_BUILDER.comment("How much power/t this machine can input from a generator/capacitor")
                .defineInRange("powerPerTickIn", 200, 0, 1000);
        POWER_PER_ORE_TICK = Config.SERVER_BUILDER.comment("How much power/t this machine consumes during smelting ores")
                .defineInRange("powerPerOreTick", 10, 0, 1000);
        POWER_MAXIMUM = Config.SERVER_BUILDER.comment("Maximum power that can be stored in this machine")
                .defineInRange("powerMaximum", 5000, 0, 100000);
        PROCESS_TIME = Config.SERVER_BUILDER.comment("The number of ticks to smelt one ore")
                .defineInRange("processTime", 200, 10, 1000);
        LAVA_COST = Config.SERVER_BUILDER.comment("The amount of lava to smelt one ore")
                .defineInRange("lavaCost", 200, 100, 10000);
        RCL_PER_ORE = Config.SERVER_BUILDER.comment("The amount of RCL to produce with one ore")
                .defineInRange("rclPerOre", 200, 50, 1000);
        Config.SERVER_BUILDER.pop();
    }

}
