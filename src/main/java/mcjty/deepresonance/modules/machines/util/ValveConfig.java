package mcjty.deepresonance.modules.machines.util;

import elec332.core.api.config.IConfigurableElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 25-7-2020
 */
public class ValveConfig implements IConfigurableElement {

    public ForgeConfigSpec.IntValue ticksPerOperation;
    public ForgeConfigSpec.IntValue rclPerOperation;

    @Override
    public void registerProperties(@Nonnull ForgeConfigSpec.Builder builder, ModConfig.Type type) {
        this.ticksPerOperation = builder.comment("The amount of ticks between a transfer operation")
                .defineInRange("ticksPerOperation", 5, 1, 300);
        this.rclPerOperation = builder.comment("The amount of RCL to transfer in one operation")
                .defineInRange("rclPerOperation", 1, 100, 10000);
    }

}
