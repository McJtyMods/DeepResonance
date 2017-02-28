package mcjty.deepresonance.varia;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 11-8-2016.
 */
public abstract class FluidTankWrapper implements IFluidHandler, IFluidTank {

    public static FluidTankWrapper of(final IFluidTank tank){
        return new FluidTankWrapper() {

            @Override
            protected IFluidTank getTank() {
                return tank;
            }

        };
    }

    public FluidTankWrapper(){
        final IFluidTankProperties prop = new Properties(this);
        this.properties = new IFluidTankProperties[]{
                prop
        };
    }
    private IFluidTankProperties[] properties;

    protected abstract IFluidTank getTank();

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return properties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!canFillFluidType(resource)) {
            return 0;
        }
        IFluidTank tank = getTank();
        // @todo check why this is sometimes needed
        if (tank == null) {
            return 0;
        }
        return tank.fill(resource, doFill);
    }


    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        IFluidTank tank = getTank();
        if (tank == null) {
            return null;
        }
        FluidStack f = tank.getFluid();
        if (!canDrainFluidType(f) || resource == null || f == null || !resource.isFluidEqual(f)) {
            return null;
        }
        return tank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        IFluidTank tank = getTank();
        if (tank == null) {
            return null;
        }
        if (!canDrainFluidType(tank.getFluid())) {
            return null;
        }
        return tank.drain(maxDrain, doDrain);
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        IFluidTank tank = getTank();
        if (tank == null) {
            return null;
        }
        FluidStack tankStack = tank.getFluid();
        return tankStack == null ? null : tankStack.copy();
    }

    @Override
    public int getFluidAmount() {
        IFluidTank tank = getTank();
        if (tank == null) {
            return 0;
        }
        return tank.getFluidAmount();
    }

    @Override
    public int getCapacity() {
        IFluidTank tank = getTank();
        if (tank == null) {
            return 0;
        }
        return tank.getCapacity();
    }

    @Override
    public FluidTankInfo getInfo() {
        IFluidTank tank = getTank();
        if (tank == null) {
            return null;
        }
        return tank.getInfo();
    }

    protected boolean canFill() {
        return true;
    }

    protected boolean canDrain() {
        return true;
    }

    protected boolean canFillFluidType(FluidStack fluidStack) {
        if (fluidStack == null){
            return false;
        }
        IFluidTank tank = getTank();
        if (tank == null) {
            return false;
        }
        FluidStack f = tank.getFluid();
        if (f == null){
            return canFillFluidTypeInternal(fluidStack);
        }
        return f.getFluid() == fluidStack.getFluid();
    }

    protected boolean canFillFluidTypeInternal(FluidStack fluidStack) {
        return canFill();
    }

    protected boolean canDrainFluidType(FluidStack fluidStack) {
        return fluidStack != null && canDrain();
    }

    private static class Properties implements IFluidTankProperties {

        private Properties(FluidTankWrapper tank){
            this.tank = tank;
        }

        private final FluidTankWrapper tank;

        @Nullable
        @Override
        public FluidStack getContents() {
            IFluidTank tank = this.tank.getTank();
            if (tank == null) {
                return null;
            }
            FluidStack stack = tank.getFluid();
            return stack == null ? null : stack.copy();
        }

        @Override
        public int getCapacity() {
            IFluidTank tank = this.tank.getTank();
            if (tank == null) {
                return 0;
            }
            return tank.getCapacity();
        }

        @Override
        public boolean canFill() {
            return tank.canFill();
        }

        @Override
        public boolean canDrain() {
            return tank.canDrain();
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack) {
            return tank.canFillFluidType(fluidStack);
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
            return tank.canDrainFluidType(fluidStack);
        }

    }

}
