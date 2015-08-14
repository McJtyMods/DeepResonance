package mcjty.deepresonance.radiation;

import net.minecraftforge.common.config.Configuration;

public class RadiationConfiguration {
    public static final String CATEGORY_RADIATION = "radiation";

    public static float minRadiationRadius = 5.0f;
    public static float maxRadiationRadius = 60.0f;

    public static float minRadiationStrength = 100.0f;
    public static float maxRadiationStrength = 1000000.0f;

    public static float strengthGrowthFactor = 0.001f;
    public static float strengthDecreasePerTick = 5.0f;

    public static float radiationStrenghLevel0 = 50000.0f;
    public static float radiationStrenghLevel1 = 100000.0f;
    public static float radiationStrenghLevel2 = 200000.0f;
    public static float radiationStrenghLevel3 = 500000.0f;
    public static float radiationStrenghLevel4 = 1000000.0f;

    public static float maxRadiationMeter = 200000.0f;

    public static float radiationExplosionFactor = 1.3f;

    public static float minimumExplosionMultiplier = 6.0f;
    public static float maximumExplosionMultiplier = 17.0f;
    public static float absoluteMaximumExplosionMultiplier = 20.0f;

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

        radiationStrenghLevel0 = (float) cfg.get(CATEGORY_RADIATION, "radiationStrenghLevel0", radiationStrenghLevel0,
                "Radiation strength level 0").getDouble();
        radiationStrenghLevel1 = (float) cfg.get(CATEGORY_RADIATION, "radiationStrenghLevel1", radiationStrenghLevel1,
                "Radiation strength level 1").getDouble();
        radiationStrenghLevel2 = (float) cfg.get(CATEGORY_RADIATION, "radiationStrenghLevel2", radiationStrenghLevel2,
                "Radiation strength level 2").getDouble();
        radiationStrenghLevel3 = (float) cfg.get(CATEGORY_RADIATION, "radiationStrenghLevel3", radiationStrenghLevel3,
                "Radiation strength level 3").getDouble();
        radiationStrenghLevel4 = (float) cfg.get(CATEGORY_RADIATION, "radiationStrenghLevel4", radiationStrenghLevel4,
                "Radiation strength level 4").getDouble();

        maxRadiationMeter = (float) cfg.get(CATEGORY_RADIATION, "maxRadiationMeter", maxRadiationMeter,
                "The maximum that a radiation meter can measure").getDouble();

        radiationExplosionFactor = (float) cfg.get(CATEGORY_RADIATION, "radiationExplosionFactor", radiationExplosionFactor,
                "This factor increases the radius of radiation on explosion and decreases the strength").getDouble();
        minimumExplosionMultiplier = (float) cfg.get(CATEGORY_RADIATION, "minimumExplosionMultiplier", minimumExplosionMultiplier,
                "The minimum explosion multiplier").getDouble();
        maximumExplosionMultiplier = (float) cfg.get(CATEGORY_RADIATION, "maximumExplosionMultiplier", maximumExplosionMultiplier,
                "The maximum explosion multiplier for a 100%/100% power/strength crystal").getDouble();
        absoluteMaximumExplosionMultiplier = (float) cfg.get(CATEGORY_RADIATION, "absoluteMaximumExplosionMultiplier", absoluteMaximumExplosionMultiplier,
                "The maximum explosion multiplier that is possible").getDouble();
    }

}
