package mcjty.deepresonance.modules.core.fluid;

import mcjty.deepresonance.DeepResonance;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.FluidState;

public class FluidLiquidCrystal extends EmptyFluid {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(DeepResonance.MODID, "block/rclfluid");

    @Override
    public boolean isSource(FluidState state) {
        return true;
    }
}
