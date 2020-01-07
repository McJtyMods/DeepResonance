package mcjty.deepresonance.tanks;

import com.google.common.collect.Sets;
import elec332.core.util.FluidTankWrapper;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Elec332 on 7-1-2020
 */
public class TankGrid implements ICapabilityProvider {

    TankGrid(TankTileLink tank) {
        this.tanks = Sets.newHashSet();
        this.tanks_ = Collections.unmodifiableSet(this.tanks);
        this.tanks.add(tank);
        this.tank = new InternalGridTank(TANK_BUCKETS * 1000);
        this.fluidHandler = FluidTankWrapper.of(this.tank);
        this.capability = null;
        resetCapability();
    }

    public static final int TANK_BUCKETS = 16;

    private final InternalGridTank tank;
    private final IFluidHandler fluidHandler;
    private final Set<TankTileLink> tanks, tanks_;

    private LazyOptional<IFluidHandler> capability;

    public Set<TankTileLink> getComponents() {
        return tanks_;
    }

    public void onComponentRemoved(TankTileLink link) {
        link.setGrid(null);
        resetCapability();
    }

    public void tick() {

    }

    public void mergeWith(TankGrid other) {
        for (TankTileLink tank : other.getComponents()) {
            tank.setGrid(this);
            tanks.add(tank);
        }
        tank.merge(other.tank);
        resetCapability();
    }

    public boolean canMerge(TankGrid other) {
        if (other == this) {
            return false;
        }
        Fluid fluid = getStoredFluid(), otherFluid = other.getStoredFluid();
        return fluid == otherFluid || fluid == null || otherFluid == null;
    }

    public void invalidate() {
        tanks.clear();
        resetCapability();
    }

    public Fluid getStoredFluid() {
        return tank.getStoredFluid();
    }

    private void resetCapability() {
        if (this.capability != null) {
            this.capability.invalidate();
        }
        this.capability = LazyOptional.of(() -> fluidHandler);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.orEmpty(cap, capability);
    }

}
