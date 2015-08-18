package mcjty.deepresonance.grid.fluid;

import elec332.core.grid.basic.AbstractWiringTypeHelper;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidAcceptor;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidProvider;
import mcjty.deepresonance.blocks.duct.TileBasicFluidDuct;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRGridTypeHelper extends AbstractWiringTypeHelper {

    public static final DRGridTypeHelper instance = new DRGridTypeHelper();
    private DRGridTypeHelper(){
    }

    @Override
    public boolean isReceiver(TileEntity tile) {
        return tile instanceof IDeepResonanceFluidAcceptor;
    }

    @Override
    public boolean isTransmitter(TileEntity tile) {
        return tile instanceof TileBasicFluidDuct;
    }

    @Override
    public boolean isSource(TileEntity tile) {
        return tile instanceof IDeepResonanceFluidProvider;
    }

    @Override
    public boolean canReceiverReceiveFrom(TileEntity tile, ForgeDirection direction) {
        return ((IDeepResonanceFluidAcceptor)tile).canAcceptFrom(direction);
    }

    @Override
    public boolean canTransmitterConnectTo(TileEntity transmitter, TileEntity otherTransmitter) {
        Fluid fluid1 = ((TileBasicFluidDuct) transmitter).lastSeenFluid;
        Fluid fluid2 = ((TileBasicFluidDuct) otherTransmitter).lastSeenFluid;
        return (fluid1 == null || fluid2 == null) || fluid1 == fluid2;
    }

    public boolean canTransmitterConnectTo(TileEntity transmitter, ForgeDirection direction){
        return true;
    }

    @Override
    public boolean canSourceProvideTo(TileEntity tile, ForgeDirection direction) {
        return ((IDeepResonanceFluidProvider)tile).canProvideTo(direction);
    }

    @Override
    public boolean isTileValid(TileEntity tile) {
        return tile instanceof IDeepResonanceFluidProvider || tile instanceof IDeepResonanceFluidAcceptor || tile instanceof TileBasicFluidDuct;
    }
}
