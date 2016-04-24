package mcjty.deepresonance.api.fluid;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 5-8-2015.
 */
public interface IDeepResonanceFluidProvider {

    /**
     * @param direction The direction
     * @return Weather the tile can connect to the given side
     */
    public boolean canProvideTo(EnumFacing direction);

    /**
     * Use this method to provide RS to the grid, make sure the FluidStack you return
     * doesn't contain more RS than given in the parameter, otherwise the rest will be lost.
     *
     * @param maxProvided The maximum amount of fluid the FluidStack is allowed to contain
     * @param from From where the fluid gets extracted
     * @return The provided FluidStack
     */
    public FluidStack getProvidedFluid(int maxProvided, EnumFacing from);

}
