package mcjty.deepresonance.modules.tank.blocks;

import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.data.DRTankHandler;
import mcjty.deepresonance.modules.tank.data.DRTankNetwork;
import mcjty.deepresonance.modules.tank.data.TankBlob;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.multiblock.MultiblockSupport;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Collections;

public class TankTileEntity extends GenericTileEntity implements IMultiblockConnector {

    private int blobId = -1;

    // Client only
    private Fluid clientRenderFluid;
    private float renderHeight; //Value from 0.0f to 1.0f

    // Only used when the tank is broken and needs to be put back later
    private FluidStack preservedFluid = FluidStack.EMPTY;

    @Cap(type = CapType.FLUIDS)
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(this::createFluidHandler);

    public TankTileEntity() {
        super(TankModule.TYPE_TANK.get());
    }

    public void setClientData(float newHeight, Fluid render) {
        boolean dirty = false;
        if (newHeight >= 0 && renderHeight != newHeight) {
            renderHeight = newHeight;
            dirty = true;
        }
        if (clientRenderFluid != render) {
            clientRenderFluid = render;
            dirty = true;
        }
        if (dirty) {
            markDirtyClient();
        }
    }

    // Client side
    public float getClientRenderHeight() {
        return renderHeight;
    }

    // Client side
    public Fluid getClientRenderFluid() {
        return clientRenderFluid;
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        tagCompound.putInt("blobid", blobId);
        saveClientDataToNBT(tagCompound);
        super.saveAdditional(tagCompound);
    }

    @Override
    protected void saveInfo(CompoundNBT tagCompound) {
        super.saveInfo(tagCompound);
        CompoundNBT tag = new CompoundNBT();
        preservedFluid.writeToNBT(tag);
        getOrCreateInfo(tagCompound).put("preserved", tag);
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        if (tagCompound.contains("blobid")) {
            blobId = tagCompound.getInt("blobid");
        } else {
            blobId = -1;
        }
        loadClientDataFromNBT(tagCompound);
        super.load(tagCompound);
    }

    @Override
    protected void loadInfo(CompoundNBT tagCompound) {
        super.loadInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        if (info.contains("preserved")) {
            preservedFluid = FluidStack.loadFluidStackFromNBT(info.getCompound("preserved"));
        } else {
            preservedFluid = FluidStack.EMPTY;
        }
    }

    @Override
    public void saveClientDataToNBT(CompoundNBT tagCompound) {
        tagCompound.putFloat("renderC", renderHeight);
        if (clientRenderFluid != null) {
            tagCompound.putString("fluidC", clientRenderFluid.getRegistryName().toString());
        }
    }

    @Override
    public void loadClientDataFromNBT(CompoundNBT tagCompound) {
        renderHeight = tagCompound.getFloat("renderC");
        if (tagCompound.contains("fluidC")) {
            clientRenderFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tagCompound.getString("fluidC")));
        } else {
            clientRenderFluid = null;
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (FluidUtil.getFluidHandler(player.getItemInHand(hand)).isPresent()) {
            if (!level.isClientSide) {
                FluidUtil.interactWithFluidHandler(player, hand, getLevel(), result.getBlockPos(), result.getDirection());
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, player, hand, result);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide()) {
            addBlockToNetwork();
            TankBlob network = getBlob();
            if (network != null) {
                CompoundNBT tag = stack.getTag();
                if (tag != null) {
                    getDriver().modify(getMultiblockId(), holder -> {
                        CompoundNBT infoTag = tag.getCompound("BlockEntityTag").getCompound("Info");
                        if (infoTag.contains("preserved")) {
                            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(infoTag.getCompound("preserved"));
                            holder.getMb().fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onReplaced(World world, BlockPos pos, BlockState state, BlockState newstate) {
        if (!world.isClientSide()) {
            if (newstate.getBlock() != GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                TankBlob network = getBlob();
                if (network != null) {
//                    int energy = network.getEnergy() / network.getGeneratorBlocks();
//                    network.setEnergy(network.getEnergy() - energy);
                    network.getData().ifPresent(data -> {
                        preservedFluid = data.getFluidStack().copy();
                        int amount = data.getAmount() / network.getTankBlocks();
                        preservedFluid.setAmount(amount);
                        setChanged();
                    });
                }
                removeBlockFromNetwork();
            }

            BlockState stateUp = world.getBlockState(pos.above());
            if (stateUp.getBlock() == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                world.sendBlockUpdated(pos.above(), stateUp, stateUp, Constants.BlockFlags.DEFAULT);
            }
            BlockState stateDown = world.getBlockState(pos.below());
            if (stateDown.getBlock() == GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                world.sendBlockUpdated(pos.below(), stateDown, stateDown, Constants.BlockFlags.DEFAULT);
            }
        }
    }

    public void addBlockToNetwork() {
        TankBlob newMb = new TankBlob().setTankBlocks(1);
        newMb.updateDistribution(Collections.singleton(worldPosition));
        MultiblockSupport.addBlock(level, getBlockPos(), DRTankNetwork.getNetwork(level).getDriver(), newMb);
        updateHeightsForClient();
    }

    public void removeBlockFromNetwork() {
        MultiblockSupport.removeBlock(level, getBlockPos(), DRTankNetwork.getNetwork(level).getDriver());
    }

    private void updateHeightsForClient() {
        int id = getMultiblockId();
        if (id != -1) {
            TankBlob blob = getBlob();
            FluidStack fluidStack = blob.getData().map(LiquidCrystalData::getFluidStack).orElse(FluidStack.EMPTY);
            int amount = fluidStack.getAmount();
            int capacityPerTank = blob.getCapacityPerTank();
            DRTankNetwork.foreach(level, id, blockPos -> {
                TileEntity be = level.getBlockEntity(blockPos);
                if (be instanceof TankTileEntity) {
                    int countBelow = blob.getBlocksBelowY(blockPos.getY());
                    int countAtY = blob.getBlocksAtY(blockPos.getY());
                    float height;
                    if (amount <= countBelow * capacityPerTank) {
                        // The fluid doesn't come to this height
                        height = 0;
                    } else if (amount > (countBelow + countAtY) * capacityPerTank) {
                        // The fluid comes above this height
                        height = 1.0f;
                    } else {
                        height = amount - (countBelow * capacityPerTank);
                        height /= (countAtY * capacityPerTank);
                    }

                    ((TankTileEntity) be).setClientData(height, fluidStack.getFluid());
                }
            }, worldPosition);
        }
    }

    @Override
    public ResourceLocation getId() {
        return DRTankNetwork.TANK_NETWORK_ID;
    }

    @Override
    public int getMultiblockId() {
        return blobId;
    }

    @Override
    public void setMultiblockId(int newId) {
        if (blobId != newId) {
            blobId = newId;
            setChanged();
        }
    }

    public TankBlob getBlob() {
        if (blobId == -1) {
            return null;
        }
        DRTankNetwork network = DRTankNetwork.getNetwork(level);
        return network.getOrCreateBlob(blobId);
    }

    private MultiblockDriver<TankBlob> getDriver() {
        return DRTankNetwork.getNetwork(level).getDriver();
    }

    @Nonnull
    private IFluidHandler createFluidHandler() {
        return new DRTankHandler(level, this::getMultiblockId) {
            @Override
            public void onUpdate() {
                updateHeightsForClient();
                setChanged();
                DRTankNetwork.getNetwork(level).save();
            }
        };
    }
}
