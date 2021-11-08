package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.util.LiquidCrystalData;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Supplier;

public class DRTankHandler implements IFluidHandler, IFluidTank {

    private final World level;
    private final Supplier<Integer> blobIdGetter;

    public DRTankHandler(World level, Supplier<Integer> blobIdGetter) {
        this.level = level;
        this.blobIdGetter = blobIdGetter;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    private Optional<LiquidCrystalData> getData() {
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(blobIdGetter.get());
        if (blob == null) {
            return Optional.empty();
        } else {
            return blob.getData();
        }
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return getData().map(LiquidCrystalData::toFluidStack).orElse(FluidStack.EMPTY);
    }

    @Override
    public int getTankCapacity(int tank) {
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(blobIdGetter.get());
        if (blob != null) {
            return blob.getCapacity();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(blobIdGetter.get());
        if (blob != null) {
            int filled = blob.fill(resource, action);
            if (filled > 0) {
                onUpdate();
            }
            return filled;
        } else {
            return 0;
        }
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(blobIdGetter.get());
        if (blob != null) {
            FluidStack drained = blob.drain(resource, action);
            if (!drained.isEmpty()) {
                onUpdate();
            }
            return drained;
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(blobIdGetter.get());
        if (blob != null) {
            FluidStack drained = blob.drain(maxDrain, action);
            if (!drained.isEmpty()) {
                onUpdate();
            }
            return drained;
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Nonnull
    @Override
    public FluidStack getFluid() {
        return getData().map(LiquidCrystalData::toFluidStack).orElse(FluidStack.EMPTY);
    }

    @Override
    public int getFluidAmount() {
        return getData().map(LiquidCrystalData::getAmount).orElse(0);
    }

    @Override
    public int getCapacity() {
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(blobIdGetter.get());
        if (blob != null) {
            return blob.getCapacity();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    public void onUpdate() {

    }
}
