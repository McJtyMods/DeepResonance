package mcjty.deepresonance.blocks.generator;

import cofh.redstoneflux.api.IEnergyProvider;
import com.google.common.collect.Sets;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.generatornetwork.DRGeneratorNetwork;
import mcjty.lib.compat.RedstoneFluxCompatibility;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.EnergyTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

import java.util.HashSet;
import java.util.Set;

@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux")
public class GeneratorTileEntity extends GenericTileEntity implements IEnergyProvider, ITickable, IEnergyStorage {

    private int networkId = -1;

    public GeneratorTileEntity() {
        super();
    }

    public void addBlockToNetwork() {
        Set<Integer> adjacentGeneratorIds = new HashSet<Integer>();
        for (EnumFacing direction : EnumFacing.VALUES) {
            BlockPos pos = getPos().offset(direction);
            Block block = getWorld().getBlockState(pos).getBlock();
            if (block == GeneratorSetup.generatorBlock) {
                GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) getWorld().getTileEntity(pos);
                adjacentGeneratorIds.add(generatorTileEntity.getNetworkId());
            }
        }

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(getWorld());

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
            Set<BlockPos> done = Sets.newHashSet();
            setBlocksToNetwork(pos, done, id);

            DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(networkId);
            network.setEnergy(energy);
        }

        generatorNetwork.save();
    }

    private void setBlocksToNetwork(BlockPos c, Set<BlockPos> done, int newId) {
        done.add(c);

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(getWorld());
        GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) getWorld().getTileEntity(c);
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

        for (EnumFacing direction : EnumFacing.VALUES) {
            BlockPos newC = c.offset(direction);
            if (!done.contains(newC)) {
                Block block = getWorld().getBlockState(newC).getBlock();
                if (block == GeneratorSetup.generatorBlock) {
                    setBlocksToNetwork(newC, done, newId);
                }
            }
        }
    }

    public void removeBlockFromNetwork() {

        int totalEnergy = 0;
        int totalBlocks = 0;
        if (networkId != -1) {
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(getWorld());
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

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(getWorld());

        // Clear all networks adjacent to this one.
        for (EnumFacing direction : EnumFacing.VALUES) {
            BlockPos newC = getPos().offset(direction);
            Block block = getWorld().getBlockState(newC).getBlock();
            if (block == GeneratorSetup.generatorBlock) {
                Set<BlockPos> done = Sets.newHashSet();
                done.add(pos);
                setBlocksToNetwork(newC, done, -1);
            }
        }

        // Now assign new ones.
        int idToUse = networkId;
        for (EnumFacing direction : EnumFacing.VALUES) {
            BlockPos newC = getPos().offset(direction);
            Block block = getWorld().getBlockState(newC).getBlock();
            if (block == GeneratorSetup.generatorBlock) {
                GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) getWorld().getTileEntity(newC);                if (generatorTileEntity.getNetworkId() == -1) {
                    if (idToUse == -1) {
                        idToUse = generatorNetwork.newChannel();
                    }
                    Set<BlockPos> done = Sets.newHashSet();
                    done.add(getPos());
                    setBlocksToNetwork(newC, done, idToUse);
                    generatorNetwork.getOrCreateNetwork(idToUse).setEnergy(-1);      // Marker so we know what energy to set later.

                    idToUse = -1;
                }
            }
        }

        // Now we need to redistribute the total energy based on the size of the adjacent networks.
        int energy = totalEnergy / totalBlocks;
        int remainder = totalEnergy % totalBlocks;
        for (EnumFacing direction : EnumFacing.VALUES) {
            BlockPos newC = getPos().offset(direction);
            Block block = getWorld().getBlockState(newC).getBlock();
            if (block == GeneratorSetup.generatorBlock) {
                GeneratorTileEntity generatorTileEntity = (GeneratorTileEntity) getWorld().getTileEntity(newC);
                DRGeneratorNetwork.Network network = generatorTileEntity.getNetwork();
                if (network.getEnergy() == -1) {
                    network.setEnergy(energy * network.getGeneratorBlocks() + remainder);
                    remainder = 0;  // Only the first network gets the remainder.
                }
            }
        }
        generatorNetwork.save();
    }

    // Move this tile entity to another network.
    public void setNetworkId(int newId) {
        networkId = newId;
        markDirty();
        IBlockState state = getWorld().getBlockState(pos);
        getWorld().notifyBlockUpdate(pos, state, state, 3);
    }

    public int getNetworkId() {
        return networkId;
    }

    public DRGeneratorNetwork.Network getNetwork() {
        if (networkId == -1) {
            return null;
        }
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(getWorld());
        return generatorNetwork.getOrCreateNetwork(networkId);
    }

    public void activate(boolean active) {
        DRGeneratorNetwork.Network network = getNetwork();
        if (network != null && network.isActive() != active) {
            network.setActive(active);
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(getWorld());
            generatorNetwork.save();
            Set<BlockPos> done = Sets.newHashSet();
            activateBlocks(getPos(), done, active);
        }
    }

    private void activateBlocks(BlockPos c, Set<BlockPos> done, boolean active) {
        done.add(c);

        IBlockState state = getWorld().getBlockState(c);
        if (state.getValue(GeneratorBlock.ENABLED) != active) {
            getWorld().setBlockState(c, state.withProperty(GeneratorBlock.ENABLED, active), 3);
        }

        for (EnumFacing direction : EnumFacing.VALUES) {
            BlockPos newC = c.offset(direction);
            if (!done.contains(newC)) {
                Block block = getWorld().getBlockState(newC).getBlock();
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
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("networkId", networkId);
        return tagCompound;
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        int energyStored = getEnergyStored();

        if (energyStored <= 0) {
            return;
        }

        for (int i = 0 ; i < 6 ; i++) {
            BlockPos pos = getPos().offset(EnumFacing.VALUES[i]);
            TileEntity te = getWorld().getTileEntity(pos);
            EnumFacing opposite = EnumFacing.VALUES[i].getOpposite();
            if (EnergyTools.isEnergyTE(te, opposite)) {
                int rfToGive;
                if (GeneratorConfiguration.rfPerTickGenerator <= energyStored) {
                    rfToGive = GeneratorConfiguration.rfPerTickGenerator;
                } else {
                    rfToGive = energyStored;
                }
                int received;

                if (DeepResonance.redstoneflux && RedstoneFluxCompatibility.isEnergyConnection(te)) {
                    if (RedstoneFluxCompatibility.canConnectEnergy(te, opposite)) {
                        received = (int) EnergyTools.receiveEnergy(te, opposite, rfToGive);
                    } else {
                        received = 0;
                    }
                } else {
                    // Forge unit
                    received = (int) EnergyTools.receiveEnergy(te, opposite, rfToGive);
                }
                energyStored -= extractEnergy(received, false);
                if (energyStored <= 0) {
                    break;
                }
            }
        }
    }


    @Optional.Method(modid = "redstoneflux")
    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return extraEnergyInternal(maxExtract, simulate);
    }

    private int extraEnergyInternal(int maxExtract, boolean simulate) {
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
            DRGeneratorNetwork.getChannels(getWorld()).save();
        }
        return maxExtract;
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getEnergyStored(EnumFacing from) {
        return getEnergyStoredInternal();
    }

    private int getEnergyStoredInternal() {
        if (networkId == -1) {
            return 0;
        }
        DRGeneratorNetwork.Network network = getNetwork();
        return network.getEnergy();
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return getMaxEnergyStoredInternal();
    }

    private int getMaxEnergyStoredInternal() {
        if (networkId == -1) {
            return 0;
        }
        DRGeneratorNetwork.Network network = getNetwork();
        return network.getGeneratorBlocks() * GeneratorConfiguration.rfPerGeneratorBlock;
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    //----------------------------------------------------------
    // Forge  EnergyStorage

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return extraEnergyInternal(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return getEnergyStoredInternal();
    }

    @Override
    public int getMaxEnergyStored() {
        return getMaxEnergyStoredInternal();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) this;
        }
        return super.getCapability(capability, facing);
    }
}
