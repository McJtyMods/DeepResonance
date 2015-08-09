package mcjty.deepresonance.api.fluid;

import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 9-8-2015.
 */
@Deprecated //Do not use
public interface IDeepResonanceFluidDuct {

    /**
     * Use this to fill a pipe network with a FluidStack
     *
     * @param toFill The fluidStack you want to fill the grid with
     * @return The unused fluid, can be null if the entire FluidStack was used to fill the pipes
     */
    public FluidStack fillNetwork(FluidStack toFill);

}
