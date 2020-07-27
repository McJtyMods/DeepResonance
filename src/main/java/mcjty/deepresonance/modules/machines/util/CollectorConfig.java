package mcjty.deepresonance.modules.machines.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-7-2020
 */
public class CollectorConfig {

    public final ForgeConfigSpec.IntValue maxHorizontalCrystalDistance;
    public final ForgeConfigSpec.IntValue maxVerticalCrystalDistance;

    public CollectorConfig(@Nonnull ForgeConfigSpec.Builder builder) {
        this.maxHorizontalCrystalDistance = builder.comment("Maximum horizontal distance to look for crystals")
                .defineInRange("maxHorizontalCrystalDistance", 10, 1, 16);
        this.maxVerticalCrystalDistance = builder.comment("Maximum vertical distance to look for crystals")
                .defineInRange("maxVerticalCrystalDistance", 1, 1, 16);
    }

}
