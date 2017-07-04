package mcjty.deepresonance.varia;

import mcjty.deepresonance.DeepResonance;
import mcjty.lib.compat.RedstoneFluxCompatibility;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyTools {

    public static boolean isEnergyTE(TileEntity te) {
        if (te == null) {
            return false;
        }
        return (DeepResonance.redstoneflux && RedstoneFluxCompatibility.isEnergyHandler(te)) || te.hasCapability(CapabilityEnergy.ENERGY, null);
    }

    public static int receiveEnergy(TileEntity tileEntity, EnumFacing from, int maxReceive) {
        if (DeepResonance.redstoneflux && RedstoneFluxCompatibility.isEnergyReceiver(tileEntity)) {
            return RedstoneFluxCompatibility.receiveEnergy(tileEntity, from, maxReceive);
        } else if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, from)) {
            IEnergyStorage capability = tileEntity.getCapability(CapabilityEnergy.ENERGY, from);
            if (capability.canReceive()) {
                return capability.receiveEnergy(maxReceive, false);
            }
        }
        return 0;
    }
}
