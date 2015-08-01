package mcjty.deepresonance.blocks.generator;

import mcjty.entity.GenericEnergyProviderTileEntity;
import net.minecraft.nbt.NBTTagCompound;

public class GeneratorTileEntity extends GenericEnergyProviderTileEntity {

    public GeneratorTileEntity() {
        super(5000000, 20000);
    }

    @Override
    protected void checkStateServer() {
        super.checkStateServer();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
    }

}
