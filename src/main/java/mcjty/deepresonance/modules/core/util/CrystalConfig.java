package mcjty.deepresonance.modules.core.util;

import elec332.core.api.config.IConfigurableElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 18-1-2020
 */
public class CrystalConfig implements IConfigurableElement {

    public static ForgeConfigSpec.IntValue MAX_POWER_STORED;
    public static ForgeConfigSpec.IntValue MAX_POWER_TICK;

    public static ForgeConfigSpec.IntValue MAX_RESISTANCE;
    public static ForgeConfigSpec.IntValue RESISTANCE_INCREASE_TICK;
    public static ForgeConfigSpec.IntValue RESISTANCE_DECREASE_PULSE;
    public static ForgeConfigSpec.DoubleValue INSTABILITY_HANDLING_CHANCE;
    public static ForgeConfigSpec.DoubleValue INSTABILITY_EXPLOSION_THRESHOLD;
    public static ForgeConfigSpec.DoubleValue INSTABILITY_BIG_DAMAGE_THRESHOLD;
    public static ForgeConfigSpec.DoubleValue INSTABILITY_SMALL_DAMAGE_THRESHOLD;
    public static ForgeConfigSpec.DoubleValue INSTABILITY_SENSOR_THRESHOLD;

    @Override
    public void registerProperties(@Nonnull ForgeConfigSpec.Builder config, ModConfig.Type type) {
        MAX_POWER_STORED = config.comment("The maximum kilo-RF (per 1000, so 1000 = 1milion RF) that a crystal with 100% power can hold")
                .defineInRange("maximunStoredPower", 1000000, 1, 2000000000);
        MAX_POWER_TICK = config.comment("The maximum RF/tick that a crystal with 100% efficiency can give")
                .defineInRange("maximumPowerTick", 20000, 1, 20000);

        MAX_RESISTANCE = config.comment("Maximum resistance")
                .defineInRange("maxResistance", 40000, 1, 1000000000);
        RESISTANCE_INCREASE_TICK = config.comment("How much resistance increases when idle")
                .defineInRange("resistanceIncreasePerTick", 200, 1, 1000000000);
        RESISTANCE_DECREASE_PULSE = config.comment("How much resistance decreases when a pulse is received (if cooldown is 0)")
                .defineInRange("resistanceDecreasePerPulse", 500, 1, 1000000000);

        INSTABILITY_HANDLING_CHANCE = config.comment("When the crystal has accumulated instability then this is the chance that (at any tick) we actually handle that instability")
                .defineInRange("instabilityHandlingChance", 0.3, 0, 1);
        INSTABILITY_EXPLOSION_THRESHOLD = config.comment("When accumulated instability is handled then we handle a random amount of that instability. When that random amount is greater then this value we cause a massive explosion")
                .defineInRange("instabilityExplosionThresshold", 5d, 0, 10000000);
        INSTABILITY_BIG_DAMAGE_THRESHOLD = config.comment("When accumulated instability is handled then we handle a random amount of that instability. When that random amount is greater then this value we cause big damage on the crystal")
                .defineInRange("instabilityBigDamageThresshold", 1.25, 0, 10000000);
        INSTABILITY_SMALL_DAMAGE_THRESHOLD = config.comment("When accumulated instability is handled then we handle a random amount of that instability. When that random amount is greater then this value we cause minor damage on the crystal")
                .defineInRange("instabilitySmallDamageThresshold", 0.5, 0, 10000000);

        INSTABILITY_SENSOR_THRESHOLD = config.comment("The amount of instability in the crystal that corresponds to redstone level 15 in the instability sensor")
                .defineInRange("instabilitySensorThresshold", 0.3, 0, 10000000);

    }

}
