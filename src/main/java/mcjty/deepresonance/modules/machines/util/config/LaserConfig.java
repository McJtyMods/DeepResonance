package mcjty.deepresonance.modules.machines.util.config;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-7-2020
 */
public class LaserConfig {

    public final ForgeConfigSpec.IntValue powerPerTickIn;
    public final ForgeConfigSpec.IntValue powerMaximum;
    public final ForgeConfigSpec.IntValue crystalLiquidMaximum;
    public final ForgeConfigSpec.IntValue minCrystalLiquidPerCrystal;
    public final ForgeConfigSpec.IntValue maxCrystalLiquidPerCrystal;

    public LaserConfig(@Nonnull ForgeConfigSpec.Builder builder) {
        this.powerPerTickIn = builder.comment("How much power/t this machine can input from a generator/capacitor")
                .defineInRange("powerPerTickIn", 2000, 0, 10000);
        this.powerMaximum = builder.comment("Maximum power that can be stored in this machine")
                .defineInRange("powerMaximum", 10000, 0, 100000);
        this.crystalLiquidMaximum = builder.comment("The maximum amount of liquified crystal this machine can hold (this is not RCL!)")
                .defineInRange("crystalLiquidMaximum", 8000, 100, 10000);
        this.minCrystalLiquidPerCrystal = builder.comment("The minimum amount of liquified crystal one crystal will yield (this is not RCL!). This value is for a 0% strength crystal")
                .defineInRange("minCrystalLiquidPerCrystal", 2000, 1, 4000);
        this.maxCrystalLiquidPerCrystal = builder.comment("The maximum amount of liquified crystal one crystal will yield (this is not RCL!). This value is for a 100% strength crystal")
                .defineInRange("maxCrystalLiquidPerCrystal", 8000, 1, 10000);
    }

}
