package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.util.LiquidCrystalData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

import static mcjty.deepresonance.util.Constants.TANK_BUCKETS;

public class DRTankHandler implements IFluidHandler, IFluidTank {

    private final TankTileEntity tank;

    public DRTankHandler(TankTileEntity tank) {
        this.tank = tank;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    private LiquidCrystalData getData() {
        Level level = tank.getLevel();
        if (level == null || level.isClientSide) {
            return tank.getClientLiquidData();
        }
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(tank.getMultiblockId());
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
    public int getTankCapacity(int tankNr) {
        Level level = tank.getLevel();
        if (level == null || level.isClientSide) {
            return TANK_BUCKETS * 1000;   // @todo not correct for configuration!
        }
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(tank.getMultiblockId());
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
        Level level = tank.getLevel();
        if (level == null || level.isClientSide) {
            return 0;
        }
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(tank.getMultiblockId());
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
        Level level = tank.getLevel();
        if (level == null || level.isClientSide) {
            return FluidStack.EMPTY;
        }
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(tank.getMultiblockId());
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
        Level level = tank.getLevel();
        if (level == null || level.isClientSide) {
            return FluidStack.EMPTY;
        }
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(tank.getMultiblockId());
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
        Level level = tank.getLevel();
        if (level == null || level.isClientSide) {
            return tank.getClientLiquidData().getFluidStack();
        }
        return getData().getFluidStack();
    }

    @Override
    public int getFluidAmount() {
        Level level = tank.getLevel();
        if (level == null || level.isClientSide) {
            return tank.getClientLiquidData().getFluidStack().getAmount();
        }
        return getData().getAmount();
    }

    @Override
    public int getCapacity() {
        Level level = tank.getLevel();
        if (level == null || level.isClientSide) {
            return TANK_BUCKETS * 1000;   // @todo not correct for configuration!
        }
        TankBlob blob = DRTankNetwork.getNetwork(level).getBlob(tank.getMultiblockId());
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
