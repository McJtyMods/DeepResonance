package mcjty.deepresonance.grid;

import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Elec332 on 10-8-2015.
 */
public class InternalGridTank{

    public InternalGridTank(int maxAmount){
        this.maxAmount = maxAmount;
        this.tank = LiquidCrystalFluidTagData.fromNBT(new NBTTagCompound(), 0);
        this.extraTank = new FluidTank(maxAmount);
    }

    private int maxAmount;
    private LiquidCrystalFluidTagData tank;
    private FluidTank extraTank;

    public int getStoredAmount(){
        return tank.getInternalTankAmount() == 0 ? extraTank.getFluidAmount() : tank.getInternalTankAmount();
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public Fluid getStoredFluid(){
        FluidStack stored = getStoredFluidStack();
        return stored == null? null : getStoredFluidStack().getFluid();
    }

    public FluidStack getStoredFluidStack(){
        return tank.getInternalTankAmount() > 0 ? tank.getReferenceStack() : (extraTank.getFluid() == null ? null : extraTank.getFluid().copy());
    }

    public int fill(FluidStack stack, boolean doFill){
        if (DRFluidRegistry.isValidLiquidCrystalStack(stack)) {
            if (extraTank.getFluidAmount() > 0)
                return 0;
            int ret = stack.amount;
            int compare = tank.getInternalTankAmount() + stack.amount;
            FluidStack toAdd = stack.copy();
            if (compare > maxAmount) {
                toAdd.amount = maxAmount - tank.getInternalTankAmount();
                ret = toAdd.amount;
            }
            if (doFill)
                tank.merge(LiquidCrystalFluidTagData.fromStack(toAdd));
            return ret;
        } else {
            if (tank.getInternalTankAmount() > 0)
                return 0;
            return extraTank.fill(stack, doFill);
        }
    }

    public FluidStack drain(int toRemoveMax, boolean doDrain){
        if (tank.getInternalTankAmount() > 0) {
            NBTTagCompound tag = new NBTTagCompound();
            tank.writeDataToNBT(tag);
            int stored = tank.getInternalTankAmount();
            if (toRemoveMax > stored)
                toRemoveMax = stored;
            if (doDrain)
                tank.setInternalAmount(stored - toRemoveMax);
            return new FluidStack(DRFluidRegistry.liquidCrystal, toRemoveMax, tag);
        } else {
            return extraTank.drain(toRemoveMax, doDrain);
        }
    }

    public FluidStack getShare(int i){
        if (tank.getInternalTankAmount() > 0) {
            NBTTagCompound tag = new NBTTagCompound();
            tank.writeDataToNBT(tag);
            int ret = getStoredAmount() / i;
            return new FluidStack(DRFluidRegistry.liquidCrystal, ret, tag);
        } else if (extraTank.getFluidAmount() > 0){
            FluidStack ret = extraTank.getFluid().copy();
            ret.amount = extraTank.getFluidAmount() / i;
            return ret;
        }
        return null;
    }

    public void merge(InternalGridTank otherTank){
        maxAmount += otherTank.maxAmount;
        tank.merge(otherTank.tank);
        extraTank = new FluidTank(extraTank.getFluid(), maxAmount);
        extraTank.fill(otherTank.extraTank.getFluid(), true);
    }

    public String getInfo(){
        return getStoredFluid() == DRFluidRegistry.liquidCrystal ? tank.toString() : "Fluid: "+getStoredFluid()+" Amount: "+extraTank.getFluidAmount();
    }
}
