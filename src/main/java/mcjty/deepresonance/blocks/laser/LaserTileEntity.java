package mcjty.deepresonance.blocks.laser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.deepresonance.config.ConfigMachines;
import mcjty.lib.entity.GenericEnergyReceiverTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class LaserTileEntity extends GenericEnergyReceiverTileEntity {

    public LaserTileEntity() {
        super(ConfigMachines.Laser.rfMaximum, ConfigMachines.Laser.rfPerTick);
    }

    @Override
    protected void checkStateServer() {
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        // @todo needs a better box
        return AxisAlignedBB.getBoundingBox(xCoord - 7, yCoord - 1, zCoord - 7, xCoord + 8, yCoord + 2, zCoord + 8);
    }

}
