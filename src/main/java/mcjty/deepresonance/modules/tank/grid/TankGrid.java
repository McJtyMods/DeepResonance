package mcjty.deepresonance.modules.tank.grid;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.core.util.FluidTankWrapper;
import elec332.core.world.DimensionCoordinate;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.modules.tank.tile.TileEntityTank;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Elec332 on 7-1-2020
 */
@SuppressWarnings("WeakerAccess")
public class TankGrid implements ICapabilityProvider, IFluidHandler {

    public static final int UPDATE_INTERVAL = 20;
    public static final int TANK_BUCKETS = 16;
    private final InternalGridTank tank;
    private final IFluidHandler fluidHandler;
    private final Set<TankTileLink> tanks, tanks_;
    private final TankRenderHandler renderHandler;
    private LazyOptional<IFluidHandler> capability;
    private int counter;

    TankGrid(TankTileLink tank) {
        this.tanks = Sets.newHashSet();
        this.tanks_ = Collections.unmodifiableSet(this.tanks);
        this.tanks.add(tank);
        this.tank = new InternalGridTank(TANK_BUCKETS * 1000);
        this.fluidHandler = FluidTankWrapper.of(this.tank);
        this.capability = null;
        CompoundNBT tag = Preconditions.checkNotNull(tank.getTileEntity()).getGridData();
        if (tag.contains("fluid")) {
            CompoundNBT fluid = tag.getCompound("fluid");
            this.tank.fill(FluidStack.loadFluidStackFromNBT(fluid), FluidAction.EXECUTE);
        }
        resetCapability();
        this.renderHandler = new TankRenderHandler(this);
        this.renderHandler.needsResort();
    }

    public Set<TankTileLink> getComponents() {
        return tanks_;
    }

    public void onComponentRemoved(TankTileLink link) {
        FluidStack stack = getFluidShare(link.getTileEntity());
        setDataToTile(link.getTileEntity(), stack);
        link.setGrid(null);
        resetCapability();
    }

    public void tick() {
        if (counter <= 0) {
            renderHandler.checkForChanges();
            counter = UPDATE_INTERVAL;
        }
        counter--;
    }

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    public void mergeWith(TankGrid other) {
        for (TankTileLink tank : other.getComponents()) {
            tank.setGrid(this);
            tanks.add(tank);
        }
        tank.merge(other.tank);
        renderHandler.needsResort();
        renderHandler.checkForChanges();
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
        this.capability = LazyOptional.of(() -> this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.orEmpty(cap, capability);
    }

    @Override
    public int getTanks() {
        return fluidHandler.getTanks();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return fluidHandler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return fluidHandler.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return fluidHandler.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int ret = fluidHandler.fill(resource, action);
        if (action.execute()) {
            renderHandler.onFluidContentsChanged();
            markDirty();
        }
        return ret;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack ret = fluidHandler.drain(resource, action);
        if (action.execute()) {
            renderHandler.onFluidContentsChanged();
            markDirty();
        }
        return ret;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack ret = fluidHandler.drain(maxDrain, action);
        if (action.execute()) {
            renderHandler.onFluidContentsChanged();
            markDirty();
        }
        return ret;
    }

    public void setDataToTile(TileEntityTank tile) {
        setDataToTile(tile, getFluidShare(tile));
    }

    private void setDataToTile(TileEntityTank tile, FluidStack share) {
        CompoundNBT tagCompound = new CompoundNBT();
        if (share != null) {
            CompoundNBT fluidTag = new CompoundNBT();
            share.writeToNBT(fluidTag);
            tagCompound.put("fluid", fluidTag);
        }
        tile.setGridData(tagCompound);
        if (WorldHelper.chunkLoaded(Preconditions.checkNotNull(tile.getWorld()), tile.getPos())) {
            tile.markDirty();
        }
    }

    private FluidStack getFluidShare(TileEntityTank tile) {
        return tank.getShare(tanks_.size(), tanks_.iterator().next().getPosition().equals(DimensionCoordinate.fromTileEntity(tile)));
    }

    private void markDirty() {
        for (TankTileLink tank : tanks_) {
            markDirty(tank.getTileEntity());
        }
    }

    private void markDirty(TileEntityTank tank) {
        if (tank != null && WorldHelper.chunkLoaded(Preconditions.checkNotNull(tank.getWorld()), tank.getPos())) {
            tank.markDirty();
        }
    }

}
