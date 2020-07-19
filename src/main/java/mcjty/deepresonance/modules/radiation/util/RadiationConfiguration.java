package mcjty.deepresonance.modules.radiation.util;

import elec332.core.api.config.IConfigurableElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 12-7-2020
 */
public class RadiationConfiguration implements IConfigurableElement {

    public static ForgeConfigSpec.DoubleValue MIN_RADIATION_RADIUS;
    public static ForgeConfigSpec.DoubleValue MAX_RADIATION_RADIUS;

    public static ForgeConfigSpec.DoubleValue MIN_RADIATION_STRENGTH;
    public static ForgeConfigSpec.DoubleValue MAX_RADIATION_STRENGTH;

    public static ForgeConfigSpec.DoubleValue STRENGTH_GROWTH_FACTOR;
    public static ForgeConfigSpec.DoubleValue STRENGTH_DECREASE_TICK;

    public static ForgeConfigSpec.DoubleValue RADIATION_DESTRUCTION_EVENT_LEVEL;
    public static ForgeConfigSpec.DoubleValue RADIATION_DESTRUCTION_EVENT_CHANCE;

    public static ForgeConfigSpec.DoubleValue RADIATION_EFFECT_LEVEL_NONE;
    public static ForgeConfigSpec.DoubleValue RADIATION_EFFECT_LEVEL_0;
    public static ForgeConfigSpec.DoubleValue RADIATION_EFFECT_LEVEL_1;
    public static ForgeConfigSpec.DoubleValue RADIATION_EFFECT_LEVEL_2;
    public static ForgeConfigSpec.DoubleValue RADIATION_EFFECT_LEVEL_3;
    public static ForgeConfigSpec.DoubleValue RADIATION_EFFECT_LEVEL_4;
    public static ForgeConfigSpec.DoubleValue RADIATION_EFFECT_LEVEL_5;

    @Override
    public void registerProperties(@Nonnull ForgeConfigSpec.Builder config, ModConfig.Type type) {
        MIN_RADIATION_RADIUS = config.comment("The minimum radiation radius")
                .defineInRange("minRadiationRadius", 7.0, 3.0, 16.0);
        MAX_RADIATION_RADIUS = config.comment("The maximum radiation radius for a 100/100/100 crystal")
                .defineInRange("maxRadiationRadius", 50.0, 16.0, 128.0);

        MIN_RADIATION_STRENGTH = config.comment("The minimum radiation strength")
                .defineInRange("minRadiationStrength", 3000.0, 500.0, 250000.0);
        MAX_RADIATION_STRENGTH = config.comment("The maximum radiation strength for a 100/100/100 crystal")
                .defineInRange("maxRadiationStrength", 600000.0, 100000.0, 1000000.0f);

        STRENGTH_GROWTH_FACTOR = config.comment("Percentage of the maximum strength the radiation increases every tick")
                .defineInRange("strengthGrowthFactor", 0.002, 0.0001, 0.1);
        STRENGTH_DECREASE_TICK = config.comment("How much the radiation strength decreases every tick")
                .defineInRange("strengthDecreasePerTick", 3.0, 0.1, 50.0);

        RADIATION_DESTRUCTION_EVENT_LEVEL = config.comment("The radiation strength at which point destruction events can happen")
                .defineInRange("radiationDestructionEventLevel", 300000.0, 1000.0, 1000000.0);
        RADIATION_DESTRUCTION_EVENT_CHANCE = config.comment("Every 10 ticks (half a second) this chance is evaluated to see if there should be a destruction event. 1.0 means it will always occur")
                .defineInRange("radiationDestructionEventChance", 0.02, 0.0025, 0.1);

        RADIATION_EFFECT_LEVEL_NONE = config.comment("Below this level no effects occur")
                .defineInRange("radiationEffectLevelNone", 2000.0, 100.0, 10000.0);
        RADIATION_EFFECT_LEVEL_NONE = config.comment("Radiation strength level 0")
                .defineInRange("radiationEffectLevel0", 20000.0, 1000.0, 100000.0);
        RADIATION_EFFECT_LEVEL_NONE = config.comment("Radiation strength level 1")
                .defineInRange("radiationEffectLevel1", 50000.0, 2500.0, 250000.0);
        RADIATION_EFFECT_LEVEL_NONE = config.comment("Radiation strength level 2")
                .defineInRange("radiationEffectLevel2", 100000.0, 5000.0, 500000.0);
        RADIATION_EFFECT_LEVEL_NONE = config.comment("Radiation strength level 3")
                .defineInRange("radiationEffectLevel3", 200000.0, 10000.0, 1000000.0);
        RADIATION_EFFECT_LEVEL_NONE = config.comment("Radiation strength level 4")
                .defineInRange("radiationEffectLevel4", 500000.0, 25000.0, 2500000.0);
        RADIATION_EFFECT_LEVEL_NONE = config.comment("Radiation strength level 5")
                .defineInRange("radiationEffectLevel5", 1000000.0, 50000.0, 5000000.0);
    }

}
