package mcjty.deepresonance.modules.pulser.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-8-2020
 */
public class PulserCrystalConfig {

    public final ForgeConfigSpec.IntValue MAX_RESISTANCE;
    public final ForgeConfigSpec.IntValue RESISTANCE_INCREASE_TICK;
    public final ForgeConfigSpec.IntValue RESISTANCE_DECREASE_PULSE;
    public final ForgeConfigSpec.DoubleValue INSTABILITY_HANDLING_CHANCE;
    public final ForgeConfigSpec.DoubleValue INSTABILITY_EXPLOSION_THRESHOLD;
    public final ForgeConfigSpec.DoubleValue INSTABILITY_BIG_DAMAGE_THRESHOLD;
    public final ForgeConfigSpec.DoubleValue INSTABILITY_SMALL_DAMAGE_THRESHOLD;
    public final ForgeConfigSpec.DoubleValue INSTABILITY_SENSOR_THRESHOLD;

    public PulserCrystalConfig(@Nonnull ForgeConfigSpec.Builder config) {
        MAX_RESISTANCE = config.comment("Maximum resistance")
                .defineInRange("maxResistance", 40000, 1, 1000000000);
        RESISTANCE_INCREASE_TICK = config.comment("How much resistance increases when idle")
                .defineInRange("resistanceIncreasePerTick", 200, 1, 1000000000);
        RESISTANCE_DECREASE_PULSE = config.comment("How much resistance decreases when a pulse is received (if cooldown is 0)")
                .defineInRange("resistanceDecreasePerPulse", 500, 1, 1000000000);

        INSTABILITY_HANDLING_CHANCE = config.comment("When the crystal has accumulated instability then this is the chance that (at any tick) we actually handle that instability")
                .defineInRange("instabilityHandlingChance", 0.3, 0, 1);
        INSTABILITY_EXPLOSION_THRESHOLD = config.comment("When accumulated instability is handled then we handle a random amount of that instability. When that random amount is greater then this value we cause a massive explosion")
                .defineInRange("instabilityExplosionThreshold", 5d, 0, 10000000);
        INSTABILITY_BIG_DAMAGE_THRESHOLD = config.comment("When accumulated instability is handled then we handle a random amount of that instability. When that random amount is greater then this value we cause big damage on the crystal")
                .defineInRange("instabilityBigDamageThreshold", 1.25, 0, 10000000);
        INSTABILITY_SMALL_DAMAGE_THRESHOLD = config.comment("When accumulated instability is handled then we handle a random amount of that instability. When that random amount is greater then this value we cause minor damage on the crystal")
                .defineInRange("instabilitySmallDamageThreshold", 0.5, 0, 10000000);

        INSTABILITY_SENSOR_THRESHOLD = config.comment("The amount of instability in the crystal that corresponds to redstone level 15 in the instability sensor")
                .defineInRange("instabilitySensorThreshold", 0.3, 0, 10000000);
    }

}
