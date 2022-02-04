package mcjty.deepresonance.modules.machines.block;

import mcjty.deepresonance.modules.core.CoreModule;
import mcjty.deepresonance.modules.core.block.ResonatingCrystalTileEntity;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.modules.machines.util.config.CrystallizerConfig;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.api.container.DefaultContainerProvider;
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
import mcjty.lib.varia.LevelTools;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.container.GenericItemHandler.no;
import static mcjty.lib.container.GenericItemHandler.yes;
import static mcjty.lib.container.SlotDefinition.generic;

public class CrystallizerTileEntity extends TickingTileEntity {

    public static final int SLOT = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(generic().out(), SLOT, 64, 24)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((integer, stack) -> stack.getItem() == CoreModule.RESONATING_CRYSTAL_GENERATED_ITEM.get())
            .insertable(no())
            .extractable(yes())
            .build();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, CrystallizerConfig.POWER_MAXIMUM.get(), CrystallizerConfig.POWER_PER_TICK_IN.get());

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Crystalizer")
            .containerSupplier(container(MachinesModule.CRYSTALIZER_CONTAINER, CONTAINER_FACTORY,this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage)
            .setupSync(this));

    private int progress = 0;
    private LiquidCrystalData crystalData;
    private LazyOptional<IFluidHandler> rclTank;
    private int tankCooldown = 0;

    public CrystallizerTileEntity() {
        super(MachinesModule.TYPE_CRYSTALIZER.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(CrystallizerTileEntity::new)
                .infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("crystallizer")))) {
            @Override
            public RotationType getRotationType() {
                return RotationType.HORIZROTATION;
            }
        };
    }

    @Override
    public void tickServer() {
        if (!canCrystallize()) {
            return;
        }

        energyStorage.consumeEnergy(CrystallizerConfig.POWER_PER_TICK.get());
        int rclPerCrystal = getRclPerCrystal();
        int drain = CrystallizerConfig.RCL_PER_TICK.get();
        if (crystalData != null) {
            drain = Math.min(drain, rclPerCrystal - crystalData.getAmount());
        }
        if (drain > 0) { //Config can change between ticks
            FluidStack stack = rclTank.orElseThrow(NullPointerException::new).drain(drain, IFluidHandler.FluidAction.EXECUTE);
            LiquidCrystalData data = DeepResonanceFluidHelper.readCrystalDataFromStack(stack);
            if (crystalData == null) {
                crystalData = data;
            } else {
                crystalData.merge(data);
            }
        }
        if (crystalData != null && crystalData.getAmount() >= rclPerCrystal) {
            ResonatingCrystalTileEntity crystal = new ResonatingCrystalTileEntity();
            crystal.setEfficiency(crystalData.getEfficiency() * 100f);
            crystal.setPurity(crystalData.getPurity() * 100f);
            crystal.setStrength(crystalData.getStrength() * 100f);
            crystal.setPower(100f);
            crystalData = null;
            items.setStackInSlot(SLOT, CoreModule.RESONATING_CRYSTAL_GENERATED.get().createStack(crystal));
            setChanged();
        }
        int newProgress = crystalData == null ? 0 : (int) ((crystalData.getAmount() / (float) getRclPerCrystal()) * 100);
        if (progress != newProgress) {
            CompoundNBT packet = new CompoundNBT();
            packet.putInt("progress", newProgress);
            LevelTools.getAllPlayersWatchingBlock(level, worldPosition).forEach(player -> player.connection.send(new SUpdateTileEntityPacket(worldPosition, 3, packet)));
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

        if (energyStorage.getEnergyStored() < CrystallizerConfig.POWER_PER_TICK.get()) {
            return false;
        }

        if (hasCrystal()) {
            return false;
        }

        FluidStack fluidStack = rclTank.orElseThrow(NullPointerException::new).drain(CrystallizerConfig.RCL_PER_TICK.get(), IFluidHandler.FluidAction.SIMULATE);
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
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        if (crystalData != null) {
            CompoundNBT tag = new CompoundNBT();
            crystalData.getFluidStack().writeToNBT(tag);
            tagCompound.put("crystalData", tag);
        }
        super.saveAdditional(tagCompound);
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        if (tagCompound.contains("crystalData")) {
            crystalData = DeepResonanceFluidHelper.readCrystalDataFromStack(FluidStack.loadFluidStackFromNBT(tagCompound.getCompound("crystalData")));
        } else {
            crystalData = null;
        }
        super.load(tagCompound);
    }

    private static int getRclPerCrystal() {
        return CrystallizerConfig.RCL_PER_CRYSTAL.get();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getBlockPos().getX() - 10, getBlockPos().getY() - 10, getBlockPos().getZ() - 10, getBlockPos().getX() + 10, getBlockPos().getY() + 10, getBlockPos().getZ() + 10);
    }

    // Client side
    public int getProgress() {
        return progress;
    }

}
