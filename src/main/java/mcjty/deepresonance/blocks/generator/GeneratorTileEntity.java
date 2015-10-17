package mcjty.deepresonance.blocks.generator;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.deepresonance.varia.EnergyTools;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.Coordinate;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            int ox = xCoord + direction.offsetX;
            int oy = yCoord + direction.offsetY;
            int oz = zCoord + direction.offsetZ;
            Block block = worldObj.getBlock(ox, oy, oz);
            if (block == GeneratorSetup.generatorBlock) {
                GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) worldObj.getTileEntity(ox, oy, oz);
                adjacentGeneratorIds.add(generatorTileEntity.getNetworkId());
            }
        }

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);

        if (adjacentGeneratorIds.isEmpty()) {
            // New network.
            networkId = generatorNetwork.newChannel();
            DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(networkId);
            network.setGeneratorBlocks(1);
        } else if (adjacentGeneratorIds.size() == 1) {
            // Only one network adjacent. So we can simply join this new block to that network.
            networkId = adjacentGeneratorIds.iterator().next();
            DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(networkId);
            network.setActive(false);       // Deactivate to make sure it properly restarts
            network.incGeneratorBlocks();
        } else {
            // We need to merge networks. The first network will be the master. First we
            // calculate the total amount of energy in all the networks that are merged this way.
            int energy = 0;
            for (Integer netId : adjacentGeneratorIds) {
                DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(netId);
                network.setActive(false);       // Deactivate to make sure it properly restarts
                energy += network.getEnergy();
            }

            int id = adjacentGeneratorIds.iterator().next();
            Set<Coordinate> done = new HashSet<Coordinate>();
            setBlocksToNetwork(new Coordinate(xCoord, yCoord, zCoord), done, id);

            DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(networkId);
            network.setEnergy(energy);
        }

        generatorNetwork.save(worldObj);
    }

    private void setBlocksToNetwork(Coordinate c, Set<Coordinate> done, int newId) {
        done.add(c);

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
        GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) worldObj.getTileEntity(c.getX(), c.getY(), c.getZ());
        int oldNetworkId = generatorTileEntity.getNetworkId();
        if (oldNetworkId != newId) {
            if (oldNetworkId != -1) {
                generatorNetwork.getOrCreateNetwork(oldNetworkId).decGeneratorBlocks();
            }
            generatorTileEntity.setNetworkId(newId);
            if (newId != -1) {
                generatorNetwork.getOrCreateNetwork(newId).incGeneratorBlocks();
            }
        }

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            Coordinate newC = c.addDirection(direction);
            if (!done.contains(newC)) {
                Block block = worldObj.getBlock(newC.getX(), newC.getY(), newC.getZ());
                if (block == GeneratorSetup.generatorBlock) {
                    setBlocksToNetwork(newC, done, newId);
                }
            }
        }
    }

    public void removeBlockFromNetwork() {
        Coordinate thisCoord = new Coordinate(xCoord, yCoord, zCoord);

        int totalEnergy = 0;
        int totalBlocks = 0;
        if (networkId != -1) {
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
            DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(networkId);
            network.setActive(false);       // Deactivate to make sure it properly restarts
            network.decGeneratorBlocks();
            totalEnergy = network.getEnergy();
            totalBlocks = network.getGeneratorBlocks();
            setNetworkId(-1);

        }
        // Safety:
        if (totalBlocks < 1) {
            totalBlocks = 1;
        }

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);

        // Clear all networks adjacent to this one.
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            Coordinate newC = thisCoord.addDirection(direction);
            Block block = worldObj.getBlock(newC.getX(), newC.getY(), newC.getZ());
            if (block == GeneratorSetup.generatorBlock) {
                Set<Coordinate> done = new HashSet<Coordinate>();
                done.add(thisCoord);
                setBlocksToNetwork(newC, done, -1);
            }
        }

        // Now assign new ones.
        int idToUse = networkId;
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            Coordinate newC = thisCoord.addDirection(direction);
            Block block = worldObj.getBlock(newC.getX(), newC.getY(), newC.getZ());
            if (block == GeneratorSetup.generatorBlock) {
                GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) worldObj.getTileEntity(newC.getX(), newC.getY(), newC.getZ());
                if (generatorTileEntity.getNetworkId() == -1) {
                    if (idToUse == -1) {
                        idToUse = generatorNetwork.newChannel();
                    }
                    Set<Coordinate> done = new HashSet<Coordinate>();
                    done.add(thisCoord);
                    setBlocksToNetwork(newC, done, idToUse);
                    generatorNetwork.getOrCreateNetwork(idToUse).setEnergy(-1);      // Marker so we know what energy to set later.

                    idToUse = -1;
                }
            }
        }

        // Now we need to redistribute the total energy based on the size of the adjacent networks.
        int energy = totalEnergy / totalBlocks;
        int remainder = totalEnergy % totalBlocks;
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            Coordinate newC = thisCoord.addDirection(direction);
            Block block = worldObj.getBlock(newC.getX(), newC.getY(), newC.getZ());
            if (block == GeneratorSetup.generatorBlock) {
                GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) worldObj.getTileEntity(newC.getX(), newC.getY(), newC.getZ());
                DRGeneratorNetwork.Network network = generatorTileEntity.getNetwork();
                if (network.getEnergy() == -1) {
                    network.setEnergy(energy * network.getGeneratorBlocks() + remainder);
                    remainder = 0;  // Only the first network gets the remainder.
                }
            }
        }
        generatorNetwork.save(worldObj);
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

    public DRGeneratorNetwork.Network getNetwork() {
        if (networkId == -1) {
            return null;
        }
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
        return generatorNetwork.getOrCreateNetwork(networkId);
    }

    public void activate(boolean active) {
        DRGeneratorNetwork.Network network = getNetwork();
        if (network != null && network.isActive() != active) {
            network.setActive(active);
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(worldObj);
            generatorNetwork.save(worldObj);
            Set<Coordinate> done = new HashSet<Coordinate> ();
            activateBlocks(new Coordinate(xCoord, yCoord, zCoord), done, active);
        }
    }

    private void activateBlocks(Coordinate c, Set<Coordinate> done, boolean active) {
        done.add(c);

        int meta = worldObj.getBlockMetadata(c.getX(), c.getY(), c.getZ());

        if (((meta & GeneratorBlock.META_ON) == 0) == active) {
            if (active) {
                worldObj.setBlockMetadataWithNotify(c.getX(), c.getY(), c.getZ(), meta | GeneratorBlock.META_ON, 3);
            } else {
                worldObj.setBlockMetadataWithNotify(c.getX(), c.getY(), c.getZ(), meta & ~GeneratorBlock.META_ON, 3);
            }
        }

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            Coordinate newC = c.addDirection(direction);
            if (!done.contains(newC)) {
                Block block = worldObj.getBlock(newC.getX(), newC.getY(), newC.getZ());
                if (block == GeneratorSetup.generatorBlock) {
                    activateBlocks(newC, done, active);
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

    @Override
    protected void checkStateServer() {
        int energyStored = getEnergyStored(ForgeDirection.DOWN);

        if (energyStored <= 0) {
            return;
        }

        for (int i = 0 ; i < 6 ; i++) {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            int x = xCoord + dir.offsetX;
            int y = yCoord + dir.offsetY;
            int z = zCoord + dir.offsetZ;
            TileEntity te = worldObj.getTileEntity(x, y, z);
            if (EnergyTools.isEnergyTE(te)) {
                IEnergyConnection connection = (IEnergyConnection) te;
                ForgeDirection opposite = dir.getOpposite();
                if (connection.canConnectEnergy(opposite)) {
                    int rfToGive;
                    if (GeneratorConfiguration.rfPerTickGenerator <= energyStored) {
                        rfToGive = GeneratorConfiguration.rfPerTickGenerator;
                    } else {
                        rfToGive = energyStored;
                    }

                    int received = EnergyTools.receiveEnergy(te, opposite, rfToGive);
                    energyStored -= extractEnergy(ForgeDirection.DOWN, received, false);
                    if (energyStored <= 0) {
                        break;
                    }
                }
            }
        }
    }


    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        if (networkId == -1) {
            return 0;
        }
        DRGeneratorNetwork.Network network = getNetwork();
        int energy = network.getEnergy();
        if (maxExtract > energy) {
            maxExtract = energy;
        }
        if (maxExtract > GeneratorConfiguration.rfPerTickGenerator) {
            maxExtract = GeneratorConfiguration.rfPerTickGenerator;
        }
        if (!simulate) {
            network.setEnergy(energy - maxExtract);
            DRGeneratorNetwork.getChannels(worldObj).save(worldObj);
        }
        return maxExtract;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        if (networkId == -1) {
            return 0;
        }
        DRGeneratorNetwork.Network network = getNetwork();
        return network.getEnergy();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        if (networkId == -1) {
            return 0;
        }
        DRGeneratorNetwork.Network network = getNetwork();
        return network.getGeneratorBlocks() * GeneratorConfiguration.rfPerGeneratorBlock;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }
}
