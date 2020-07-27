package mcjty.deepresonance.modules.machines.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-7-2020
 */
public class PurifierConfig {

    public final ForgeConfigSpec.IntValue ticksPerPurify;
    public final ForgeConfigSpec.IntValue rclPerPurify;
    public final ForgeConfigSpec.IntValue addedPurity;
    public final ForgeConfigSpec.IntValue maxPurity;

    public PurifierConfig(@Nonnull ForgeConfigSpec.Builder builder) {
        this.ticksPerPurify = builder.comment("Amount of ticks needed to purify one unit of RCL")
                .defineInRange("ticksPerPurify", 100, 1, 10000);
        this.rclPerPurify = builder.comment("The amount of RCL we purify as one unit")
                .defineInRange("rclPerPurify", 200, 1, 10000);
        this.addedPurity = builder.comment("How much the purifier adds to the purity of a liquid (in %)")
                .defineInRange("addedPurity", 25, 1, 100);
        this.maxPurity = builder.comment("Maximum purity that the purifier can handle (in %)")
                .defineInRange("maxPurity", 85, 1, 100);
    }

}
