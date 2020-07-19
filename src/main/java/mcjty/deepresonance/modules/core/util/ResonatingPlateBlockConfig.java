package mcjty.deepresonance.modules.core.util;

import elec332.core.api.config.IConfigurableElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-7-2020
 */
public class ResonatingPlateBlockConfig implements IConfigurableElement {

    public static ForgeConfigSpec.IntValue RADIATION_STRENGTH;
    public static ForgeConfigSpec.IntValue RADIATION_RADIUS;
    public static ForgeConfigSpec.IntValue RADIATION_TICKS;

    @Override
    public void registerProperties(@Nonnull ForgeConfigSpec.Builder config, ModConfig.Type type) {
        RADIATION_STRENGTH = config.comment("Strength of radiation that a plate block gives when it has a redstone signal. 0 to disable")
                .defineInRange("radiationStrength", 20000, 0, 100000);
        RADIATION_RADIUS = config.comment("Radius of radiation that a plate block gives when it has a redstone signal")
                .defineInRange("radiationRadius", 10, 8, 128);
        RADIATION_TICKS = config.comment("Amount of ticks that the radiation from a plate block lasts")
                .defineInRange("radiationTicks", 100, 20, 72000);
    }

}
