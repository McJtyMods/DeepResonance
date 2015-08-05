package mcjty.deepresonance.grid.fluid;

import elec332.core.grid.basic.AbstractWiringTypeHelper;
import mcjty.deepresonance.blocks.cable.TileBasicFluidDuct;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRWiringTypeHelper extends AbstractWiringTypeHelper {
//TODO: everything
    public static final DRWiringTypeHelper instance = new DRWiringTypeHelper();
    private DRWiringTypeHelper(){
    }

    @Override
    public boolean isReceiver(TileEntity tile) {
        return false;
    }

    @Override
    public boolean isTransmitter(TileEntity tile) {
        return tile instanceof TileBasicFluidDuct;
    }

    @Override
    public boolean isSource(TileEntity tile) {
        return false;
    }

    @Override
    public boolean canReceiverReceiveFrom(TileEntity tile, ForgeDirection direction) {
        return false;
    }

    @Override
    public boolean canTransmitterConnectTo(TileEntity transmitter, TileEntity otherTransmitter) {
        return true;
    }

    @Override
    public boolean canSourceProvideTo(TileEntity tile, ForgeDirection direction) {
        return false;
    }

    @Override
    public boolean isTileValid(TileEntity tile) {
        return true;
    }
}
