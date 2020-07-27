package mcjty.deepresonance.modules.machines.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-7-2020
 */
public class CrystallizerConfig {

    public final ForgeConfigSpec.IntValue powerPerTickIn;
    public final ForgeConfigSpec.IntValue powerPerRcl;
    public final ForgeConfigSpec.IntValue powerMaximum;
    public final ForgeConfigSpec.IntValue rclPerCrystal;
    public final ForgeConfigSpec.IntValue rclPerTick;

    public CrystallizerConfig(@Nonnull ForgeConfigSpec.Builder builder) {
        this.powerPerTickIn = builder.comment("How much power/t this machine can input from a generator/capacitor")
                .defineInRange("powerPerTickIn", 200, 0, 1000);
        this.powerPerRcl = builder.comment("How much power this machine consumes for one crystalizing step")
                .defineInRange("powerPerRcl", 20, 0, 1000);
        this.powerMaximum = builder.comment("Maximum power that can be stored in this machine")
                .defineInRange("powerMaximum", 10000, 0, 100000);
        this.rclPerCrystal = builder.comment("The amount of RCL that is needed for one crystal")
                .defineInRange("rclPerCrystal", 6000, 100, 80000);
        this.rclPerTick = builder.comment("The amount of RCL/t that is consumed during crystalizing")
                .defineInRange("rclPerTick", 1, 1, 100000);
    }

}
