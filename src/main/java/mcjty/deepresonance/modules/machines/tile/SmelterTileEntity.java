package mcjty.deepresonance.modules.machines.tile;

import com.google.common.base.Preconditions;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.inventory.BasicItemHandler;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.fluids.LiquidCrystalData;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.client.SmelterGui;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.setup.FluidRegister;
import mcjty.deepresonance.util.AbstractTileEntity;
import mcjty.deepresonance.util.DeepResonanceContainer;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.deepresonance.util.RegisteredContainer;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 26-7-2020
 */
@RegisteredTileEntity("smelter")
public class SmelterTileEntity extends AbstractTileEntity implements ITickableTileEntity, DeepResonanceContainer.Modifier {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.DOWN, Direction.UP);
    private final GenericEnergyStorage storage = new GenericEnergyStorage(this, true, MachinesModule.smelterConfig.powerMaximum.get(), MachinesModule.smelterConfig.powerPerTickIn.get());
    private final BasicItemHandler itemHandler = new BasicItemHandler(1) {

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return DeepResonanceTags.RESONANT_ORE_ITEM.contains(stack.getItem());
        }

    };
    private final IIntArray data = new IIntArray() {

        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return processTime;
                case 1:
                    return processTimeLeft;
                case 2:
                    return storage.getEnergyStored();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    processTime = value;
                    break;
                case 1:
                    processTimeLeft = value;
                    break;
                case 2:
                    storage.setEnergy(value);
            }
        }

        @Override
        public int size() {
            return 3;
        }

    };
    private static final RegisteredContainer<GenericContainer, SmelterGui, SmelterTileEntity> container = new RegisteredContainer<GenericContainer, SmelterGui, SmelterTileEntity>("smelter", 0, factory -> {
        factory.playerSlots(10, 70);
        factory.slot(SlotDefinition.specific(itemStack -> DeepResonanceTags.RESONANT_ORE_ITEM.contains(itemStack.getItem())), ContainerFactory.CONTAINER_CONTAINER, SLOT, 64, 24);
    }) {

        @Override
        public Object createGui(SmelterTileEntity tile, GenericContainer container, PlayerInventory inventory) {
            return new SmelterGui(tile, container, inventory);
        }

    }.modifyContainer((container, tile) -> {
        container.itemHandler(() -> tile.itemHandler);
        container.energyHandler(() -> tile.storage);
    });
    private final LazyOptional<INamedContainerProvider> screenHandler = container.build(this);
    private final LazyOptional<IItemHandler> inventory = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IEnergyStorage> power = LazyOptional.of(() -> storage);

    private int processTimeLeft = 0;
    private int processTime = 0;
    private float finalQuality = 1.0f;  // Calculated quality based on the amount of lava in the lava tank
    private float finalPurity = 0.1f;   // Calculated quality based on the amount of lava in the lava tank

    @Override
    public void tick() {
        if (Preconditions.checkNotNull(getWorld()).isRemote) {
            return;
        }
        if (processTimeLeft > 0) {
            if (canWork()) {
                processTimeLeft--;
                storage.extractEnergy(MachinesModule.smelterConfig.powerPerOreTick.get(), false);
                if (processTimeLeft == 0) {
                    // Done!
                    finishSmelting();
                }
            }
        } else {
            BlockState state = WorldHelper.getBlockState(getWorld(), getPos());
            boolean oldworking = state.get(BlockStateProperties.LIT);
            boolean newworking;
            if (canWork() && inputSlotValid()) {
                startSmelting();
                newworking = true;
            } else {
                newworking = false;
            }
            if (newworking != oldworking) {
                state = state.with(BlockStateProperties.LIT, newworking);
                WorldHelper.setBlockState(getWorld(), getPos(), state, 3);
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
        if (tankHook.getTank2().fill(new FluidStack(FluidRegister.liquidCrystal, fill), IFluidHandler.FluidAction.SIMULATE) != fill) {
            return false;
        }
        return storage.getEnergyStored() >= MachinesModule.smelterConfig.powerPerOreTick.get();
    }


    private boolean inputSlotValid() {
        return !itemHandler.getStackInSlot(SLOT).isEmpty() && DeepResonanceTags.RESONANT_ORE_ITEM.contains(itemHandler.getStackInSlot(SLOT).getItem());
    }

    private void startSmelting() {
        ItemStack stack = itemHandler.extractItem(SLOT, 1, false);
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
            FluidStack stack = LiquidCrystalData.makeLiquidCrystalStack(MachinesModule.smelterConfig.rclPerOre.get(), finalQuality, finalPurity, 0.1f, 0.1f);
            tankHook.getTank2().fill(stack, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public void onReplaced(World world, BlockPos pos, BlockState state, BlockState newState) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof IInventory) {
                dropInventory(itemHandler);
                world.updateComparatorOutputLevel(pos, newState.getBlock());
            }
            super.onReplaced(world, pos, state, newState);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putInt("processTime", processTime);
        tagCompound.putInt("processTimeLeft", processTimeLeft);
        tagCompound.putFloat("finalQuality", finalQuality);
        tagCompound.putFloat("finalPurity", finalPurity);

        itemHandler.writeToNBT(tagCompound);
        return super.write(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        processTime = tagCompound.getInt("processTime");
        processTimeLeft = tagCompound.getInt("processTimeLeft");
        finalQuality = tagCompound.getFloat("finalQuality");
        finalPurity = tagCompound.getFloat("finalPurity");

        itemHandler.deserializeNBT(tagCompound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, inventory);
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.orEmpty(cap, power);
        }
        return CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY.orEmpty(cap, screenHandler);
    }

    public int getMaxPower() {
        return storage.getMaxEnergyStored();
    }

    public int getCurrentPower() {
        return storage.getEnergyStored();
    }

    public int getProgress() {
        if (processTime == 0) {
            return 0;
        } else {
            return (int) (((processTime - processTimeLeft) / (float) processTime) * 100);
        }
    }

    @Override
    public void modify(DeepResonanceContainer container) {
        container.trackIntArray(data);
    }

}
