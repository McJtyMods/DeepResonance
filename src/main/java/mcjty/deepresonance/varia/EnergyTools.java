package mcjty.deepresonance.varia;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyTools {

    public static boolean isEnergyTE(TileEntity te) {
        return te instanceof IEnergyHandler || te instanceof IEnergyReceiver || te instanceof IEnergyProvider;
    }

    public static int extractEnergy(TileEntity tileEntity, ForgeDirection from, int maxExtract) {
        if (tileEntity instanceof IEnergyHandler) {
            return ((IEnergyHandler) tileEntity).extractEnergy(from, maxExtract, false);
        } else if (tileEntity instanceof IEnergyProvider) {
            return ((IEnergyProvider) tileEntity).extractEnergy(from, maxExtract, false);
        } else {
            return 0;
        }
    }

    public static int receiveEnergy(TileEntity tileEntity, ForgeDirection from, int maxReceive) {
        if (tileEntity instanceof IEnergyHandler) {
            return ((IEnergyHandler) tileEntity).receiveEnergy(from, maxReceive, false);
        } else if (tileEntity instanceof IEnergyReceiver) {
            return ((IEnergyReceiver) tileEntity).receiveEnergy(from, maxReceive, false);
        } else {
            return 0;
        }
    }
}
