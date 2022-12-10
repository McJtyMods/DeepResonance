package mcjty.deepresonance.modules.core.fluid;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class FluidLiquidCrystal extends EmptyFluid {

    private static final ResourceLocation texture = new ResourceLocation(DeepResonance.MODID, "block/rclfluid");

    @Override
    @Nonnull
    public FluidType getFluidType() {
        return CoreModule.LIQUID_CRYSTAL_TYPE.get();
    }

    public static class ClientExtensions implements IClientFluidTypeExtensions {
        @Override
        public int getTintColor() {
            return 0xffffdd00;
        }

        @Override
        public ResourceLocation getStillTexture() {
            return texture;
        }

        @Override
        public @Nullable ResourceLocation getOverlayTexture() {
            return texture;
        }
    }
}
