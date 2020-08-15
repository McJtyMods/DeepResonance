package mcjty.deepresonance.modules.radiation.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 12-7-2020
 */
public class RadiationConfiguration {

    public final ForgeConfigSpec.DoubleValue minRadiationRadius;
    public final ForgeConfigSpec.DoubleValue maxRadiationRadius;

    public final ForgeConfigSpec.DoubleValue minRadiationStrength;
    public final ForgeConfigSpec.DoubleValue maxRadiationStrength;

    public final ForgeConfigSpec.DoubleValue strengthGrowthFactor;
    public final ForgeConfigSpec.DoubleValue strengthDecreaseTick;

    public final ForgeConfigSpec.DoubleValue radiationDestructionEventLevel;
    public final ForgeConfigSpec.DoubleValue radiationDestructionEventChance;

    public final ForgeConfigSpec.DoubleValue radiationEffectLevelNone;
    public final ForgeConfigSpec.DoubleValue radiationEffectLevel0;
    public final ForgeConfigSpec.DoubleValue radiationEffectLevel1;
    public final ForgeConfigSpec.DoubleValue radiationEffectLevel2;
    public final ForgeConfigSpec.DoubleValue radiationEffectLevel3;
    public final ForgeConfigSpec.DoubleValue radiationEffectLevel4;
    public final ForgeConfigSpec.DoubleValue radiationEffectLevel5;

    public RadiationConfiguration(@Nonnull ForgeConfigSpec.Builder config) {
        minRadiationRadius = config.comment("The minimum radiation radius")
                .defineInRange("minRadiationRadius", 7.0, 3.0, 16.0);
        maxRadiationRadius = config.comment("The maximum radiation radius for a 100/100/100 crystal")
                .defineInRange("maxRadiationRadius", 50.0, 16.0, 128.0);

        minRadiationStrength = config.comment("The minimum radiation strength")
                .defineInRange("minRadiationStrength", 3000.0, 500.0, 250000.0);
        maxRadiationStrength = config.comment("The maximum radiation strength for a 100/100/100 crystal")
                .defineInRange("maxRadiationStrength", 600000.0, 100000.0, 1000000.0f);

        strengthGrowthFactor = config.comment("Percentage of the maximum strength the radiation increases every tick")
                .defineInRange("strengthGrowthFactor", 0.002, 0.0001, 0.1);
        strengthDecreaseTick = config.comment("How much the radiation strength decreases every tick")
                .defineInRange("strengthDecreasePerTick", 3.0, 0.1, 50.0);

        radiationDestructionEventLevel = config.comment("The radiation strength at which point destruction events can happen")
                .defineInRange("radiationDestructionEventLevel", 300000.0, 1000.0, 1000000.0);
        radiationDestructionEventChance = config.comment("Every 10 ticks (half a second) this chance is evaluated to see if there should be a destruction event. 1.0 means it will always occur")
                .defineInRange("radiationDestructionEventChance", 0.02, 0.0025, 0.1);

        radiationEffectLevelNone = config.comment("Below this level no effects occur")
                .defineInRange("radiationEffectLevelNone", 2000.0, 100.0, 10000.0);
        radiationEffectLevel0 = config.comment("Radiation strength level 0")
                .defineInRange("radiationEffectLevel0", 20000.0, 1000.0, 100000.0);
        radiationEffectLevel1 = config.comment("Radiation strength level 1")
                .defineInRange("radiationEffectLevel1", 50000.0, 2500.0, 250000.0);
        radiationEffectLevel2 = config.comment("Radiation strength level 2")
                .defineInRange("radiationEffectLevel2", 100000.0, 5000.0, 500000.0);
        radiationEffectLevel3 = config.comment("Radiation strength level 3")
                .defineInRange("radiationEffectLevel3", 200000.0, 10000.0, 1000000.0);
        radiationEffectLevel4 = config.comment("Radiation strength level 4")
                .defineInRange("radiationEffectLevel4", 500000.0, 25000.0, 2500000.0);
        radiationEffectLevel5 = config.comment("Radiation strength level 5")
                .defineInRange("radiationEffectLevel5", 1000000.0, 50000.0, 5000000.0);
    }

}
