package mcjty.deepresonance.modules.core.util;

import mcjty.deepresonance.setup.Config;
import net.neoforged.neoforge.common.ForgeConfigSpec;

public class CrystalConfig {

    public static ForgeConfigSpec.IntValue MAX_POWER_STORED;
    public static ForgeConfigSpec.IntValue MAX_POWER_TICK;

    public static void init() {
        Config.SERVER_BUILDER.push("crystals");

        MAX_POWER_STORED = Config.SERVER_BUILDER.comment("The maximum kilo-RF (per 1000, so 1000 = 1milion RF) that a crystal with 100% power can hold")
                .defineInRange("maximumStoredPower", 1000000, 1, 2000000000);
        MAX_POWER_TICK = Config.SERVER_BUILDER.comment("The maximum RF/tick that a crystal with 100% efficiency can give")
                .defineInRange("maximumPowerTick", 20000, 1, 20000);

        Config.SERVER_BUILDER.pop();
    }

}
