package mcjty.deepresonance.modules.core.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

public class CrystalConfig {

    public final ForgeConfigSpec.IntValue MAX_POWER_STORED;
    public final ForgeConfigSpec.IntValue MAX_POWER_TICK;

    public CrystalConfig(@Nonnull ForgeConfigSpec.Builder config) {
        MAX_POWER_STORED = config.comment("The maximum kilo-RF (per 1000, so 1000 = 1milion RF) that a crystal with 100% power can hold")
                .defineInRange("maximunStoredPower", 1000000, 1, 2000000000);
        MAX_POWER_TICK = config.comment("The maximum RF/tick that a crystal with 100% efficiency can give")
                .defineInRange("maximumPowerTick", 20000, 1, 20000);
    }

}
