package mcjty.deepresonance.blocks.lens;

import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.nbt.NBTTagCompound;

public class LensTileEntity extends GenericTileEntity {

    public LensTileEntity() {
        super();
    }

    @Override
    public boolean canUpdate() {
        return false;
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
        return pass == 0;
    }
}
