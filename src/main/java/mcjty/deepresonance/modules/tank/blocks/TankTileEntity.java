package mcjty.deepresonance.modules.tank.blocks;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import mcjty.deepresonance.modules.tank.util.TankTransfers;
import net.minecraft.core.RegistryAccess;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import mcjty.deepresonance.modules.generator.GeneratorModule;
import mcjty.deepresonance.modules.tank.TankModule;
import mcjty.deepresonance.modules.tank.data.DRTankHandler;
import mcjty.deepresonance.modules.tank.data.DRTankNetwork;
import mcjty.deepresonance.modules.tank.data.TankBlob;
import mcjty.deepresonance.modules.tank.data.TankData;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.multiblock.IMultiblockConnector;
import mcjty.lib.multiblock.MultiblockDriver;
import mcjty.lib.multiblock.MultiblockSupport;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.Tools;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
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
import mcjty.lib.varia.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

public class TankTileEntity extends GenericTileEntity implements IMultiblockConnector {

    private int blobId = -1;

    // Client only
    @Nonnull
    private LiquidCrystalData clientRenderFluid = LiquidCrystalData.EMPTY;
    private float renderHeight; //Value from 0.0f to 1.0f

    private final DRTankHandler fluidHandler = new DRTankHandler(this);
    @Cap(type = CapType.FLUIDS)
    private static final Function<TankTileEntity, DRTankHandler> FLUID_CAP = tile -> tile.fluidHandler;

    public TankTileEntity(BlockPos pos, BlockState state) {
        super(TankModule.TYPE_TANK.get(), pos, state);
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
        DRTankHandler handler = fluidHandler;
        float f = (float) TankTransfers.contents(handler).getAmount() / TankTransfers.capacity(handler);
        return (int) (f * 15);
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

    public DRTankHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        HolderLookup.Provider provider = level == null ? RegistryAccess.EMPTY : level.registryAccess();
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putInt("blobid", blobId);
        saveClientDataToNBT(tagCompound, provider);
        output.store(MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tagCompound);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        HolderLookup.Provider provider = input.lookup();
        CompoundTag tagCompound = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        blobId = tagCompound.contains("blobid") ? tagCompound.getIntOr("blobid", 0) : -1;
        loadClientDataFromNBT(tagCompound, provider);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound, HolderLookup.Provider provider) {
        tagCompound.putFloat("renderC", renderHeight);
        if (!clientRenderFluid.isEmpty()) {
            CompoundTag tag = LiquidCrystalData.writeStack(clientRenderFluid.getFluidStack(), provider);
            tagCompound.put("fluidC", tag);
        }
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound, HolderLookup.Provider provider) {
        renderHeight = tagCompound.getFloatOr("renderC", 0.0f);
        if (tagCompound.contains("fluidC")) {
            Tag fluidTag = tagCompound.get("fluidC");
            if (StringTag.TYPE.equals(fluidTag.getType())) {
                // For compatibility
                Fluid fluid = Tools.getFluid(Identifier.parse(fluidTag.asString().orElseThrow()));
                clientRenderFluid = LiquidCrystalData.fromStack(new FluidStack(fluid, 1));
            } else {
                FluidStack fluidStack = LiquidCrystalData.readStack(provider, (CompoundTag) fluidTag);
                clientRenderFluid = LiquidCrystalData.fromStack(fluidStack);
            }
        } else {
            clientRenderFluid = LiquidCrystalData.EMPTY;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        TankBlob network = getBlob();
        if (network != null) {
            FluidStack preservedFluid;
            LiquidCrystalData data = network.getData();
            if (!data.isEmpty()) {
                preservedFluid = data.getFluidStack().copy();
                int amount = data.getAmount() / network.getTankBlocks();
                preservedFluid.setAmount(amount);
                data.setAmount(data.getAmount() - amount);
            } else {
                preservedFluid = FluidStack.EMPTY;
            }
            builder.set(TankModule.ITEM_TANK_DATA.get(), new TankData(preservedFluid));
        }
    }

    @Override
    public InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult result) {
        if (ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM) != null) {
            if (!level.isClientSide()) {
                FluidStorageUtil.interactWithFluidStorage(fluidHandler, player, hand);
            }
            return InteractionResult.SUCCESS;
        } else {
            // Show info about contained liquid
            if (!level.isClientSide()) {
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                FluidStack fluid = TankTransfers.contents(fluidHandler);
                if (fluid.isEmpty()) {
                    player.sendSystemMessage(ComponentFactory.literal("Tank is empty").withStyle(ChatFormatting.YELLOW));
                } else {
                    String amount = " (" + fluid.getAmount() + " mb)";
                    player.sendSystemMessage(ComponentFactory.literal("Liquid: ")
                            .append(fluid.getHoverName())
                            .append(ComponentFactory.literal(amount))
                            .withStyle(ChatFormatting.AQUA));
                    if (LiquidCrystalData.isLiquidCrystal(fluid.getFluid())) {
                        LiquidCrystalData d = LiquidCrystalData.fromStack(fluid);
                        player.sendSystemMessage(ComponentFactory.literal("Quality " + decimalFormat.format(d.getQuality() * 100) + "%"));
                        player.sendSystemMessage(ComponentFactory.literal("Efficiency " + decimalFormat.format(d.getEfficiency() * 100) + "%"));
                        player.sendSystemMessage(ComponentFactory.literal("Purity " + decimalFormat.format(d.getPurity() * 100) + "%"));
                        player.sendSystemMessage(ComponentFactory.literal("Strength " + decimalFormat.format(d.getStrength() * 100) + "%"));
                    }
                }
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
                TankData data = stack.get(TankModule.ITEM_TANK_DATA.get());
                if (data != null && !data.preservedLiquid().isEmpty()) {
                    getDriver().modify(getMultiblockId(), holder -> {
                        FluidStack fluidStack = data.preservedLiquid();
                        if (!fluidStack.isEmpty()) {
                            holder.getMb().fill(fluidStack, true);
                            updateHeightsForClient();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onReplaced(Level world, BlockPos pos, BlockState state, BlockState newstate) {
        if (!world.isClientSide()) {
            if (newstate.getBlock() != TankModule.TANK.block().get()) {
                TankBlob network = getBlob();
                if (network != null) {
                    LiquidCrystalData data = network.getData();
                    if (!data.isEmpty()) {
                        int amount = data.getAmount() / network.getTankBlocks();
                        data.setAmount(data.getAmount() - amount);
                    }
                    setChanged();
                }
                removeBlockFromNetwork();
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
        TankBlob newMb = new TankBlob().setTankBlocks(1);
        newMb.updateDistribution(Collections.singleton(worldPosition));
        MultiblockSupport.addBlock(level, getBlockPos(), DRTankNetwork.getNetwork(level).getDriver(), newMb);
        updateHeightsForClient();
    }

    public void removeBlockFromNetwork() {
        MultiblockSupport.removeBlock(level, getBlockPos(), DRTankNetwork.getNetwork(level).getDriver());
    }

    @Nonnull
    public LiquidCrystalData getClientLiquidData() {
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
    public Identifier getId() {
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

    public DRTankHandler getInternalFluidHandler() {
        return fluidHandler;
    }

    public void onFluidChanged() {
        updateHeightsForClient();
        setChanged();
        DRTankNetwork.getNetwork(level).save();
    }
}
