package mcjty.deepresonance.varia;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class EnergyTools {

    public static boolean isEnergyTE(TileEntity te) {
        return te instanceof IEnergyHandler;
    }

    public static int extractEnergy(TileEntity tileEntity, EnumFacing from, int maxExtract) {
        if (tileEntity instanceof IEnergyProvider) {
            return ((IEnergyProvider) tileEntity).extractEnergy(from, maxExtract, false);
        } else {
            return 0;
        }
    }

    public static int receiveEnergy(TileEntity tileEntity, EnumFacing from, int maxReceive) {
        if (tileEntity instanceof IEnergyReceiver) {
            return ((IEnergyReceiver) tileEntity).receiveEnergy(from, maxReceive, false);
        } else {
            return 0;
        }
    }
}
