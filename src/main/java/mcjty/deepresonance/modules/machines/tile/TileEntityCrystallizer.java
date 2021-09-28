package mcjty.deepresonance.modules.machines.tile;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.tile.TileEntityResonatingCrystal;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.WorldTools;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.generic;

public class TileEntityCrystallizer extends GenericTileEntity implements ITickableTileEntity {

    public static final int SLOT = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(generic().out(), CONTAINER_CONTAINER, SLOT, 64, 24)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Crystalizer")
            .containerSupplier((windowId,player) -> new GenericContainer(MachinesModule.CRYSTALIZER_CONTAINER.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), TileEntityCrystallizer.this))
            .itemHandler(() -> items));

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, false, MachinesModule.crystallizerConfig.powerMaximum.get(), 0);
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    private int progress = 0;
    private ILiquidCrystalData crystalData;
    private LazyOptional<IFluidHandler> rclTank;
    private int tankCooldown = 0;

    public TileEntityCrystallizer() {
        super(MachinesModule.TYPE_CRYSTALIZER.get());
    }

    @Override
    public void tick() {
        if (level.isClientSide()) {
            return;
        }

        if (!canCrystallize()) {
            return;
        }

        energyStorage.consumeEnergy(MachinesModule.crystallizerConfig.powerPerTick.get());
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
            items.setStackInSlot(SLOT, CoreModule.RESONATING_CRYSTAL_BLOCK.get().createStack(crystal));
            markDirtyClient();
        }
        int newProgress = crystalData == null ? 0 : (int) ((crystalData.getAmount() / (float) getRclPerCrystal()) * 100);
        if (progress != newProgress) {
            CompoundNBT packet = new CompoundNBT();
            packet.putInt("progress", newProgress);
            WorldTools.getAllPlayersWatchingBlock(level, worldPosition).forEach(player -> {
                player.connection.send(new SUpdateTileEntityPacket(worldPosition, 3, packet));
            });
            progress = newProgress;
        }
    }

    public int getMaxPower() {
        return energyStorage.getMaxEnergyStored();
    }

    public int getCurrentPower() {
        return energyStorage.getEnergyStored();
    }

    private boolean canCrystallize() {
        if (tankCooldown > 0) {
            tankCooldown--;
        }
        if ((rclTank == null || !rclTank.isPresent()) && !checkTank()) {
            return false;
        }

        if (energyStorage.getEnergyStored() < MachinesModule.crystallizerConfig.powerPerTick.get()) {
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
            TileEntity tile = level.getBlockEntity(worldPosition.below());
            if (tile != null) {
                rclTank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
                return rclTank.isPresent();
            }
        }
        return false;
    }

    public boolean hasCrystal() {
        return !items.getStackInSlot(SLOT).isEmpty();
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        if (packet.getType() == 3) {
            progress = packet.getTag().getInt("progress");
            return;
        }
        super.onDataPacket(net, packet);
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        if (crystalData != null) {
            CompoundNBT tag = new CompoundNBT();
            crystalData.toFluidStack().writeToNBT(tag);
            tagCompound.put("crystalData", tag);
        }
        return super.save(tagCompound);
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
        return new AxisAlignedBB(getBlockPos().getX() - 10, getBlockPos().getY() - 10, getBlockPos().getZ() - 10, getBlockPos().getX() + 10, getBlockPos().getY() + 10, getBlockPos().getZ() + 10);
    }

    // Client side
    public int getProgress() {
        return progress;
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == CoreModule.RESONATING_CRYSTAL_ITEM.get();
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return false;
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
