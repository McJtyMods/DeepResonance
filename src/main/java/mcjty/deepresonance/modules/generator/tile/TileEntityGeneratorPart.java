package mcjty.deepresonance.modules.generator.tile;

import com.google.common.collect.Sets;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.data.DRGeneratorNetwork;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class TileEntityGeneratorPart extends GenericTileEntity implements ITickableTileEntity {

    private int networkId = -1;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, false, GeneratorModule.generatorConfig.powerStoragePerBlock.get(), 0);
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    public TileEntityGeneratorPart() {
        super(GeneratorModule.TYPE_GENERATOR_PART.get());
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
            checkStateServer();
        }
    }

    private void checkStateServer() {
        int energyStored = energyStorage.getEnergyStored();

        if (energyStored <= 0) {
            return;
        }

        for (Direction facing : OrientationTools.DIRECTION_VALUES) {
            BlockPos pos = getBlockPos().relative(facing);
            TileEntity te = level.getBlockEntity(pos);
            Direction opposite = facing.getOpposite();
            if (EnergyTools.isEnergyTE(te, opposite)) {
                int rfToGive = Math.min(GeneratorModule.generatorConfig.powerPerTickOut.get(), energyStored);   // @todo 1.16 is this the right config?
                int received = (int) EnergyTools.receiveEnergy(te, opposite, rfToGive);
                energyStored -= energyStorage.extractEnergy(received, false);
                if (energyStored <= 0) {
                    break;
                }
            }
        }
    }

    public void addBlockToNetwork() {
        Set<Integer> adjacentGeneratorIds = new HashSet<>();
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos pos = getBlockPos().relative(direction);
            Block block = level.getBlockState(pos).getBlock();
            if (block == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                TileEntityGeneratorPart generatorTileEntity = (TileEntityGeneratorPart) level.getBlockEntity(pos);
                adjacentGeneratorIds.add(generatorTileEntity.getNetworkId());
            }
        }

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(level);

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
            setBlocksToNetwork(getBlockPos(), done, id);

            DRGeneratorNetwork.Network network = generatorNetwork.getOrCreateNetwork(networkId);
            network.setEnergy(energy);
        }

        generatorNetwork.save();
    }

    private void setBlocksToNetwork(BlockPos c, Set<BlockPos> done, int newId) {
        done.add(c);

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(level);
        TileEntityGeneratorPart generatorTileEntity = (TileEntityGeneratorPart) level.getBlockEntity(c);
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

        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = c.relative(direction);
            if (!done.contains(newC)) {
                Block block = level.getBlockState(newC).getBlock();
                if (block == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                    setBlocksToNetwork(newC, done, newId);
                }
            }
        }
    }

    public void removeBlockFromNetwork() {

        int totalEnergy = 0;
        int totalBlocks = 0;
        if (networkId != -1) {
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(level);
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

        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(level);

        // Clear all networks adjacent to this one.
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = getBlockPos().relative(direction);
            Block block = level.getBlockState(newC).getBlock();
            if (block == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                Set<BlockPos> done = Sets.newHashSet();
                done.add(getBlockPos());
                setBlocksToNetwork(newC, done, -1);
            }
        }

        // Now assign new ones.
        int idToUse = networkId;
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = getBlockPos().relative(direction);
            Block block = level.getBlockState(newC).getBlock();
            if (block == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                TileEntityGeneratorPart generatorTileEntity = (TileEntityGeneratorPart) level.getBlockEntity(newC);
                if (generatorTileEntity.getNetworkId() == -1) {
                    if (idToUse == -1) {
                        idToUse = generatorNetwork.newChannel();
                    }
                    Set<BlockPos> done = Sets.newHashSet();
                    done.add(getBlockPos());
                    setBlocksToNetwork(newC, done, idToUse);
                    generatorNetwork.getOrCreateNetwork(idToUse).setEnergy(-1);      // Marker so we know what energy to set later.

                    idToUse = -1;
                }
            }
        }

        // Now we need to redistribute the total energy based on the size of the adjacent networks.
        int energy = totalEnergy / totalBlocks;
        int remainder = totalEnergy % totalBlocks;
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = getBlockPos().relative(direction);
            Block block = level.getBlockState(newC).getBlock();
            if (block == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                TileEntityGeneratorPart generatorTileEntity = (TileEntityGeneratorPart) level.getBlockEntity(newC);
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
        markDirtyClient();
    }

    public int getNetworkId() {
        return networkId;
    }

    public DRGeneratorNetwork.Network getNetwork() {
        if (networkId == -1) {
            return null;
        }
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(level);
        return generatorNetwork.getOrCreateNetwork(networkId);
    }


    public void activate(boolean active) {
        DRGeneratorNetwork.Network network = getNetwork();
        if (network != null && network.isActive() != active) {
            network.setActive(active);
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(level);
            generatorNetwork.save();
            Set<BlockPos> done = Sets.newHashSet();
            activateBlocks(getBlockPos(), done, active);
        }
    }

    private void activateBlocks(BlockPos c, Set<BlockPos> done, boolean active) {
        done.add(c);

        BlockState state = level.getBlockState(c);
        if (state.getValue(BlockStateProperties.POWERED) != active) {
            level.setBlock(c, state.setValue(BlockStateProperties.POWERED, active), Constants.BlockFlags.DEFAULT);
        }

        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = c.relative(direction);
            if (!done.contains(newC)) {
                Block block = level.getBlockState(newC).getBlock();
                if (block == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                    activateBlocks(newC, done, active);
                }
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        tagCompound.putInt("networkId", networkId);
        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        networkId = tagCompound.getInt("networkId");
    }

    // @todo 1.16
//    @Override
//    public void validate() {
//        super.validate();
//        if (Preconditions.checkNotNull(getLevel()).isRemote) {
//            return;
//        }
//        ElecCore.tickHandler.registerCall(() -> {
//            surroundings.clear();
//            for (Direction dir : Direction.values()) {
//                BlockPos pos = getBlockPos().offset(dir);
//                TileEntity tile = WorldHelper.getTileAt(getLevel(), pos);
//                if (tile != null) {
//                    surroundings.put(dir, tile.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()));
//                }
//            }
//        }, getLevel());
//    }

    // @todo 1.16
//    @Override
//    public void onNeighborChange(BlockState myState, BlockPos neighbor) {
//        if (Preconditions.checkNotNull(getLevel()).isRemote) {
//            return;
//        }
//        BlockPos offset = getBlockPos().subtract(neighbor);
//        Direction side = Preconditions.checkNotNull(Direction.byLong(offset.getX(), offset.getY(), offset.getZ()));
//        LazyOptional<IEnergyStorage> cap = surroundings.get(side);
//        if (cap == null || !cap.isPresent()) {
//            cap = null;
//            TileEntity tile = WorldHelper.getTileAt(getLevel(), neighbor);
//            if (tile != null) {
//                cap = tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
//            }
//            surroundings.put(side, cap);
//        }
//    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
