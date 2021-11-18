package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.SmelterConfig;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.sync.SyncToGui;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.generic;

public class SmelterTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.DOWN, Direction.UP);

    @SyncToGui
    private short processTimeLeft = 0;

    @SyncToGui
    private short processTime = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(generic().in().out(), SLOT, 64, 24)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final NoDirectionItemHander items = createItemHandler();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, SmelterConfig.POWER_MAXIMUM.get(), SmelterConfig.POWER_PER_TICK_IN.get());

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Smelter")
            .containerSupplier((windowId,player) -> new GenericContainer(MachinesModule.SMELTER_CONTAINER.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), SmelterTileEntity.this))
            .energyHandler(() -> energyStorage)
            .itemHandler(() -> items)
            .setupSync(this));

    private float finalQuality = 1.0f;  // Calculated quality based on the amount of lava in the lava tank
    private float finalPurity = 0.1f;   // Calculated quality based on the amount of lava in the lava tank

    public SmelterTileEntity() {
        super(MachinesModule.TYPE_SMELTER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(
                new BlockBuilder()
                        .tileEntitySupplier(SmelterTileEntity::new)
                        .infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("smelter")))) {

            @Override
            public RotationType getRotationType() {
                return RotationType.HORIZROTATION;
            }

            @Override
            protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(BlockStateProperties.POWERED);
            }

        };
    }

    @Override
    public void tick() {
        if (level.isClientSide()) {
            return;
        }
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
                level.setBlock(getBlockPos(), state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
            }
        }
    }

    private boolean canWork() {
        if (!tankHook.checkTankContents(Fluids.LAVA, null)) {
            return false;
        }
        if (tankHook.getTank1().getFluidInTank(0).getAmount() < tankHook.getTank1().getTankCapacity(0) * 0.25) {
            return false;
        }
        int fill = SmelterConfig.RCL_PER_ORE.get();
        if (tankHook.getTank2().fill(DeepResonanceFluidHelper.makeLiquidCrystalStack(fill), IFluidHandler.FluidAction.SIMULATE) != fill) {
            return false;
        }
        return energyStorage.getEnergyStored() >= SmelterConfig.POWER_PER_ORE_TICK.get();
    }


    private boolean inputSlotValid() {
        return !items.getStackInSlot(SLOT).isEmpty() && DeepResonanceTags.RESONANT_ORE_ITEM.contains(items.getStackInSlot(SLOT).getItem());
    }

    private void startSmelting() {
        ItemStack stack = items.extractItem(SLOT, 1, false);
        if (stack.isEmpty()) {
            return;
        }

        IFluidHandler lavaTank = tankHook.getTank1();

        float percentage = (float) lavaTank.getFluidInTank(0).getAmount() / lavaTank.getTankCapacity(0);

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

        lavaTank.drain(new FluidStack(Fluids.LAVA, SmelterConfig.LAVA_COST.get()), IFluidHandler.FluidAction.EXECUTE);

        int processTimeConfig = SmelterConfig.PROCESS_TIME.get();
        processTimeLeft = (short) (processTimeConfig + (int) ((percentage - 0.5f) * processTimeConfig));
        processTime = processTimeLeft;
    }

    private void finishSmelting() {
        if (finalQuality > 0.0f) {
            FluidStack stack = DeepResonanceFluidHelper.makeLiquidCrystalStack(SmelterConfig.RCL_PER_ORE.get(), finalQuality, finalPurity, 0.1f, 0.1f);
            tankHook.getTank2().fill(stack, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tagCompound) {
        tagCompound.putInt("processTime", processTime);
        tagCompound.putInt("processTimeLeft", processTimeLeft);
        tagCompound.putFloat("finalQuality", finalQuality);
        tagCompound.putFloat("finalPurity", finalPurity);

        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        processTime = (short) tagCompound.getInt("processTime");
        processTimeLeft = (short) tagCompound.getInt("processTimeLeft");
        finalQuality = tagCompound.getFloat("finalQuality");
        finalPurity = tagCompound.getFloat("finalPurity");
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

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return true; // @todo 1.16 DeepResonanceTags.RESONANT_ORE_ITEM.contains(stack.getItem());
            }
        };
    }
}
