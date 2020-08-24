package mcjty.deepresonance.modules.machines.tile;

import elec332.core.inventory.BasicItemHandler;
import elec332.core.util.FMLHelper;
import elec332.core.util.ServerHelper;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.client.gui.CrystallizerGui;
import mcjty.deepresonance.util.AbstractPoweredTileEntity;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.deepresonance.util.RegisteredContainer;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 30-7-2020
 */
public class TileEntityCrystallizer extends AbstractPoweredTileEntity implements ITickableTileEntity {

    public static final int SLOT = 0;

    private static final RegisteredContainer<GenericContainer, CrystallizerGui, TileEntityCrystallizer> container = new RegisteredContainer<GenericContainer, CrystallizerGui, TileEntityCrystallizer>("crystallizer", 1, factory -> {
        factory.playerSlots(10, 70);
        factory.slot(SlotDefinition.container(), ContainerFactory.CONTAINER_CONTAINER, SLOT, 64, 24);
    }) {

        @Override
        public Object createGui(TileEntityCrystallizer tile, GenericContainer container, PlayerInventory inventory) {
            return new CrystallizerGui(tile, container, inventory);
        }

    };

    private int progress = 0;
    private ILiquidCrystalData crystalData;
    private LazyOptional<IFluidHandler> rclTank;
    private int tankCooldown = 0;

    public TileEntityCrystallizer() {
        super(MachinesModule.crystallizerConfig.powerMaximum.get(), MachinesModule.crystallizerConfig.powerPerTickIn.get(), new BasicItemHandler(1) {

            @Override
            public boolean canInsert(int slot, @Nonnull ItemStack stack) {
                return false;
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == CoreModule.RESONATING_CRYSTAL_ITEM.get();
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

        if (!canCrystallize()) {
            return;
        }

        energyHandler.consumeEnergy(MachinesModule.crystallizerConfig.powerPerTick.get());
        int rclPerCrystal = getRclPerCrystal();
        int drain = MachinesModule.crystallizerConfig.rclPerTick.get();
        if (crystalData != null) {
            drain = Math.min(drain, rclPerCrystal - crystalData.getAmount());
        }
        if (drain > 0) { //Config can change between ticks
            FluidStack stack = rclTank.orElseThrow(NullPointerException::new).drain(drain, IFluidHandler.FluidAction.EXECUTE);
            ILiquidCrystalData data = DeepResonanceFluidHelper.readCrystalDataFromStack(stack);
            if (data != null) {
                if (crystalData == null) {
                    crystalData = data;
                } else {
                    crystalData.merge(data);
                }
            }
        }
        if (crystalData != null && crystalData.getAmount() >= rclPerCrystal) {
            TileEntityResonatingCrystal crystal = new TileEntityResonatingCrystal();
            crystal.setEfficiency(crystalData.getEfficiency());
            crystal.setPurity(crystalData.getPurity());
            crystal.setStrength(crystalData.getStrength());
            crystal.setPower(100);
            crystalData = null;
            itemHandler.setStackInSlot(SLOT, CoreModule.RESONATING_CRYSTAL_BLOCK.get().createStack(crystal));
            markDirtyClient();
        }
        int newProgress = crystalData == null ? 0 : (int) ((crystalData.getAmount() / (float) getRclPerCrystal()) * 100);
        if (progress != newProgress) {
            CompoundNBT packet = new CompoundNBT();
            packet.putInt("progress", newProgress);
            ServerHelper.sendTileUpdatePackets(this, 3, packet);
            progress = newProgress;
        }
    }

    private boolean canCrystallize() {
        if (tankCooldown > 0) {
            tankCooldown--;
        }
        if ((rclTank == null || !rclTank.isPresent()) && !checkTank()) {
            return false;
        }

        if (energyHandler.getEnergyStored() < MachinesModule.crystallizerConfig.powerPerTick.get()) {
            return false;
        }

        if (hasCrystal()) {
            return false;
        }

        FluidStack fluidStack = rclTank.orElseThrow(NullPointerException::new).drain(MachinesModule.crystallizerConfig.rclPerTick.get(), IFluidHandler.FluidAction.SIMULATE);
        if (fluidStack.isEmpty() || fluidStack.getAmount() < 1) {
            return false;
        }

        return DeepResonanceFluidHelper.isValidLiquidCrystalStack(fluidStack);
    }

    private boolean checkTank() {
        rclTank = null;
        if (tankCooldown <= 0) {
            tankCooldown = 21;
            TileEntity tile = WorldHelper.getTileAt(getWorld(), pos.down());
            if (tile != null) {
                rclTank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
                return rclTank.isPresent();
            }
        }
        return false;
    }

    public boolean hasCrystal() {
        return !itemHandler.getStackInSlot(SLOT).isEmpty();
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        if (packet.getTileEntityType() == 3) {
            progress = packet.getNbtCompound().getInt("progress");
            return;
        }
        super.onDataPacket(net, packet);
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        if (crystalData != null) {
            CompoundNBT tag = new CompoundNBT();
            crystalData.toFluidStack().writeToNBT(tag);
            tagCompound.put("crystalData", tag);
        }
        return super.write(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        if (tagCompound.contains("crystalData")) {
            crystalData = DeepResonanceFluidHelper.readCrystalDataFromStack(FluidStack.loadFluidStackFromNBT(tagCompound.getCompound("crystalData")));
        } else {
            crystalData = null;
        }
        super.read(tagCompound);
    }

    private static int getRclPerCrystal() {
        return MachinesModule.crystallizerConfig.rclPerCrystal.get();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (!FMLHelper.getDist().isClient()) {
            throw new UnsupportedOperationException();
        }
        return new AxisAlignedBB(getPos().getX() - 10, getPos().getY() - 10, getPos().getZ() - 10, getPos().getX() + 10, getPos().getY() + 10, getPos().getZ() + 10);
    }

    public int getProgress() {
        if (!FMLHelper.getDist().isClient()) {
            throw new UnsupportedOperationException();
        }
        return progress;
    }

}
