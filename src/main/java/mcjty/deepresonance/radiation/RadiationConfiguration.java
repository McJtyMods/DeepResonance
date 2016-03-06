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

    public static float radiationEffectLevelNone = 2000.0f;
    public static float radiationEffectLevel0 = 20000.0f;
    public static float radiationEffectLevel1 = 50000.0f;
    public static float radiationEffectLevel2 = 100000.0f;
    public static float radiationEffectLevel3 = 200000.0f;
    public static float radiationEffectLevel4 = 500000.0f;
    public static float radiationEffectLevel5 = 1000000.0f;

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

    public static float suitProtection[] = new float[] { 0, .25f, .50f, .75f, .95f };

    public static int radiationOverlayColor = 0xffff0000;
    public static int radiationOverlayColorNoRadiation = 0xff00ff00;
    public static int radiationOverlayX = 10;
    public static int radiationOverlayY = 10;

    public static int RADIATIONMODULE_RFPERTICK = 6;


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

        radiationEffectLevelNone = (float) cfg.get(CATEGORY_RADIATION, "radiationEffectLevelNone", radiationEffectLevelNone,
                "Below this level no effects occur").getDouble();
        radiationEffectLevel0 = (float) cfg.get(CATEGORY_RADIATION, "radiationEffectLevel0", radiationEffectLevel0,
                "Radiation strength level 0").getDouble();
        radiationEffectLevel1 = (float) cfg.get(CATEGORY_RADIATION, "radiationEffectLevel1", radiationEffectLevel1,
                "Radiation strength level 1").getDouble();
        radiationEffectLevel2 = (float) cfg.get(CATEGORY_RADIATION, "radiationEffectLevel2", radiationEffectLevel2,
                "Radiation strength level 2").getDouble();
        radiationEffectLevel3 = (float) cfg.get(CATEGORY_RADIATION, "radiationEffectLevel3", radiationEffectLevel3,
                "Radiation strength level 3").getDouble();
        radiationEffectLevel4 = (float) cfg.get(CATEGORY_RADIATION, "radiationEffectLevel4", radiationEffectLevel4,
                "Radiation strength level 4").getDouble();
        radiationEffectLevel5 = (float) cfg.get(CATEGORY_RADIATION, "radiationEffectLevel5", radiationEffectLevel5,
                "Radiation strength level 5").getDouble();

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

        suitProtection[1] = (float) cfg.get(CATEGORY_RADIATION, "suitProtection1", suitProtection[1],
                "How much protection you get from radiation with 1 radiation suit piece equipped").getDouble();
        suitProtection[2] = (float) cfg.get(CATEGORY_RADIATION, "suitProtection2", suitProtection[2],
                "How much protection you get from radiation with 2 radiation suit pieces equipped").getDouble();
        suitProtection[3] = (float) cfg.get(CATEGORY_RADIATION, "suitProtection3", suitProtection[3],
                "How much protection you get from radiation with 3 radiation suit pieces equipped").getDouble();
        suitProtection[4] = (float) cfg.get(CATEGORY_RADIATION, "suitProtection4", suitProtection[4],
                "How much protection you get from radiation with 4 radiation suit pieces equipped").getDouble();

        radiationOverlayColor = cfg.get(CATEGORY_RADIATION, "radiationOverlayColor", radiationOverlayColor,
                "The color for the radiation overlay text in case the radiation monitor is in the players hand").getInt();
        radiationOverlayColorNoRadiation = cfg.get(CATEGORY_RADIATION, "radiationOverlayColorNoRadiation", radiationOverlayColorNoRadiation,
                "The color for the radiation overlay text in case the radiation monitor is in the players hand (in case there is no radiation)").getInt();
        radiationOverlayX = cfg.get(CATEGORY_RADIATION, "radiationOverlayX", radiationOverlayX,
                "The X coordinate (with 0 being left) for the radiation overlay text. Use -1 to disable").getInt();
        radiationOverlayY = cfg.get(CATEGORY_RADIATION, "radiationOverlayY", radiationOverlayY,
                "The Y coordinate (with 0 being top) for the radiation overlay text. Use -1 to disable").getInt();

        RADIATIONMODULE_RFPERTICK = cfg.get(CATEGORY_RADIATION, "radiationModuleRFPerTick", RADIATIONMODULE_RFPERTICK,
                                            "RF per tick/per block for the radiation screen module (if rftools is present)").getInt();
    }

}
