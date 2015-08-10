package mcjty.deepresonance.blocks.crystals;

import net.minecraftforge.common.config.Configuration;

public class ResonatingCrystalConfiguration {
    public static final String CATEGORY_CRYSTALS = "crystals";

    public static int maximumRF = 500000000;      // The maximum RF that a crystal with 100% power can hold.
    public static int maximumRFtick = 50000;      // The maximum RF/tick that a crystal with 100% efficiency can give.

    public static void init(Configuration cfg) {

        maximumRF = cfg.get(CATEGORY_CRYSTALS, "maximumRF", maximumRF, "The maximum RF that a crystal with 100% power can hold").getInt();
        maximumRFtick = cfg.get(CATEGORY_CRYSTALS, "maximumRFtick", maximumRFtick, "The maximum RF/tick that a crystal with 100% efficiency can give").getInt();
    }
}
