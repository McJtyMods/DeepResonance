package mcjty.deepresonance.radiation;

import net.minecraftforge.common.config.Configuration;

public class SuperGenerationConfiguration {
    public static final String CATEGORY_SUPERGEN = "supergen";

    public static int maxResistance = 40000;
    public static int resistanceIncreasePerTick = 200;
    public static int resistanceDecreasePerPulse = 500;
    public static float instabilityHandlingChance = .3f;
    public static float instabilityExplosionThresshold = 2.0f;
    public static float instabilityBigDamageThresshold = 0.7f;
    public static float instabilitySmallDamageThresshold = 0.2f;
    public static float instabilitySensorThresshold = 0.2f;


    public static void init(Configuration cfg) {
        maxResistance = cfg.getInt("maxResistance", CATEGORY_SUPERGEN, maxResistance, 1, 1000000000,
                "Maximum resistance (in microticks)");
        resistanceDecreasePerPulse = cfg.getInt("resistanceDecreasePerPulse", CATEGORY_SUPERGEN, resistanceDecreasePerPulse, 1, 1000000000,
                "How much resistance decreases when a pulse is received (if cooldown is 0)");
        resistanceIncreasePerTick = cfg.getInt("resistanceIncreasePerTick", CATEGORY_SUPERGEN, resistanceIncreasePerTick, 1, 1000000000,
                "How much resistance increases again when idle");
        instabilityHandlingChance = cfg.getFloat("instabilityHandlingChance", CATEGORY_SUPERGEN, instabilityHandlingChance, 0.0f, 1.0f,
                "When the crystal has accumulated instability then this is the chance that (at any tick) we actually handle that instability");
        instabilityExplosionThresshold = cfg.getFloat("instabilityExplosionThresshold", CATEGORY_SUPERGEN, instabilityExplosionThresshold, 0.0f, 10000000.0f,
                "When accumulated instability is handled then we handle a random amount of that instability. When that random amount is greater then this value we cause a massive explosion");
        instabilityBigDamageThresshold = cfg.getFloat("instabilityBigDamageThresshold", CATEGORY_SUPERGEN, instabilityBigDamageThresshold, 0.0f, 10000000.0f,
                "When accumulated instability is handled then we handle a random amount of that instability. When that random amount is greater then this value we cause big damage on the crystal");
        instabilitySmallDamageThresshold = cfg.getFloat("instabilitySmallDamageThresshold", CATEGORY_SUPERGEN, instabilitySmallDamageThresshold, 0.0f, 10000000.0f,
                "When accumulated instability is handled then we handle a random amount of that instability. When that random amount is greater then this value we cause minor damage on the crystal");
        instabilitySensorThresshold = cfg.getFloat("instabilitySensorThresshold", CATEGORY_SUPERGEN, instabilitySensorThresshold, 0.0f, 10000000.0f,
                "The amount of instability in the crystal that corresponds to redstone level 15 in the instability sensor");
    }

}
