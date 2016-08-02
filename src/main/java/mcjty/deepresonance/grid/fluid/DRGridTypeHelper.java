package mcjty.deepresonance.grid.fluid;

import elec332.core.grid.basic.IWiringTypeHelper;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidAcceptor;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRGridTypeHelper implements IWiringTypeHelper {

    public static final DRGridTypeHelper instance = new DRGridTypeHelper();
    private DRGridTypeHelper(){
    }

    @Override
    public boolean isReceiver(TileEntity tile) {
        return tile instanceof IDeepResonanceFluidAcceptor;
    }

    @Override
    public boolean isTransmitter(TileEntity tile) {
        return false;
    }

    @Override
    public boolean isSource(TileEntity tile) {
        return tile instanceof IDeepResonanceFluidProvider;
    }

    @Override
    public boolean canReceiverReceiveFrom(TileEntity tile, EnumFacing direction) {
        return ((IDeepResonanceFluidAcceptor)tile).canAcceptFrom(direction);
    }

    @Override
    public boolean canTransmitterConnectTo(TileEntity transmitter, TileEntity otherTransmitter) {
        return false;
    }

    @Override
    public boolean canTransmitterConnectTo(TileEntity transmitter, EnumFacing direction){
        return true;
    }

    @Override
    public boolean canSourceProvideTo(TileEntity tile, EnumFacing direction) {
        return ((IDeepResonanceFluidProvider)tile).canProvideTo(direction);
    }

    @Override
    public boolean isTileValid(TileEntity tile) {
        return tile instanceof IDeepResonanceFluidProvider || tile instanceof IDeepResonanceFluidAcceptor;
    }
}
