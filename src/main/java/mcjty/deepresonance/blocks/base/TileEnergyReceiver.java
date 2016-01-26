package mcjty.deepresonance.blocks.base;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/**
 * Created by Elec332 on 10-8-2015.
 */
public abstract class TileEnergyReceiver extends ElecTileBase implements IEnergyReceiver{

    public TileEnergyReceiver(EnergyStorage energyStorage){
        this.energyStorage = energyStorage;
    }

    protected EnergyStorage energyStorage;

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        energyStorage.readFromNBT(tagCompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        energyStorage.writeToNBT(tagCompound);
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if (canConnectEnergy(from))
            return energyStorage.receiveEnergy(maxReceive, simulate);
        return 0;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return energyStorage.getMaxEnergyStored();
    }

}
