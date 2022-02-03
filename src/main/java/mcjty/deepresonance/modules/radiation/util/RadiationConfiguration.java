package mcjty.deepresonance.modules.radiation.util;

import mcjty.deepresonance.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class RadiationConfiguration {

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

    public static ForgeConfigSpec.DoubleValue MAX_RADIATION_METER;

    public static ForgeConfigSpec.IntValue RADIATION_OVERLAY_COLOR;
    public static ForgeConfigSpec.IntValue RADIATION_OVERLAY_COLOR_NORADIATION;
    public static ForgeConfigSpec.IntValue RADIATION_OVERLAY_X;
    public static ForgeConfigSpec.IntValue RADIATION_OVERLAY_Y;

    public static ForgeConfigSpec.DoubleValue RADIATION_SHIELD_OBSIDIAN_FACTOR;
    public static ForgeConfigSpec.DoubleValue RADIATION_SHIELD_DENSE_OBSIDIAN_FACTOR;
    public static ForgeConfigSpec.DoubleValue RADIATION_SHIELD_DENSE_GLASS_FACTOR;
    public static ForgeConfigSpec.DoubleValue RADIATION_SHIELD_LEAD_FACTOR;

    public static void init() {
        MIN_RADIATION_RADIUS = Config.SERVER_BUILDER.comment("The minimum radiation radius")
                .defineInRange("minRadiationRadius", 7.0, 3.0, 16.0);
        MAX_RADIATION_RADIUS = Config.SERVER_BUILDER.comment("The maximum radiation radius for a 100/100/100 crystal")
                .defineInRange("maxRadiationRadius", 50.0, 16.0, 128.0);

        MIN_RADIATION_STRENGTH = Config.SERVER_BUILDER.comment("The minimum radiation strength")
                .defineInRange("minRadiationStrength", 3000.0, 500.0, 250000.0);
        MAX_RADIATION_STRENGTH = Config.SERVER_BUILDER.comment("The maximum radiation strength for a 100/100/100 crystal")
                .defineInRange("maxRadiationStrength", 600000.0, 100000.0, 1000000.0f);

        STRENGTH_GROWTH_FACTOR = Config.SERVER_BUILDER.comment("Percentage of the maximum strength the radiation increases every tick")
                .defineInRange("strengthGrowthFactor", 0.002, 0.0001, 0.1);
        STRENGTH_DECREASE_TICK = Config.SERVER_BUILDER.comment("How much the radiation strength decreases every tick")
                .defineInRange("strengthDecreasePerTick", 3.0, 0.1, 50.0);

        RADIATION_DESTRUCTION_EVENT_LEVEL = Config.SERVER_BUILDER.comment("The radiation strength at which point destruction events can happen")
                .defineInRange("radiationDestructionEventLevel", 300000.0, 1000.0, 1000000.0);
        RADIATION_DESTRUCTION_EVENT_CHANCE = Config.SERVER_BUILDER.comment("Every 10 ticks (half a second) this chance is evaluated to see if there should be a destruction event. 1.0 means it will always occur")
                .defineInRange("radiationDestructionEventChance", 0.02, 0.0025, 0.1);

        RADIATION_EFFECT_LEVEL_NONE = Config.SERVER_BUILDER.comment("Below this level no effects occur")
                .defineInRange("radiationEffectLevelNone", 2000.0, 100.0, 10000.0);
        RADIATION_EFFECT_LEVEL_0 = Config.SERVER_BUILDER.comment("Radiation strength level 0")
                .defineInRange("radiationEffectLevel0", 20000.0, 1000.0, 100000.0);
        RADIATION_EFFECT_LEVEL_1 = Config.SERVER_BUILDER.comment("Radiation strength level 1")
                .defineInRange("radiationEffectLevel1", 50000.0, 2500.0, 250000.0);
        RADIATION_EFFECT_LEVEL_2 = Config.SERVER_BUILDER.comment("Radiation strength level 2")
                .defineInRange("radiationEffectLevel2", 100000.0, 5000.0, 500000.0);
        RADIATION_EFFECT_LEVEL_3 = Config.SERVER_BUILDER.comment("Radiation strength level 3")
                .defineInRange("radiationEffectLevel3", 200000.0, 10000.0, 1000000.0);
        RADIATION_EFFECT_LEVEL_4 = Config.SERVER_BUILDER.comment("Radiation strength level 4")
                .defineInRange("radiationEffectLevel4", 500000.0, 25000.0, 2500000.0);
        RADIATION_EFFECT_LEVEL_5 = Config.SERVER_BUILDER.comment("Radiation strength level 5")
                .defineInRange("radiationEffectLevel5", 1000000.0, 50000.0, 5000000.0);

        MAX_RADIATION_METER = Config.SERVER_BUILDER.comment("The maximum that a radiation meter can measure").defineInRange("maxRadiationMeter", 200000f, 0, Double.MAX_VALUE);

        RADIATION_SHIELD_OBSIDIAN_FACTOR = Config.SERVER_BUILDER.comment("How much obsidian blocks radiation (0.0 is total block, 1.0 is not block at all)")
                .defineInRange("radiationShieldObsidianFactor", 0.2f, 0.0f, Float.MAX_VALUE);
        RADIATION_SHIELD_DENSE_OBSIDIAN_FACTOR = Config.SERVER_BUILDER.comment("How much dense obsidian blocks radiation (0.0 is total block, 1.0 is not block at all)")
                .defineInRange("radiationShieldDenseObsidianFactor", 0.05f, 0.0f, Float.MAX_VALUE);
        RADIATION_SHIELD_DENSE_GLASS_FACTOR = Config.SERVER_BUILDER.comment("How much dense glass blocks radiation (0.0 is total block, 1.0 is not block at all)")
                .defineInRange("radiationShieldDenseGlassFactor", 0.1f, 0.0f, Float.MAX_VALUE);
        RADIATION_SHIELD_LEAD_FACTOR = Config.SERVER_BUILDER.comment("How much dense lead blocks radiation (0.0 is total block, 1.0 is not block at all)")
                .defineInRange("radiationShieldLeadFactor", 0.1f, 0.0f, Float.MAX_VALUE);


        RADIATION_OVERLAY_COLOR = Config.CLIENT_BUILDER.comment("The color for the radiation overlay text in case the radiation monitor is in the players hand")
                .defineInRange("radiationOverlayColor", 0xffff0000, Integer.MIN_VALUE, Integer.MAX_VALUE);
        RADIATION_OVERLAY_COLOR_NORADIATION = Config.CLIENT_BUILDER.comment("The color for the radiation overlay text in case the radiation monitor is in the players hand (in case there is no radiation)")
                .defineInRange("radiationOverlayColorNoRadiation", 0xff00ff00, Integer.MIN_VALUE, Integer.MAX_VALUE);
        RADIATION_OVERLAY_X = Config.CLIENT_BUILDER.comment("The X coordinate (with 0 being left) for the radiation overlay text. Use -1 to disable")
                .defineInRange("radiationOverlayX", 10, -1, Integer.MAX_VALUE);
        RADIATION_OVERLAY_Y = Config.CLIENT_BUILDER.comment("The Y coordinate (with 0 being top) for the radiation overlay text. Use -1 to disable")
                .defineInRange("radiationOverlayY", 10, -1, Integer.MAX_VALUE);
    }

}
