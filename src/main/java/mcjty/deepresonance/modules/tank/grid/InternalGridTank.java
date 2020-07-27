package mcjty.deepresonance.modules.tank.grid;

import com.google.common.base.Preconditions;
import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.fluids.LiquidCrystalData;
import mcjty.deepresonance.setup.FluidRegister;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 7-1-2020
 */
public class InternalGridTank implements IFluidTank {

    private final ILiquidCrystalData tank;
    private int maxAmount;
    private FluidTank extraTank;

    public InternalGridTank(int maxAmount) {
        this.maxAmount = maxAmount;
        this.tank = Preconditions.checkNotNull(LiquidCrystalData.fromNBT(new CompoundNBT(), 0));
        this.extraTank = new FluidTank(maxAmount);
    }

    public Fluid getStoredFluid() {
        FluidStack stored = getFluid();
        return stored.isEmpty() ? null : stored.getFluid();
    }

    @Override
    public int fill(FluidStack stack, IFluidHandler.FluidAction action) {
        if (FluidRegister.isValidLiquidCrystalStack(stack)) {
            if (extraTank.getFluidAmount() > 0) {
                return 0;
            }
            int ret = stack.getAmount();
            int compare = tank.getAmount() + stack.getAmount();
            FluidStack toAdd = stack.copy();
            if (compare > maxAmount) {
                toAdd.setAmount(maxAmount - tank.getAmount());
                ret = toAdd.getAmount();
            }
            if (action.execute()) {
                tank.merge(Preconditions.checkNotNull(LiquidCrystalData.fromStack(toAdd)));
            }
            return ret;
        } else {
            if (tank.getAmount() > 0) {
                return 0;
            }
            return extraTank.fill(stack, action);
        }
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(getFluid())) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (tank.getAmount() > 0) {
            int stored = tank.getAmount();
            if (maxDrain > stored) {
                maxDrain = stored;
            }
            if (action.execute()) {
                tank.setAmount(stored - maxDrain);
            }
            return tank.toFluidStack(maxDrain);
        } else {
            return extraTank.drain(maxDrain, action);
        }
    }

    public FluidStack getShare(int i, boolean first) {
        if (tank.getAmount() > 0) {
            int ret = getFluidAmount() / i;
            if (first) {
                ret += getFluidAmount() % i;
            }
            return tank.toFluidStack(ret);
        } else if (extraTank.getFluidAmount() > 0) {
            FluidStack ret = extraTank.getFluid().copy();
            ret.setAmount(extraTank.getFluidAmount() / i);
            if (first) {
                ret.setAmount(ret.getAmount() + extraTank.getFluidAmount() % i);
            }
            return ret;
        }
        return null;
    }

    public void merge(InternalGridTank otherTank) {
        maxAmount += otherTank.maxAmount;
        tank.merge(otherTank.tank);
        FluidStack internal = extraTank.getFluid();
        extraTank = new FluidTank(maxAmount);
        extraTank.setFluid(internal);
        extraTank.fill(otherTank.extraTank.getFluid(), IFluidHandler.FluidAction.EXECUTE);
    }

    @Nonnull
    @Override
    public FluidStack getFluid() {
        return tank.getAmount() > 0 ? tank.toFluidStack() : (extraTank.getFluid().isEmpty() ? FluidStack.EMPTY : extraTank.getFluid().copy());
    }

    @Override
    public int getFluidAmount() {
        return tank.getAmount() == 0 ? extraTank.getFluidAmount() : tank.getAmount();
    }

    @Override
    public int getCapacity() {
        return maxAmount;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    @Override
    public String toString() {
        return getStoredFluid() == FluidRegister.liquidCrystal ? tank.toString() : "Fluid: " + getStoredFluid() + " Amount: " + extraTank.getFluidAmount();
    }

}