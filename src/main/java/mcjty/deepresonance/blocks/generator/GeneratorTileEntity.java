package mcjty.deepresonance.blocks.generator;

import cofh.api.energy.IEnergyProvider;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.entity.GenericEnergyProviderTileEntity;
import mcjty.entity.GenericTileEntity;
import mcjty.varia.Coordinate;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashSet;
import java.util.Set;

public class GeneratorTileEntity extends GenericTileEntity implements IEnergyProvider {

    private int networkId = -1;

    public GeneratorTileEntity() {
        super();
    }

    public void addBlockToNetwork() {
        Set<Integer> adjacentGeneratorIds = new HashSet<Integer>();
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
            int id = adjacentGeneratorIds.iterator().next();
            Set<Coordinate> done = new HashSet<Coordinate>();
            setBlocksToNetwork(new Coordinate(xCoord, yCoord, zCoord), done, id);
        }

        generatorNetwork.markDirty();
        generatorNetwork.save(worldObj);
    }

    private void setBlocksToNetwork(Coordinate c, Set<Coordinate> done, int newId) {
        done.add(c);

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
        GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) worldObj.getTileEntity(c.getX(), c.getY(), c.getZ());
        int oldNetworkId = generatorTileEntity.getNetworkId();
        if (oldNetworkId != newId) {
            if (oldNetworkId != -1) {
                generatorNetwork.getOrCreateNetwork(oldNetworkId).decRefCount();
            }
            generatorTileEntity.setNetworkId(newId);
            if (newId != -1) {
                generatorNetwork.getOrCreateNetwork(newId).incRefCount();
            }
        }

        for (ForgeDirection direction : ForgeDirection.values()) {
            if (!direction.equals(ForgeDirection.UNKNOWN)) {
                Coordinate newC = c.addDirection(direction);
                if (!done.contains(newC)) {
                    Block block = worldObj.getBlock(newC.getX(), newC.getY(), newC.getZ());
                    if (block == GeneratorSetup.generatorBlock) {
                        setBlocksToNetwork(newC, done, newId);
                    }
                }
            }
        }
    }

    public void removeBlockFromNetwork() {
        Coordinate thisCoord = new Coordinate(xCoord, yCoord, zCoord);

        if (networkId != -1) {
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
            generatorNetwork.getOrCreateNetwork(networkId).decRefCount();
            setNetworkId(-1);
        }

        // Clear all networks adjacent to this one.
        for (ForgeDirection direction : ForgeDirection.values()) {
            if (!direction.equals(ForgeDirection.UNKNOWN)) {
                Coordinate newC = thisCoord.addDirection(direction);
                Block block = worldObj.getBlock(newC.getX(), newC.getY(), newC.getZ());
                if (block == GeneratorSetup.generatorBlock) {
                    Set<Coordinate> done = new HashSet<Coordinate>();
                    done.add(thisCoord);
                    setBlocksToNetwork(newC, done, -1);
                }
            }
        }

        // Now assign new ones.
        int idToUse = networkId;
        for (ForgeDirection direction : ForgeDirection.values()) {
            if (!direction.equals(ForgeDirection.UNKNOWN)) {
                Coordinate newC = thisCoord.addDirection(direction);
                Block block = worldObj.getBlock(newC.getX(), newC.getY(), newC.getZ());
                if (block == GeneratorSetup.generatorBlock) {
                    GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) worldObj.getTileEntity(newC.getX(), newC.getY(), newC.getZ());
                    if (generatorTileEntity.getNetworkId() == -1) {
                        if (idToUse == -1) {
                            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
                            idToUse = generatorNetwork.newChannel();
                        }
                        Set<Coordinate> done = new HashSet<Coordinate>();
                        done.add(thisCoord);
                        setBlocksToNetwork(newC, done, idToUse);
                        idToUse = -1;
                    }
                }
            }
        }

    }

    // Move this tile entity to another network.
    public void setNetworkId(int newId) {
        networkId = newId;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public int getNetworkId() {
        return networkId;
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

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }
}
