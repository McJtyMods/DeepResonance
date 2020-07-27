package mcjty.deepresonance.modules.machines.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 25-7-2020
 */
public class ValveConfig {

    public final ForgeConfigSpec.IntValue ticksPerOperation;
    public final ForgeConfigSpec.IntValue rclPerOperation;

    public ValveConfig(@Nonnull ForgeConfigSpec.Builder builder) {
        this.ticksPerOperation = builder.comment("The amount of ticks between a transfer operation")
                .defineInRange("ticksPerOperation", 5, 1, 300);
        this.rclPerOperation = builder.comment("The amount of RCL to transfer in one operation")
                .defineInRange("rclPerOperation", 1, 100, 10000);
    }

}
