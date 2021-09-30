package mcjty.deepresonance.modules.core.fluid;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.util.TranslationHelper;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nonnull;

public class FluidLiquidCrystal extends EmptyFluid {

    private static final ResourceLocation texture = new ResourceLocation(DeepResonance.MODID, "block/rclfluid");

    @Nonnull
    @Override
    protected FluidAttributes createAttributes() {
        return FluidAttributes.builder(texture, texture)
                .translationKey(TranslationHelper.getFluidKey("liquid_crystal"))
                .build(this);
    }

}
