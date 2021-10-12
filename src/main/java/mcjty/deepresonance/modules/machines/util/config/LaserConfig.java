package mcjty.deepresonance.modules.machines.util.config;

import mcjty.deepresonance.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class LaserConfig {

    public static ForgeConfigSpec.IntValue POWER_PER_TICK_IN;
    public static ForgeConfigSpec.IntValue POWER_MAXIMUM;
    public static ForgeConfigSpec.IntValue CRYSTAL_LIQUID_MAXIMUM;
    public static ForgeConfigSpec.IntValue MIN_CRYSTAL_LIQUID_PER_CRYSTAL;
    public static ForgeConfigSpec.IntValue MAX_CRYSTAL_LIQUID_PER_CRYSTAL;

    public static void init() {
        POWER_PER_TICK_IN = Config.SERVER_BUILDER.comment("How much power/t this machine can input from a generator/capacitor")
                .defineInRange("powerPerTickIn", 2000, 0, 10000);
        POWER_MAXIMUM = Config.SERVER_BUILDER.comment("Maximum power that can be stored in this machine")
                .defineInRange("powerMaximum", 10000, 0, 100000);
        CRYSTAL_LIQUID_MAXIMUM = Config.SERVER_BUILDER.comment("The maximum amount of liquified crystal this machine can hold (this is not RCL!)")
                .defineInRange("crystalLiquidMaximum", 8000, 100, 10000);
        MIN_CRYSTAL_LIQUID_PER_CRYSTAL = Config.SERVER_BUILDER.comment("The minimum amount of liquified crystal one crystal will yield (this is not RCL!). This value is for a 0% strength crystal")
                .defineInRange("minCrystalLiquidPerCrystal", 2000, 1, 4000);
        MAX_CRYSTAL_LIQUID_PER_CRYSTAL = Config.SERVER_BUILDER.comment("The maximum amount of liquified crystal one crystal will yield (this is not RCL!). This value is for a 100% strength crystal")
                .defineInRange("maxCrystalLiquidPerCrystal", 8000, 1, 10000);
    }

}
