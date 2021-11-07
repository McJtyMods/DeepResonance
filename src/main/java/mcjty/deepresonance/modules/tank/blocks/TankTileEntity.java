package mcjty.deepresonance.modules.tank.blocks;

import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.client.ClientTankData;
import mcjty.deepresonance.modules.tank.data.DRTankHandler;
import mcjty.deepresonance.modules.tank.data.DRTankNetwork;
import mcjty.deepresonance.modules.tank.data.TankBlob;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.McJtyLib;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.multiblock.MultiblockSupport;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TankTileEntity extends GenericTileEntity implements IMultiblockConnector {

    private int blobId = -1;

    // Client only
    private Fluid clientRenderFluid;
    private float renderHeight; //Value from 0.0f to 1.0f

    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(this::createFluidHandler);

    public TankTileEntity() {
        super(TankModule.TYPE_TANK.get());
    }


    //    @Override
//    public void setRemoved() {
//        super.setRemoved();
//        if (level != null && !level.isClientSide()) {
//            McJtyLib.SYNCER.unregisterWatchHandler(TankModule.TANK_SYNC_ID, GlobalPos.of(level.dimension(), worldPosition));
//        }
//    }
//
//    @Override
//    public void clearRemoved() {
//        super.clearRemoved();
//        if (level != null && !level.isClientSide()) {
//            registerWatchHandler();
//        }
//    }
//
//    @Override
//    public void onLoad() {
//        super.onLoad();
//        if (level != null && !level.isClientSide()) {
//            registerWatchHandler();
//        }
//    }

//    private void registerWatchHandler() {
//        McJtyLib.SYNCER.registerWatchHandler(TankModule.TANK_SYNC_ID, GlobalPos.of(level.dimension(), worldPosition),
//                this::publishToClients);
//    }
//
//    private void publishToClients() {
//        getBlob().getData().ifPresent(data -> {
//            int amount = 8; // @todo calculate!
//            McJtyLib.SYNCER.publish(level, worldPosition, new ClientTankData(GlobalPos.of(level.dimension(), worldPosition), data.getStack(), amount));
//        });
//    }

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
            this.markDirtyClient();
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
    public CompoundNBT save(CompoundNBT tagCompound) {
        tagCompound.putInt("blobid", blobId);
        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        if (tagCompound.contains("blobid")) {
            blobId = tagCompound.getInt("blobid");
        } else {
            blobId = -1;
        }
        super.read(tagCompound);
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        tagCompound.putFloat("renderC", renderHeight);
        if (clientRenderFluid != null) {
            tagCompound.putString("fluidC", clientRenderFluid.getRegistryName().toString());
        }
    }

    @Override
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        super.readClientDataFromNBT(tagCompound);
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

// @todo 1.16
//    @Override
//    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
//        CompoundNBT tag = hitData.getData();
//        if (tag.contains("capacity")) {
//            if (tag.contains("fluid")) {
//                Fluid fluid = RegistryHelper.getFluidRegistry().getValue(new ResourceLocation(tag.getString("fluid")));
//                if (fluid != null) {
//                    information.addInformation(StatCollector.translateToLocal(fluid.getAttributes().getTranslationKey()));
//                    if (tag.contains("efficiency")) {
//                        DecimalFormat decimalFormat = new DecimalFormat("#.#");
//                        decimalFormat.setRoundingMode(RoundingMode.DOWN);
//                        information.addInformation("");
//                        information.addInformation("Efficiency: " + decimalFormat.format(tag.getFloat("efficiency") * 100) + "%");
//                        information.addInformation("Purity: " + decimalFormat.format(tag.getFloat("purity") * 100) + "%");
//                        information.addInformation("Quality: " + decimalFormat.format(tag.getFloat("quality") * 100) + "%");
//                        information.addInformation("Strength: " + decimalFormat.format(tag.getFloat("strength") * 100) + "%");
//                    }
//                }
//            }
//            information.addInformation(tag.getInt("amt") + "/" + tag.getInt("capacity") + "mB");
//        }
//    }

    // @todo 1.16
//    @Override
//    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
//        if (grid != null) {
//            tag.putInt("capacity", grid.getTankCapacity(0));
//            tag.putInt("amt", grid.getFluidAmount());
//            Fluid fluid = grid.getStoredFluid();
//            if (fluid != null) {
//                tag.putString("fluid", Preconditions.checkNotNull(fluid.getRegistryName()).toString());
//                ILiquidCrystalData data = DeepResonanceFluidHelper.readCrystalDataFromStack(grid.getFluidInTank(0));
//                if (data != null) {
//                    tag.putFloat("efficiency", data.getEfficiency());
//                    tag.putFloat("purity", data.getPurity());
//                    tag.putFloat("quality", data.getQuality());
//                    tag.putFloat("strength", data.getStrength());
//                }
//            }
//        }
//    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide()) {
            addBlockToNetwork();
            TankBlob network = getBlob();
            if (network != null) {
                CompoundNBT tag = stack.getTag();
                if (tag != null) {
                    getDriver().modify(getMultiblockId(), holder -> {
//                        holder.getMb().setEnergy(holder.getMb().getEnergy() + tag.getInt("energy"));
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
        MultiblockSupport.addBlock(level, getBlockPos(), DRTankNetwork.getNetwork(level).getDriver(), newMb);
    }

    public void removeBlockFromNetwork() {
        MultiblockSupport.removeBlock(level, getBlockPos(), DRTankNetwork.getNetwork(level).getDriver());
    }

    private void updateHeightsForClient() {
        int id = getMultiblockId();
        if (id != -1) {
            TankBlob blob = getBlob();
            FluidStack fluidStack = blob.getData().map(LiquidCrystalData::getStack).orElse(FluidStack.EMPTY);
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
                        xxx
                    }

                    ((TankTileEntity) be).setClientData();
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
            markDirtyClient();
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
        return new DRTankHandler(level, () -> blobId) {
            @Override
            public void onUpdate() {
                updateHeightsForClient();
                setChanged();
                DRTankNetwork.getNetwork(level).save();
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
