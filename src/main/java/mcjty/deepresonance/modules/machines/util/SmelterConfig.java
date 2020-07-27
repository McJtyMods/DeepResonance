package mcjty.deepresonance.modules.machines.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-7-2020
 */
public class SmelterConfig {

    public final ForgeConfigSpec.IntValue powerPerTickIn;
    public final ForgeConfigSpec.IntValue powerPerOreTick;
    public final ForgeConfigSpec.IntValue powerMaximum;
    public final ForgeConfigSpec.IntValue processTime;
    public final ForgeConfigSpec.IntValue lavaCost;
    public final ForgeConfigSpec.IntValue rclPerOre;


    public SmelterConfig(@Nonnull ForgeConfigSpec.Builder builder) {
        this.powerPerTickIn = builder.comment("How much power/t this machine can input from a generator/capacitor")
                .defineInRange("powerPerTickIn", 200, 0, 1000);
        this.powerPerOreTick = builder.comment("How much power/t this machine consumes during smelting ores")
                .defineInRange("powerPerOreTick", 10, 0, 1000);
        this.powerMaximum = builder.comment("Maximum power that can be stored in this machine")
                .defineInRange("powerMaximum", 5000, 0, 100000);
        this.processTime = builder.comment("The number of ticks to smelt one ore")
                .defineInRange("processTime", 200, 10, 1000);
        this.lavaCost = builder.comment("The amount of lava to smelt one ore")
                .defineInRange("lavaCost", 200, 100, 10000);
        this.rclPerOre = builder.comment("The amount of RCL to produce with one ore")
                .defineInRange("rclPerOre", 200, 50, 1000);
    }

}
