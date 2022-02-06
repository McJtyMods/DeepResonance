package mcjty.deepresonance.modules.core.fluid;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nonnull;

public class FluidLiquidCrystal extends EmptyFluid {

    private static final ResourceLocation texture = new ResourceLocation(DeepResonance.MODID, "block/rclfluid");

    @Nonnull
    @Override
    protected FluidAttributes createAttributes() {
        return FluidAttributes.builder(texture, texture)
                .translationKey("fluid.deepresonance.liquid_crystal")
                .build(this);
    }

}
