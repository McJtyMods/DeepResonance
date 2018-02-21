package mcjty.deepresonance.radiation;

import net.minecraftforge.common.config.Configuration;

public class SuperGenerationConfiguration {
    public static final String CATEGORY_SUPERGEN = "supergen";

    public static int maxResistance = 40000;
    public static int resistanceIncreasePerTick = 200;
    public static int resistanceDecreasePerPulse = 500;
    public static float dangerHandlingChance = .3f;
    public static float dangerExplosionThresshold = 2.0f;
    public static float dangerBigDamageThresshold = 0.7f;
    public static float dangerSmallDamageThresshold = 0.2f;


    public static void init(Configuration cfg) {
        maxResistance = cfg.getInt("maxResistance", CATEGORY_SUPERGEN, maxResistance, 1, 1000000000,
                "Maximum resistance (in microticks)");
        resistanceDecreasePerPulse = cfg.getInt("resistanceDecreasePerPulse", CATEGORY_SUPERGEN, resistanceDecreasePerPulse, 1, 1000000000,
                "How much resistance decreases when a pulse is received (if cooldown is 0)");
        resistanceIncreasePerTick = cfg.getInt("resistanceIncreasePerTick", CATEGORY_SUPERGEN, resistanceIncreasePerTick, 1, 1000000000,
                "How much resistance increases again when idle");
        dangerHandlingChance = cfg.getFloat("dangerHandlingChance", CATEGORY_SUPERGEN, dangerHandlingChance, 0.0f, 1.0f,
                "When the crystal has accumulated danger then this is the chance that (at any tick) we actually handle that danger");
        dangerExplosionThresshold = cfg.getFloat("dangerExplosionThresshold", CATEGORY_SUPERGEN, dangerExplosionThresshold, 0.0f, 10000000.0f,
                "When accumulated danger is handled then we handle a random amount of that danger. When that random amount is greater then this value we cause a massive explosion");
        dangerBigDamageThresshold = cfg.getFloat("dangerBigDamageThresshold", CATEGORY_SUPERGEN, dangerBigDamageThresshold, 0.0f, 10000000.0f,
                "When accumulated danger is handled then we handle a random amount of that danger. When that random amount is greater then this value we cause big damage on the crystal");
        dangerSmallDamageThresshold = cfg.getFloat("dangerSmallDamageThresshold", CATEGORY_SUPERGEN, dangerSmallDamageThresshold, 0.0f, 10000000.0f,
                "When accumulated danger is handled then we handle a random amount of that danger. When that random amount is greater then this value we cause minor damage on the crystal");
    }

}
