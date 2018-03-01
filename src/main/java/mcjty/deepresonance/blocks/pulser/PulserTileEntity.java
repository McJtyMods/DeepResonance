package mcjty.deepresonance.blocks.pulser;

import mcjty.deepresonance.blocks.crystals.ResonatingCrystalTileEntity;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.lib.entity.GenericEnergyReceiverTileEntity;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class PulserTileEntity extends GenericEnergyReceiverTileEntity implements ITickable {

    public PulserTileEntity() {
        super(ConfigMachines.pulser.rfMaximum, ConfigMachines.pulser.rfPerTick);
    }

    private int pulsePower = 0;         // Collected pulsePower

    @Override
    public void update() {
        if (!world.isRemote) {
            if (powerLevel > 0) {
                int rf = getEnergyStored();
                int rfToTransfer = (ConfigMachines.pulser.rfPerPulse / 15) * powerLevel;
                if (rf >= rfToTransfer) {
                    consumeEnergy(rfToTransfer);
                    pulsePower += rfToTransfer;
                    markDirtyQuick();
                }
            }

            if (pulsePower >= ConfigMachines.pulser.rfPerPulse) {
                pulsePower -= ConfigMachines.pulser.rfPerPulse;
                markDirtyQuick();
                // Find crystals in area
                // @todo cache crystals
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                int range = 6;      // @todo config?
                for (int dx = -range; dx <= range; dx++) {
                    for (int dy = -range; dy <= range; dy++) {
                        for (int dz = -range; dz <= range; dz++) {
                            BlockPos p = new BlockPos(x + dx, y + dy, z + dz);
                            TileEntity te = world.getTileEntity(p);
                            if (te instanceof ResonatingCrystalTileEntity) {
                                ResonatingCrystalTileEntity crystal = (ResonatingCrystalTileEntity) te;
                                crystal.pulse();
                            }
                        }
                    }
                }
            }
        }
    }

    public int getPulsePower() {
        return pulsePower;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        pulsePower = tagCompound.getInteger("pulsePower");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setInteger("pulsePower", pulsePower);
    }
}
