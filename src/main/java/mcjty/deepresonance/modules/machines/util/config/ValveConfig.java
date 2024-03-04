package mcjty.deepresonance.modules.machines.util.config;

import mcjty.deepresonance.setup.Config;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ValveConfig {

    public static ModConfigSpec.IntValue TICKS_PER_OPERATION;
    public static ModConfigSpec.IntValue RCL_PER_OPERATION;

    public static void init() {
        Config.SERVER_BUILDER.push("valve");
        TICKS_PER_OPERATION = Config.SERVER_BUILDER.comment("The amount of ticks between a transfer operation")
                .defineInRange("ticksPerOperation", 5, 1, 300);
        RCL_PER_OPERATION = Config.SERVER_BUILDER.comment("The amount of RCL to transfer in one operation")
                .defineInRange("rclPerOperation", 1, 100, 10000);
        Config.SERVER_BUILDER.pop();
    }

}
