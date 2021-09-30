package mcjty.deepresonance.modules.machines.tile;

import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.Tools;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.generic;

/**
 * Created by Elec332 on 26-7-2020
 */
public class TileEntitySmelter extends GenericTileEntity implements ITickableTileEntity {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.DOWN, Direction.UP);
    private int processTimeLeft = 0;
    private int processTime = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(generic().out(), CONTAINER_CONTAINER, SLOT, 64, 24)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Purifier")
            .containerSupplier((windowId,player) -> new GenericContainer(MachinesModule.SMELTER_CONTAINER.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), TileEntitySmelter.this))
            .itemHandler(() -> items)
            .shortListener(Tools.holder(() -> processTime, v -> processTime = v))
            .shortListener(Tools.holder(() -> processTimeLeft, v -> processTimeLeft = v)));

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, false, MachinesModule.smelterConfig.powerMaximum.get(), MachinesModule.smelterConfig.powerPerTickIn.get());
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    private float finalQuality = 1.0f;  // Calculated quality based on the amount of lava in the lava tank
    private float finalPurity = 0.1f;   // Calculated quality based on the amount of lava in the lava tank

    public TileEntitySmelter() {
        super(MachinesModule.TYPE_SMELTER.get());
    }

    @Override
    public void tick() {
        if (level.isClientSide()) {
            return;
        }
        if (processTimeLeft > 0) {
            if (canWork()) {
                processTimeLeft--;
                energyStorage.consumeEnergy(MachinesModule.smelterConfig.powerPerOreTick.get());
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
                level.setBlock(getBlockPos(), state, Constants.BlockFlags.DEFAULT);
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
        int fill = MachinesModule.smelterConfig.rclPerOre.get();
        if (tankHook.getTank2().fill(DeepResonanceFluidHelper.makeLiquidCrystalStack(fill), IFluidHandler.FluidAction.SIMULATE) != fill) {
            return false;
        }
        return energyStorage.getEnergyStored() >= MachinesModule.smelterConfig.powerPerOreTick.get();
    }


    private boolean inputSlotValid() {
        return true;
        // @todo 1.16
//        return !items.getStackInSlot(SLOT).isEmpty() && DeepResonanceTags.RESONANT_ORE_ITEM.contains(items.getStackInSlot(SLOT).getItem());
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

        lavaTank.drain(new FluidStack(Fluids.LAVA, MachinesModule.smelterConfig.lavaCost.get()), IFluidHandler.FluidAction.EXECUTE);

        int processTimeConfig = MachinesModule.smelterConfig.processTime.get();
        processTimeLeft = processTimeConfig + (int) ((percentage - 0.5f) * processTimeConfig);
        processTime = processTimeLeft;
    }

    private void finishSmelting() {
        if (finalQuality > 0.0f) {
            FluidStack stack = DeepResonanceFluidHelper.makeLiquidCrystalStack(MachinesModule.smelterConfig.rclPerOre.get(), finalQuality, finalPurity, 0.1f, 0.1f);
            tankHook.getTank2().fill(stack, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        tagCompound.putInt("processTime", processTime);
        tagCompound.putInt("processTimeLeft", processTimeLeft);
        tagCompound.putFloat("finalQuality", finalQuality);
        tagCompound.putFloat("finalPurity", finalPurity);

        return super.save(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        processTime = tagCompound.getInt("processTime");
        processTimeLeft = tagCompound.getInt("processTimeLeft");
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
