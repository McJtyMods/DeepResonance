package mcjty.deepresonance.radiation;

import net.minecraftforge.common.config.Configuration;

public class RadiationConfiguration {
    public static final String CATEGORY_RADIATION = "radiation";

    public static float minRadiationRadius = 5.0f;
    public static float maxRadiationRadius = 30.0f;

    public static float minRadiationStrength = 100.0f;
    public static float maxRadiationStrength = 1000000.0f;

    public static float strengthGrowthFactor = 0.001f;
    public static float strengthDecreasePerTick = 5.0f;

    public static float radiationStrenghLevel0 = 50000.0f;
    public static float radiationStrenghLevel1 = 100000.0f;
    public static float radiationStrenghLevel2 = 200000.0f;
    public static float radiationStrenghLevel3 = 500000.0f;
    public static float radiationStrenghLevel4 = 1000000.0f;

    public static float radiationDestructionEventLevel = 200000.0f;
    public static float destructionEventChance = 0.02f;

    public static float maxRadiationMeter = 200000.0f;

    public static float radiationExplosionFactor = 1.3f;

    public static float minimumExplosionMultiplier = 6.0f;
    public static float maximumExplosionMultiplier = 17.0f;
    public static float absoluteMaximumExplosionMultiplier = 20.0f;

    public static float radiationShieldObsidianFactor = 0.2f;
    public static float radiationShieldDenseObsidianFactor = 0.05f;
    public static float radiationShieldDenseGlassFactor = 0.1f;
    public static float radiationShieldLeadFactor = 0.1f;

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

        radiationDestructionEventLevel = (float) cfg.get(CATEGORY_RADIATION, "radiationDestructionEventLevel", radiationDestructionEventLevel,
                "The radiation strength at which point destruction events can happen").getDouble();
        destructionEventChance = (float) cfg.get(CATEGORY_RADIATION, "destructionEventChance", destructionEventChance,
                "Every 10 ticks (half a second) this chance is evaluated to see if there should be a destruction event. 1.0 means it will always occur").getDouble();

        maxRadiationMeter = (float) cfg.get(CATEGORY_RADIATION, "maxRadiationMeter", maxRadiationMeter,
                "The maximum that a radiation meter can measure").getDouble();

        radiationExplosionFactor = (float) cfg.get(CATEGORY_RADIATION, "radiationExplosionFactor", radiationExplosionFactor,
                "This factor increases the radius of radiation on explosion and decreases the strength").getDouble();
        minimumExplosionMultiplier = (float) cfg.get(CATEGORY_RADIATION, "minimumExplosionMultiplier", minimumExplosionMultiplier,
                "The minimum explosion multiplier").getDouble();
        maximumExplosionMultiplier = (float) cfg.get(CATEGORY_RADIATION, "maximumExplosionMultiplier", maximumExplosionMultiplier,
                "The maximum explosion multiplier for a 100%/100% power/strength crystal").getDouble();
        absoluteMaximumExplosionMultiplier = (float) cfg.get(CATEGORY_RADIATION, "absoluteMaximumExplosionMultiplier", absoluteMaximumExplosionMultiplier,
                "The maximum explosion multiplier that is possible. Set to 0 to disable all explosions").getDouble();

        radiationShieldObsidianFactor = (float) cfg.get(CATEGORY_RADIATION, "radiationShieldObsidianFactor", radiationShieldObsidianFactor,
                "How much obsidian blocks radiation (0.0 is total block, 1.0 is not block at all)").getDouble();
        radiationShieldDenseObsidianFactor = (float) cfg.get(CATEGORY_RADIATION, "radiationShieldDenseObsidianFactor", radiationShieldDenseObsidianFactor,
                "How much dense obsidian blocks radiation (0.0 is total block, 1.0 is not block at all)").getDouble();
        radiationShieldDenseGlassFactor = (float) cfg.get(CATEGORY_RADIATION, "radiationShieldDenseGlassFactor", radiationShieldDenseGlassFactor,
                "How much dense glass blocks radiation (0.0 is total block, 1.0 is not block at all)").getDouble();
        radiationShieldLeadFactor = (float) cfg.get(CATEGORY_RADIATION, "radiationShieldLeadFactor", radiationShieldLeadFactor,
                "How much dense lead blocks radiation (0.0 is total block, 1.0 is not block at all)").getDouble();
    }

}
