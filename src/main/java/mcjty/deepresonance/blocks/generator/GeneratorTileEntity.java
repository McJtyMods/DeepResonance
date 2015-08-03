package mcjty.deepresonance.blocks.generator;

import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.entity.GenericEnergyProviderTileEntity;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashSet;
import java.util.Set;

public class GeneratorTileEntity extends GenericEnergyProviderTileEntity {

    private int networkId = -1;

    public GeneratorTileEntity() {
        super(5000000, 20000);
    }

    public void addBlockToNetwork() {
        Set<Integer> adjacentGeneratorIds = new HashSet<>();
        for (ForgeDirection direction : ForgeDirection.values()) {
            if (!direction.equals(ForgeDirection.UNKNOWN)) {
                int ox = xCoord + direction.offsetX;
                int oy = yCoord + direction.offsetY;
                int oz = zCoord + direction.offsetZ;
                Block block = worldObj.getBlock(ox, oy, oz);
                if (block == GeneratorSetup.generatorBlock) {
                    GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) worldObj.getTileEntity(ox, oy, oz);
                    adjacentGeneratorIds.add(generatorTileEntity.getNetworkId());
                }
            }
        }

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);

        if (adjacentGeneratorIds.isEmpty()) {
            // New network.
            networkId = generatorNetwork.newChannel();
            DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(networkId);
            network.setRefcount(1);
        } else if (adjacentGeneratorIds.size() == 1) {
            // Only one network adjacent. So we can simply join this new block to that network.
            networkId = adjacentGeneratorIds.iterator().next();
            DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(networkId);
            network.incRefCount();
        } else {
            // We need to merge networks. The first network will be the master.
            networkId = adjacentGeneratorIds.iterator().next();
            //@@todoo
        }

        generatorNetwork.markDirty();
        generatorNetwork.save(worldObj);
    }

    // Move this tile entity to another network.
    public void moveToNetwork(int newId) {
        networkId = newId;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public int getNetworkId() {
        return networkId;
    }

    public void removeBlockFromNetwork() {

    }

    @Override
    protected void checkStateServer() {
        super.checkStateServer();
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
