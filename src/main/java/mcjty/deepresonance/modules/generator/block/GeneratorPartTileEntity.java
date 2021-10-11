package mcjty.deepresonance.modules.generator.block;

import com.google.common.collect.Sets;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.data.DRGeneratorNetwork;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.multiblock.MultiblockSupport;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class GeneratorPartTileEntity extends GenericTileEntity implements ITickableTileEntity, IMultiblockConnector {

    private int networkId = -1;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, false, GeneratorModule.generatorConfig.powerStoragePerBlock.get(), 0);
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    public GeneratorPartTileEntity() {
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

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide()) {
            addBlockToNetwork();
            DRGeneratorNetwork.Network network = getNetwork();
            if (network != null) {
                CompoundNBT tag = stack.getTag();
                if (tag != null) {
                    getDriver().modify(getMultiblockId(), holder -> {
                        holder.getMb().setEnergy(holder.getMb().getEnergy() + tag.getInt("energy"));
                    });
                }
            }
        }
    }

    @Override
    public void onReplaced(World world, BlockPos pos, BlockState state, BlockState newstate) {
        if (!world.isClientSide()) {
            if (newstate.getBlock() != GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                DRGeneratorNetwork.Network network = getNetwork();
                if (network != null) {
                    int energy = network.getEnergy() / network.getGeneratorBlocks();
                    network.setEnergy(network.getEnergy() - energy);
                }
                removeBlockFromNetwork();
            }
        }
    }

    public void addBlockToNetwork() {
        DRGeneratorNetwork.Network newMb = DRGeneratorNetwork.Network.builder()
                .generatorBlocks(1)
                .active(false)
                .build();
        MultiblockSupport.addBlock(level, getBlockPos(), DRGeneratorNetwork.getChannels(level).getDriver(), newMb);
    }

    public void removeBlockFromNetwork() {
        MultiblockSupport.removeBlock(level, getBlockPos(), DRGeneratorNetwork.getChannels(level).getDriver());
    }

    // Move this tile entity to another network.
    @Override
    public void setMultiblockId(int newId) {
        if (networkId != newId) {
            networkId = newId;
            markDirtyClient();
        }
    }

    @Override
    public int getMultiblockId() {
        return networkId;
    }

    private MultiblockDriver<DRGeneratorNetwork.Network> getDriver() {
        return DRGeneratorNetwork.getChannels(level).getDriver();
    }

    public DRGeneratorNetwork.Network getNetwork() {
        if (networkId == -1) {
            return null;
        }
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getChannels(level);
        return generatorNetwork.getNetwork(networkId);
    }


    public void activate(boolean active) {
        DRGeneratorNetwork.Network network = getNetwork();
        if (network != null && network.isActive() != active) {
            getDriver().modify(getMultiblockId(), holder -> {
                holder.getMb().setActive(active);
            });
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
