package mcjty.deepresonance.api.fluid;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 5-8-2015.
 */
public interface IDeepResonanceFluidAcceptor {

    /**
     * @param direction The direction
     * @return Weather the tile can connect to the given side
     */
    public boolean canAcceptFrom(EnumFacing direction);

    /**
     * This gets called right before IDeepResonanceFluidAcceptor#acceptFluid,
     * the network to which the tile is connected will attempt to give you
     * as much RS as returned by this method.
     *
     * @param from from where the fluid will be provided
     * @return the requested amount of RS
     */
    public int getRequestedAmount(EnumFacing from);

    /**
     * This gets called right after IDeepResonanceFluidAcceptor#getRequestedAmount,
     * the provided FluidStack will contain less or as much fluid as the value provided
     * in IDeepResonanceFluidAcceptor#getRequestedAmount.
     *
     * @param fluidStack The provided FluidStack
     * @param from From where the fluid was provided
     * @return The amount of fluid that wasn't used
     */
    public FluidStack acceptFluid(FluidStack fluidStack, EnumFacing from);

}
