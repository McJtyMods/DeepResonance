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
        super(ConfigMachines.Pulser.rfMaximum, ConfigMachines.Pulser.rfPerTick);
    }

    @Override
    public void update() {
        if (!world.isRemote && powerLevel > 0) {
            int rf = getEnergyStored();
            if (rf >= ConfigMachines.Pulser.rfPerPulse) {
                consumeEnergy(ConfigMachines.Pulser.rfPerPulse);
                // Find crystals in area (@todo config area)
                // @todo cache crystals
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                for (int dx = -8 ; dx <= 8 ; dx++) {
                    for (int dy = -8 ; dy <= 8 ; dy++) {
                        for (int dz = -8 ; dz <= 8 ; dz++) {
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

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        return tagCompound;
    }


}
