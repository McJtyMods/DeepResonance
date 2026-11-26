package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.modules.tank.util.TankTransfers;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import static mcjty.lib.setup.Registration.ITEM_INVENTORY;
import static mcjty.lib.setup.Registration.ITEM_ENERGY;
import net.minecraft.core.RegistryAccess;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.SmelterConfig;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.TagTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import mcjty.lib.varia.Lazy;
import mcjty.lib.varia.FluidStack;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.container.SlotDefinition.generic;

public class SmelterTileEntity extends TickingTileEntity {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.DOWN, Direction.UP);

    @GuiValue
    private int processTimeLeft = 0;

    @GuiValue
    private int processTime = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(generic().in().out(), SLOT, 64, 24)
            .playerSlots(10, 70));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((integer, itemStack) -> itemStack.is(DeepResonanceTags.RESONANT_ORE_ITEM))
            .build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<SmelterTileEntity, GenericItemHandler> ITEMS_CAP = tile -> tile.items;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, SmelterConfig.POWER_MAXIMUM.get(), SmelterConfig.POWER_PER_TICK_IN.get());
    @Cap(type = CapType.ENERGY)
    private static final Function<SmelterTileEntity, GenericEnergyStorage> ENERGY_CAP = tile -> tile.energyStorage;

    @Cap(type = CapType.CONTAINER)
    private static final Function<SmelterTileEntity, MenuProvider> screenHandler = be -> new DefaultContainerProvider<GenericContainer>("Smelter")
            .containerSupplier(container(MachinesModule.SMELTER_CONTAINER, CONTAINER_FACTORY, be))
            .energyHandler(() -> be.energyStorage)
            .itemHandler(() -> be.items)
            .setupSync(be);

    private float finalQuality = 1.0f;  // Calculated quality based on the amount of lava in the lava tank
    private float finalPurity = 0.1f;   // Calculated quality based on the amount of lava in the lava tank

    public SmelterTileEntity(BlockPos pos, BlockState state) {
        super(MachinesModule.TYPE_SMELTER.get(), pos, state);
    }

    public static BaseBlock createBlock(Block.Properties properties) {
        return new BaseBlock(
                new BlockBuilder()
                        .properties(properties)
                        .tileEntitySupplier(SmelterTileEntity::new)
                .manualEntry(ManualHelper.create("deepresonance:machines/smelter"))
                        .info(TooltipBuilder.key("message.deepresonance.shiftmessage"))
                        .infoShift(TooltipBuilder.header())) {

            @Override
            public RotationType getRotationType() {
                return RotationType.HORIZROTATION;
            }

            @Override
            protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(BlockStateProperties.POWERED);
            }

        };
    }

    @Override
    public void tickServer() {
        if (processTimeLeft > 0) {
            if (canWork()) {
                processTimeLeft--;
                energyStorage.consumeEnergy(SmelterConfig.POWER_PER_ORE_TICK.get());
                if (processTimeLeft == 0) {
                    // Done!
                    finishSmelting();
                }
            }
        } else {
            BlockState state = level.getBlockState(getBlockPos());
            boolean oldworking = state.getValue(BlockStateProperties.POWERED);
            boolean newworking;
            if (canWork() && inputSlotValid()) {
                startSmelting();
                newworking = true;
            } else {
                newworking = false;
            }
            if (newworking != oldworking) {
                state = state.setValue(BlockStateProperties.POWERED, newworking);
                level.setBlock(getBlockPos(), state, Block.UPDATE_ALL_IMMEDIATE);
            }
        }
    }

    private boolean canWork() {
        if (!tankHook.checkTankContents(Fluids.LAVA, null)) {
            return false;
        }
        if (TankTransfers.contents(tankHook.getTank1()).getAmount() < TankTransfers.capacity(tankHook.getTank1()) * 0.25) {
            return false;
        }
        int fill = SmelterConfig.RCL_PER_ORE.get();
        if (TankTransfers.canInsert(tankHook.getTank2(), LiquidCrystalData.makeLiquidCrystalStack(fill)) != fill) {
            return false;
        }
        return energyStorage.getEnergyStored() >= SmelterConfig.POWER_PER_ORE_TICK.get();
    }

    private boolean inputSlotValid() {
        return !items.getStackInSlot(SLOT).isEmpty() && TagTools.hasTag(items.getStackInSlot(SLOT).getItem(), DeepResonanceTags.RESONANT_ORE_ITEM);
    }

    private void startSmelting() {
        ItemStack stack = items.extractItem(SLOT, 1, false);
        if (stack.isEmpty()) {
            return;
        }

        Storage<FluidVariant> lavaTank = tankHook.getTank1();

        float percentage = (float) TankTransfers.contents(lavaTank).getAmount() / TankTransfers.capacity(lavaTank);

        if (percentage < 0.40f) {
            // Slower smelting progress and slightly reduced quality
            finalQuality = 1.0f - (0.40f - percentage);
            finalPurity = 0.1f;
        } else if (percentage > 0.75f) {
            finalQuality = -1.0f;   // Total waste!
            finalPurity = 0.0f;
        } else if (percentage > 0.60f) {
            // Reduced quality.
            finalQuality = 1.0f - (percentage - 0.60f) * 6.666f;
            finalPurity = 0.1f - (percentage - 0.60f) * 0.3f;
        } else {
            finalQuality = 1.0f;
            finalPurity = 0.1f;
        }

        TankTransfers.extract(lavaTank, new FluidStack(Fluids.LAVA, SmelterConfig.LAVA_COST.get()));

        int processTimeConfig = SmelterConfig.PROCESS_TIME.get();
        processTimeLeft = (short) (processTimeConfig + (int) ((percentage - 0.5f) * processTimeConfig));
        processTime = processTimeLeft;
    }

    private void finishSmelting() {
        if (finalQuality > 0.0f) {
            FluidStack stack = LiquidCrystalData.makeLiquidCrystalStack(SmelterConfig.RCL_PER_ORE.get(), finalQuality, finalPurity, 0.1f, 0.1f);
            TankTransfers.insert(tankHook.getTank2(), stack);
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        HolderLookup.Provider provider = level == null ? RegistryAccess.EMPTY : level.registryAccess();
        CompoundTag tag = new CompoundTag();
        tag.putInt("processTime", processTime);
        tag.putInt("processTimeLeft", processTimeLeft);
        tag.putFloat("finalQuality", finalQuality);
        tag.putFloat("finalPurity", finalPurity);
        energyStorage.save(tag, "energy", provider);
        items.save(tag, "items", provider);
        output.store(MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tag);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        HolderLookup.Provider provider = input.lookup();
        CompoundTag tag = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        processTime = (short) tag.getIntOr("processTime", 0);
        processTimeLeft = (short) tag.getIntOr("processTimeLeft", 0);
        finalQuality = tag.getFloatOr("finalQuality", 0.0f);
        finalPurity = tag.getFloatOr("finalPurity", 0.0f);
        energyStorage.load(tag, "energy", provider);
        items.load(tag,  "items", provider);
    }

    public int getProgress() {
        if (processTime == 0) {
            return 0;
        } else {
            return (int) (((processTime - processTimeLeft) / (float) processTime) * 100);
        }
    }

    public int getMaxPower() {
        return energyStorage.getMaxEnergyStored();
    }

    public int getCurrentPower() {
        return energyStorage.getEnergyStored();
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);
        energyStorage.applyImplicitComponents(input.get(ITEM_ENERGY.get()));
        items.applyImplicitComponents(input.get(ITEM_INVENTORY.get()));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        energyStorage.collectImplicitComponents(builder);
        items.collectImplicitComponents(builder);
    }
}
