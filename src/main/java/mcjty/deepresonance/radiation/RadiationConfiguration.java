package mcjty.deepresonance.radiation;

import net.minecraftforge.common.config.Configuration;

public class RadiationConfiguration {
    public static final String CATEGORY_RADIATION = "radiation";

    public static float minRadiationRadius = 5.0f;
    public static float maxRadiationRadius = 100.0f;

    public static float minRadiationStrength = 100.0f;
    public static float maxRadiationStrength = 1000000.0f;

    public static float strengthGrowthFactor = 0.001f;
    public static float strengthDecreasePerTick = 5.0f;

    public static void init(Configuration cfg) {
        minRadiationRadius = (float) cfg.get(CATEGORY_RADIATION, "minRadiationRadius", minRadiationRadius,
                "The minimum radiation radius").getDouble();
        maxRadiationRadius = (float) cfg.get(CATEGORY_RADIATION, "maxRadiationRadius", maxRadiationRadius,
                "The maximum radiation radius for a 100/100/100 crystal").getDouble();

        minRadiationStrength = (float) cfg.get(CATEGORY_RADIATION, "minRadiationStrength", minRadiationStrength,
                "The minimum radiation strength").getDouble();
        maxRadiationStrength = (float) cfg.get(CATEGORY_RADIATION, "maxRadiationStrength", maxRadiationStrength,
                "The maximum radiation strength for a 100/100/100 crystal").getDouble();

        strengthGrowthFactor = (float) cfg.get(CATEGORY_RADIATION, "strengthGrowthFactor", strengthGrowthFactor,
                "How much percentage of the maximum strength the radiation increases every tick").getDouble();
        strengthDecreasePerTick = (float) cfg.get(CATEGORY_RADIATION, "strengthDecreasePerTick", strengthDecreasePerTick,
                "How much the radiation strength decreases every tick").getDouble();
    }

}
