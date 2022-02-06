package mcjty.deepresonance.modules.generator.block;

import com.google.common.collect.Sets;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.data.DRGeneratorNetwork;
import mcjty.deepresonance.modules.generator.data.GeneratorBlob;
import mcjty.deepresonance.modules.generator.data.NetworkEnergyStorage;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.multiblock.MultiblockSupport;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.NBTTools;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nonnull;
import java.util.Set;

public class GeneratorPartTileEntity extends TickingTileEntity implements IMultiblockConnector {

    private int blobId = -1;

    @Cap(type = CapType.ENERGY)
    private final NetworkEnergyStorage energyStorage = new NetworkEnergyStorage(this);

    // This is used when the block is broken so the remaining energy can be stored in the drop
    private int preservedEnergy;

    public GeneratorPartTileEntity(BlockPos pos, BlockState state) {
        super(GeneratorModule.TYPE_GENERATOR_PART.get(), pos, state);
    }

    @Override
    public void tickServer() {
        int energyStored = energyStorage.getEnergyStored();
        if (energyStored <= 0) {
            return;
        }

        boolean dirty = false;
        for (Direction facing : OrientationTools.DIRECTION_VALUES) {
            BlockPos pos = getBlockPos().relative(facing);
            BlockEntity te = level.getBlockEntity(pos);
            Direction opposite = facing.getOpposite();
            if (EnergyTools.isEnergyTE(te, opposite)) {
                int rfToGive = Math.min(GeneratorConfig.POWER_PER_TICKOUT.get(), energyStored);   // @todo 1.16 is this the right config?
                int received = (int) EnergyTools.receiveEnergy(te, opposite, rfToGive);
                if (received > 0) {
                    dirty = true;
                    energyStored -= energyStorage.consumeEnergy(received);
                }
                if (energyStored <= 0) {
                    break;
                }
            }
        }
        if (dirty) {
            DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getNetwork(level);
            generatorNetwork.setDirty();
        }
    }

    @Override
    public void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide()) {
            addBlockToNetwork();
            GeneratorBlob network = getBlob();
            if (network != null) {
                CompoundTag tag = stack.getTag();
                if (tag != null) {
                    int energy = NBTTools.getInfoNBT(stack, CompoundTag::getInt, "preserved", 0);
                    getDriver().modify(getMultiblockId(), holder -> holder.getMb().setEnergy(holder.getMb().getEnergy() + energy));
                }
            }
        }
    }

    @Override
    protected void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        if (tagCompound.contains("Info")) {
            preservedEnergy = tagCompound.getCompound("Info").getInt("preserved");
        }
    }

    @Override
    protected void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        getOrCreateInfo(tagCompound).putInt("preserved", preservedEnergy);
    }

    @Override
    public void onReplaced(Level world, BlockPos pos, BlockState state, BlockState newstate) {
        if (!world.isClientSide()) {
            if (newstate.getBlock() != GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                GeneratorBlob network = getBlob();
                if (network != null) {
                    int energy = network.getEnergy() / network.getGeneratorBlocks();
                    network.setEnergy(network.getEnergy() - energy);
                    preservedEnergy = energy;
                } else {
                    preservedEnergy = 0;
                }
                setChanged();
                removeBlockFromNetwork();
            }

            BlockState stateUp = world.getBlockState(pos.above());
            if (stateUp.getBlock() == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                world.sendBlockUpdated(pos.above(), stateUp, stateUp, Block.UPDATE_ALL);
            }
            BlockState stateDown = world.getBlockState(pos.below());
            if (stateDown.getBlock() == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                world.sendBlockUpdated(pos.below(), stateDown, stateDown, Block.UPDATE_ALL);
            }
        }
    }

    public void addBlockToNetwork() {
        GeneratorBlob newMb = new GeneratorBlob()
                .setGeneratorBlocks(1)
                .setActive(false);
        MultiblockSupport.addBlock(level, getBlockPos(), DRGeneratorNetwork.getNetwork(level).getDriver(), newMb);
    }

    public void removeBlockFromNetwork() {
        MultiblockSupport.removeBlock(level, getBlockPos(), DRGeneratorNetwork.getNetwork(level).getDriver());
    }

    // Move this tile entity to another network.
    @Override
    public void setMultiblockId(int newId) {
        if (blobId != newId) {
            blobId = newId;
            setChanged();
        }
    }

    @Override
    public ResourceLocation getId() {
        return DRGeneratorNetwork.GENERATOR_NETWORK_ID;
    }

    @Override
    public int getMultiblockId() {
        return blobId;
    }

    private MultiblockDriver<GeneratorBlob> getDriver() {
        return DRGeneratorNetwork.getNetwork(level).getDriver();
    }

    public GeneratorBlob getBlob() {
        if (blobId == -1) {
            return null;
        }
        DRGeneratorNetwork generatorNetwork = DRGeneratorNetwork.getNetwork(level);
        return generatorNetwork.getOrCreateBlob(blobId);
    }


    public void activate(boolean active) {
        GeneratorBlob network = getBlob();
        if (network != null && network.isActive() != active) {
            getDriver().modify(getMultiblockId(), holder -> holder.getMb().setActive(active));
            Set<BlockPos> done = Sets.newHashSet();
            activateBlocks(getBlockPos(), done, active);
        }
    }

    private void activateBlocks(BlockPos c, Set<BlockPos> done, boolean active) {
        done.add(c);

        BlockState state = level.getBlockState(c);
        if (state.getValue(BlockStateProperties.POWERED) != active) {
            level.setBlock(c, state.setValue(BlockStateProperties.POWERED, active), Block.UPDATE_ALL);
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
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        tagCompound.putInt("networkId", blobId);
        super.saveAdditional(tagCompound);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        blobId = tagCompound.getInt("networkId");
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
}
