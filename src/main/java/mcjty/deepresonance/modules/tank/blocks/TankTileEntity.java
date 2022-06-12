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
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Objects;

public class TankTileEntity extends GenericTileEntity implements IMultiblockConnector {

    private int blobId = -1;

    // Client only
    @Nonnull
    private LiquidCrystalData clientRenderFluid = LiquidCrystalData.EMPTY;
    private float renderHeight; //Value from 0.0f to 1.0f

    // Only used when the tank is broken and needs to be put back later
    private FluidStack preservedFluid = FluidStack.EMPTY;

    @Cap(type = CapType.FLUIDS)
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(this::createFluidHandler);

    public TankTileEntity(BlockPos pos, BlockState state) {
        super(TankModule.TYPE_TANK.get(), pos, state);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandler.invalidate();
    }

    public void setClientData(float newHeight, LiquidCrystalData render) {
        boolean dirty = false;
        if (newHeight >= 0 && renderHeight != newHeight) {
            renderHeight = newHeight;
            dirty = true;
        }
        if (!Objects.equals(clientRenderFluid, render)) {
            clientRenderFluid = LiquidCrystalData.fromStack(render.getFluidStack());
            dirty = true;
        }
        if (dirty) {
            markDirtyClient();
        }
    }

    public int getComparatorValue() {
        return fluidHandler.map(handler -> {
            float f = (float) handler.getFluidInTank(0).getAmount() / handler.getTankCapacity(0);
            return (int) (f * 15);
        }).orElse(0);
    }

    // Client side
    public float getClientRenderHeight() {
        return renderHeight;
    }

    // Client side
    @Nonnull
    public Fluid getClientRenderFluid() {
        return clientRenderFluid.getFluidStack().getFluid();
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        tagCompound.putInt("blobid", blobId);
        saveClientDataToNBT(tagCompound);
        super.saveAdditional(tagCompound);
    }

    @Override
    protected void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        CompoundTag tag = new CompoundTag();
        preservedFluid.writeToNBT(tag);
        getOrCreateInfo(tagCompound).put("preserved", tag);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        if (tagCompound.contains("blobid")) {
            blobId = tagCompound.getInt("blobid");
        } else {
            blobId = -1;
        }
        loadClientDataFromNBT(tagCompound);
        super.load(tagCompound);
    }

    @Override
    protected void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
        if (info.contains("preserved")) {
            preservedFluid = FluidStack.loadFluidStackFromNBT(info.getCompound("preserved"));
        } else {
            preservedFluid = FluidStack.EMPTY;
        }
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
        tagCompound.putFloat("renderC", renderHeight);
        if (!clientRenderFluid.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            clientRenderFluid.getFluidStack().writeToNBT(tag);
            tagCompound.put("fluidC", tag);
        }
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        renderHeight = tagCompound.getFloat("renderC");
        if (tagCompound.contains("fluidC")) {
            Tag fluidTag = tagCompound.get("fluidC");
            if (StringTag.TYPE.equals(fluidTag.getType())) {
                // For compatibility
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidTag.getAsString()));
                clientRenderFluid = LiquidCrystalData.fromStack(new FluidStack(fluid, 1));
            } else {
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundTag) fluidTag);
                clientRenderFluid = LiquidCrystalData.fromStack(fluidStack);
            }
        } else {
            clientRenderFluid = LiquidCrystalData.EMPTY;
        }
    }

    @Override
    public InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (FluidUtil.getFluidHandler(itemInHand).isPresent()) {
            if (!level.isClientSide) {
                FluidUtil.interactWithFluidHandler(player, hand, getLevel(), result.getBlockPos(), result.getDirection());
            }
            return InteractionResult.SUCCESS;
        } else {
            // Show info about contained liquid
            if (!level.isClientSide) {
                fluidHandler.ifPresent(handler -> {
                    DecimalFormat decimalFormat = new DecimalFormat("#.#");
                    FluidStack fluid = handler.getFluidInTank(0);
                    if (fluid.isEmpty()) {
                        player.sendMessage(ComponentFactory.literal("Tank is empty").withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
                    } else {
                        String amount = " (" + fluid.getAmount() + " mb)";
                        player.sendMessage(ComponentFactory.literal("Liquid: ")
                                .append(ComponentFactory.translatable(fluid.getTranslationKey()))
                                .append(ComponentFactory.literal(amount))
                                .withStyle(ChatFormatting.AQUA), Util.NIL_UUID);
                        if (LiquidCrystalData.isLiquidCrystal(fluid.getFluid())) {
                            LiquidCrystalData d = LiquidCrystalData.fromStack(fluid);
                            player.sendMessage(ComponentFactory.literal("Quality " + decimalFormat.format(d.getQuality() * 100) + "%"), Util.NIL_UUID);
                            player.sendMessage(ComponentFactory.literal("Efficiency " + decimalFormat.format(d.getEfficiency() * 100) + "%"), Util.NIL_UUID);
                            player.sendMessage(ComponentFactory.literal("Purity " + decimalFormat.format(d.getPurity() * 100) + "%"), Util.NIL_UUID);
                            player.sendMessage(ComponentFactory.literal("Strength " + decimalFormat.format(d.getStrength() * 100) + "%"), Util.NIL_UUID);
                        }
                    }
                });
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide()) {
            addBlockToNetwork();
            TankBlob network = getBlob();
            if (network != null) {
                CompoundTag tag = stack.getTag();
                if (tag != null) {
                    getDriver().modify(getMultiblockId(), holder -> {
                        CompoundTag infoTag = tag.getCompound("BlockEntityTag").getCompound("Info");
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
    public void onReplaced(Level world, BlockPos pos, BlockState state, BlockState newstate) {
        if (!world.isClientSide()) {
            if (newstate.getBlock() != GeneratorModule.GENERATOR_PART_BLOCK.get()) {
                TankBlob network = getBlob();
                if (network != null) {
                    LiquidCrystalData data = network.getData();
                    if (!data.isEmpty()) {
                        preservedFluid = data.getFluidStack().copy();
                        int amount = data.getAmount() / network.getTankBlocks();
                        preservedFluid.setAmount(amount);
                        data.setAmount(data.getAmount() - amount);
                    }
                    setChanged();
                }
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
        TankBlob newMb = new TankBlob().setTankBlocks(1);
        newMb.updateDistribution(Collections.singleton(worldPosition));
        MultiblockSupport.addBlock(level, getBlockPos(), DRTankNetwork.getNetwork(level).getDriver(), newMb);
        updateHeightsForClient();
    }

    public void removeBlockFromNetwork() {
        MultiblockSupport.removeBlock(level, getBlockPos(), DRTankNetwork.getNetwork(level).getDriver());
    }

    @Nonnull
    private LiquidCrystalData getClientLiquidData() {
        return clientRenderFluid;
    }

    private void updateHeightsForClient() {
        int id = getMultiblockId();
        if (id != -1) {
            TankBlob blob = getBlob();
            LiquidCrystalData data = blob.getData();
            FluidStack fluidStack = data.getFluidStack();
            int amount = fluidStack.getAmount();
            int capacityPerTank = blob.getCapacityPerTank();
            DRTankNetwork.foreach(level, id, blockPos -> {
                BlockEntity be = level.getBlockEntity(blockPos);
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

                    ((TankTileEntity) be).setClientData(height, data);
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
        return new DRTankHandler(level, this::getMultiblockId, this::getClientLiquidData) {
            @Override
            public void onUpdate() {
                updateHeightsForClient();
                setChanged();
                DRTankNetwork.getNetwork(level).save();
            }
        };
    }
}
