package mcjty.deepresonance.modules.generator.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.generator.data.DRGeneratorNetwork;
import mcjty.deepresonance.modules.generator.data.GeneratorBlob;
import mcjty.deepresonance.modules.generator.data.GeneratorPartData;
import mcjty.deepresonance.modules.generator.data.NetworkEnergyStorage;
import mcjty.deepresonance.modules.generator.util.GeneratorConfig;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.multiblock.MultiblockSupport;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Function;

public class GeneratorPartTileEntity extends TickingTileEntity implements IMultiblockConnector {

    private int blobId = -1;

    private final NetworkEnergyStorage energyStorage = new NetworkEnergyStorage(this);
    @Cap(type = CapType.ENERGY)
    private static final Function<GeneratorPartTileEntity, NetworkEnergyStorage> ENERGY_CAP = tile -> tile.energyStorage;

    public GeneratorPartTileEntity(BlockPos pos, BlockState state) {
        super(GeneratorModule.TYPE_GENERATOR_PART.get(), pos, state);
    }

    @Override
    public void tickServer() {
        GeneratorBlob network = getBlob();
        boolean active = network != null && network.isActive();
        BlockState state = getBlockState();
        if (state.getValue(BlockStateProperties.POWERED) != active) {
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.POWERED, active), Block.UPDATE_CLIENTS);
        }

        int energyStored = network == null ? 0 : network.getEnergy();
        if (energyStored <= 0) {
            return;
        }

        boolean dirty = false;
        for (Direction facing : OrientationTools.DIRECTION_VALUES) {
            BlockPos pos = getBlockPos().relative(facing);
            BlockEntity te = level.getBlockEntity(pos);
            Direction opposite = facing.getOpposite();
            if (EnergyTools.isEnergyTE(te, opposite)) {
                int rfToGive = Math.min(GeneratorConfig.POWER_PER_TICKOUT.get(), energyStored);
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
                GeneratorPartData data = stack.getOrDefault(GeneratorModule.ITEM_GENERATOR_PART_DATA.get(), GeneratorPartData.DEFAULT);
                int energy = data.preservedEnergy();
                if (energy > 0) {
                    getDriver().modify(getMultiblockId(), holder -> holder.getMb().setEnergy(holder.getMb().getEnergy() + energy));
                }
            }
        }
    }

    @Override
    public void onReplaced(Level world, BlockPos pos, BlockState state, BlockState newstate) {
        if (!world.isClientSide()) {
            if (newstate.getBlock() != GeneratorModule.GENERATOR_PART.block().get()) {
                GeneratorBlob network = getBlob();
                if (network != null) {
                    int generatorBlocks = network.getGeneratorBlocks();
                    int energy = generatorBlocks == 0 ? 0 : (network.getEnergy() / generatorBlocks);
                    network.setEnergy(network.getEnergy() - energy);
                    setData(GeneratorModule.GENERATOR_PART_DATA, new GeneratorPartData(energy));
                } else {
                    setData(GeneratorModule.GENERATOR_PART_DATA, new GeneratorPartData(0));
                }
                setChanged();
                if (network != null) {
                    removeBlockFromNetwork();
                }
            }

            BlockState stateUp = world.getBlockState(pos.above());
            if (stateUp.getBlock() == GeneratorModule.GENERATOR_PART.block().get()) {
                world.sendBlockUpdated(pos.above(), stateUp, stateUp, Block.UPDATE_ALL);
            }
            BlockState stateDown = world.getBlockState(pos.below());
            if (stateDown.getBlock() == GeneratorModule.GENERATOR_PART.block().get()) {
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
    public Identifier getId() {
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
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        blobId = tag.getIntOr("networkId", -1);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        tag.putInt("networkId", blobId);
        output.store(MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tag);
    }
}
