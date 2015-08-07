package mcjty.deepresonance.blocks.collector;

import mcjty.deepresonance.blocks.generator.GeneratorSetup;
import mcjty.deepresonance.blocks.generator.GeneratorTileEntity;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.entity.GenericTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class EnergyCollectorTileEntity extends GenericTileEntity {

    private int networkId = -1;

    public EnergyCollectorTileEntity() {
        super();
    }

    @Override
    protected void checkStateServer() {
        if (worldObj.getBlock(xCoord, yCoord-1, zCoord) == GeneratorSetup.generatorBlock) {
            TileEntity te = worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
            if (te instanceof GeneratorTileEntity) {
                DRGeneratorNetwork.Network network = ((GeneratorTileEntity) te).getNetwork();
                if (network != null) {
                    // @todo temporary code.
                    network.setEnergy(network.getEnergy()+1);
                    DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
                    generatorNetwork.save(worldObj);
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        networkId = tagCompound.getInteger("networkId");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("networkId", networkId);
    }
}
