package mcjty.deepresonance.modules.machines.tile;

import elec332.core.inventory.BasicItemHandler;
import elec332.core.util.BlockProperties;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.client.gui.SmelterGui;
import mcjty.deepresonance.modules.tank.util.DualTankHook;
import mcjty.deepresonance.util.AbstractPoweredTileEntity;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.deepresonance.util.DeepResonanceTags;
import mcjty.deepresonance.util.RegisteredContainer;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.container.SlotDefinition.generic;

/**
 * Created by Elec332 on 26-7-2020
 */
public class TileEntitySmelter extends AbstractPoweredTileEntity implements ITickableTileEntity {

    public static final int SLOT = 0;

    private final DualTankHook tankHook = new DualTankHook(this, Direction.DOWN, Direction.UP);
    private static final RegisteredContainer<GenericContainer, SmelterGui, TileEntitySmelter> container = new RegisteredContainer<GenericContainer, SmelterGui, TileEntitySmelter>("smelter", 3, factory -> {
        factory.playerSlots(10, 70);
        factory.slot(generic(), ContainerFactory.CONTAINER_CONTAINER, SLOT, 64, 24);
    }) {

        @Override
        public Object createGui(TileEntitySmelter tile, GenericContainer container, PlayerInventory inventory) {
            return new SmelterGui(tile, container, inventory);
        }

    }.modifyContainer((container, tile) -> {
        container.shortListener(syncValue(() -> tile.processTime, i -> tile.processTime = i));
        container.shortListener(syncValue(() -> tile.processTimeLeft, i -> tile.processTimeLeft = i));
    });

    private int processTimeLeft = 0;
    private int processTime = 0;
    private float finalQuality = 1.0f;  // Calculated quality based on the amount of lava in the lava tank
    private float finalPurity = 0.1f;   // Calculated quality based on the amount of lava in the lava tank

    public TileEntitySmelter() {
        super(MachinesModule.TYPE_SMELTER.get(),
                MachinesModule.smelterConfig.powerMaximum.get(), MachinesModule.smelterConfig.powerPerTickIn.get(), new BasicItemHandler(1) {

                    @Override
                    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                        return DeepResonanceTags.RESONANT_ORE_ITEM.contains(stack.getItem());
                    }

                });
    }

    @Nullable
    @Override
    protected LazyOptional<INamedContainerProvider> createScreenHandler() {
        return container.build(this);
    }

    @Override
    public void tick() {
        if (WorldHelper.isClient(getWorld())) {
            return;
        }
        if (processTimeLeft > 0) {
            if (canWork()) {
                processTimeLeft--;
                energyHandler.consumeEnergy(MachinesModule.smelterConfig.powerPerOreTick.get());
                if (processTimeLeft == 0) {
                    // Done!
                    finishSmelting();
                }
            }
        } else {
            BlockState state = WorldHelper.getBlockState(getWorld(), getPos());
            boolean oldworking = state.get(BlockProperties.ACTIVE);
            boolean newworking;
            if (canWork() && inputSlotValid()) {
                startSmelting();
                newworking = true;
            } else {
                newworking = false;
            }
            if (newworking != oldworking) {
                state = state.with(BlockProperties.ACTIVE, newworking);
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
        if (tankHook.getTank2().fill(DeepResonanceFluidHelper.makeLiquidCrystalStack(fill), IFluidHandler.FluidAction.SIMULATE) != fill) {
            return false;
        }
        return energyHandler.getEnergyStored() >= MachinesModule.smelterConfig.powerPerOreTick.get();
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
            FluidStack stack = DeepResonanceFluidHelper.makeLiquidCrystalStack(MachinesModule.smelterConfig.rclPerOre.get(), finalQuality, finalPurity, 0.1f, 0.1f);
            tankHook.getTank2().fill(stack, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putInt("processTime", processTime);
        tagCompound.putInt("processTimeLeft", processTimeLeft);
        tagCompound.putFloat("finalQuality", finalQuality);
        tagCompound.putFloat("finalPurity", finalPurity);

        return super.write(tagCompound);
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

}
