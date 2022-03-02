package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.util.LiquidCrystalData;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class DRTankHandler implements IFluidHandler, IFluidTank {

    private final Level level;
    private final Supplier<Integer> blobIdGetter;
    private final Supplier<LiquidCrystalData> clientDataGetter;

    public DRTankHandler(Level level, Supplier<Integer> blobIdGetter, Supplier<LiquidCrystalData> clientDataGetter) {
        this.level = level;
        this.blobIdGetter = blobIdGetter;
        this.clientDataGetter = clientDataGetter;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    private LiquidCrystalData getData() {
        if (level.isClientSide) {
            return clientDataGetter.get();
        }
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(blobIdGetter.get());
        if (blob == null) {
            return LiquidCrystalData.EMPTY;
        } else {
            return blob.getData();
        }
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return getData().getFluidStack();
    }

    @Override
    public int getTankCapacity(int tank) {
        if (level.isClientSide) {
            return 10 * 1000;   // @todo not correct!
        }
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
        if (level.isClientSide) {
            return 0;
        }
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(blobIdGetter.get());
        if (blob != null) {
            int filled = blob.fill(resource, action);
            if (filled > 0 && action.execute()) {
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
        if (level.isClientSide) {
            return FluidStack.EMPTY;
        }
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(blobIdGetter.get());
        if (blob != null) {
            FluidStack drained = blob.drain(resource, action);
            if (!drained.isEmpty() && action.execute()) {
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
        if (level.isClientSide) {
            return FluidStack.EMPTY;
        }
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(blobIdGetter.get());
        if (blob != null) {
            FluidStack drained = blob.drain(maxDrain, action);
            if (!drained.isEmpty() && action.execute()) {
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
        if (level.isClientSide) {
            return clientDataGetter.get().getFluidStack();
        }
        return getData().getFluidStack();
    }

    @Override
    public int getFluidAmount() {
        if (level.isClientSide) {
            return clientDataGetter.get().getFluidStack().getAmount();
        }
        return getData().getAmount();
    }

    @Override
    public int getCapacity() {
        if (level.isClientSide) {
            return 10 * 1000;   // @todo not correct!
        }
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
