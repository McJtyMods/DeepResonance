package mcjty.deepresonance.blocks.lens;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.nbt.NBTTagCompound;

public class LensTileEntity extends GenericTileEntity {

    public LensTileEntity() {
        super();
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

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
    }
}
