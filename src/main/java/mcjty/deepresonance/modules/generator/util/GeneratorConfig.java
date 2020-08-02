package mcjty.deepresonance.modules.generator.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 30-7-2020
 */
public class GeneratorConfig {

    public final ForgeConfigSpec.IntValue startupTime;
    public final ForgeConfigSpec.IntValue shutdownTime;

    public final ForgeConfigSpec.DoubleValue baseGeneratorVolume;
    public final ForgeConfigSpec.DoubleValue loopVolumeFactor;

    public final ForgeConfigSpec.IntValue powerStoragePerBlock;
    public final ForgeConfigSpec.IntValue powerPerTickOut;

    public final ForgeConfigSpec.IntValue maxCrystalsPerBlock;
    public final ForgeConfigSpec.IntValue maxPowerInputPerBlock;

    public GeneratorConfig(@Nonnull ForgeConfigSpec.Builder builder) {
        this.startupTime = builder.comment("")
                .defineInRange("startupTime", 70, 20, 2000);
        this.shutdownTime = builder.comment("")
                .defineInRange("shutdownTime", 70, 20, 2000);
        this.baseGeneratorVolume = builder.comment("")
                .defineInRange("baseGeneratorVolume", 1.0, 0, 1);
        this.loopVolumeFactor = builder.comment("")
                .defineInRange("loopVolumeFactor", 1.0, 0, 1);
        this.powerStoragePerBlock = builder.comment("")
                .defineInRange("powerStoragePerBlock", 50000, 1000, 100000);
        this.powerPerTickOut = builder.comment("")
                .defineInRange("powerPerTickOut", 20000, 10, 100000);
        this.maxCrystalsPerBlock = builder.comment("")
                .defineInRange("maxCrystalsPerBlock", 2, 1, 8);
        this.maxPowerInputPerBlock = builder.comment("")
                .defineInRange("maxPowerInputPerBlock", 10000, 100, 50000);
    }

}
