package mcjty.deepresonance.modules.pulser.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-7-2020
 */
public class PulserBlockConfig {

    public final ForgeConfigSpec.IntValue powerPerTickIn;
    public final ForgeConfigSpec.IntValue powerMaximum;
    public final ForgeConfigSpec.IntValue powerPerPulse;
    public final ForgeConfigSpec.IntValue crystalRange;

    public PulserBlockConfig(@Nonnull ForgeConfigSpec.Builder builder) {
        this.powerPerTickIn = builder.comment("How much power/t this machine can input from a generator/capacitor")
                .defineInRange("powerPerTickIn", 100, 1, 1000);
        this.powerMaximum = builder.comment("Maximum RF that can be stored in this machine")
                .defineInRange("powerMaximum", 100, 1, 10000);
        this.powerPerPulse = builder.comment("The power needed for a single pulse per redstone power level")
                .defineInRange("powerPerPulse", 4, 1, 1000);
        this.crystalRange = builder.comment("The range of a pulser")
                .defineInRange("crystalRange", 6, 1, 12);
    }

}
