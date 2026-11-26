package mcjty.deepresonance.modules.core.fluid;

import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.modules.core.CoreModule;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.EmptyFluid;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nonnull;

public class FluidLiquidCrystal extends EmptyFluid {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/rclfluid");

    @Override
    @Nonnull
    public FluidType getFluidType() {
        return CoreModule.LIQUID_CRYSTAL_TYPE.get();
    }

}
