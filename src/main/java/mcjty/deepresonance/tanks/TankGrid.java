package mcjty.deepresonance.tanks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.util.FluidTankWrapper;
import elec332.core.world.DimensionCoordinate;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.*;

import static mcjty.deepresonance.blocks.tank.TileTank.ID_SETFLUID;
import static mcjty.deepresonance.blocks.tank.TileTank.ID_SETHEIGHT;

/**
 * Created by Elec332 on 9-8-2016.
 */
public class TankGrid implements IFluidHandler, IFluidTank {

    @SuppressWarnings("all")
    public TankGrid(TankTileLink tank){
        this.tanks = Sets.newHashSet();
        this.tanks_ = Collections.unmodifiableSet(this.tanks);
        this.tanks.add(tank);
        this.tank = new InternalGridTank(TANK_BUCKETS * 1000);
        this.tank.fill(tank.getTileEntity().myTank, true);
        this.fluidTank = new FluidTankWrapper() {

            @Override
            protected IFluidTank getTank() {
                return TankGrid.this.tank;
            }

            @Override
            public int fill(FluidStack resource, boolean doFill) {
                return getTank().fill(resource, doFill);
            }

        };
        this.renderData = Maps.newHashMap();
        this.needsSorting = true;
    }

    public static final int TANK_BUCKETS = 16;

    private final Set<TankTileLink> tanks, tanks_;
    private Map<Integer, List<DimensionCoordinate>> renderData;
    private final InternalGridTank tank;
    private final FluidTankWrapper fluidTank;
    private boolean needsSorting;

    private Fluid lastSendFluid;
    private long lastHeightTime;
    private int i;

    public Set<TankTileLink> getConnections(){
        return tanks_;
    }

    public void tick(){
        if (i <= 0){
            setTankFluidHeights(false);
            if (lastSendFluid != tank.getStoredFluid()){
                markAllBlocksForUpdate();
            }
            setClientRenderFluid(true);
            i = 20;
        }
        i--;
    }

    protected boolean canMerge(TankGrid grid){
        if (grid == this){
            return false;
        }
        Fluid fluid = getStoredFluid(), otherFluid = grid.getStoredFluid();
        return fluid == otherFluid || fluid == null || otherFluid == null;
    }

    public void mergeWith(TankGrid grid){
        for (TankTileLink tank : grid.tanks_){
            tank.setGrid(this);
            tanks.add(tank);
        }
        tank.merge(grid.tank);
        needsSorting = true;
        setTankFluidHeights(false);
    }

    public void onRemoved(TankTileLink o){
        FluidStack stack = getFluidShare(o.getTileEntity());
        //stack = fluidTank.drain(stack, true);
        setDataToTile(o.getTileEntity(), stack);
        o.setGrid(null);
    }

    public void invalidate(){
        renderData.clear();
        tanks.clear();
    }

    private void setTankFluidHeights(boolean fillDrain){
        boolean sorted = false;
        if (needsSorting){
            renderData.clear();
            List<TankTileLink> listT = Lists.newArrayList(tanks);
            Collections.sort(listT, Comparator.comparingInt(o -> o.getPosition().getPos().getY()));
            for (TankTileLink loc : listT){
                BlockPos p = loc.getPosition().getPos();
                List<DimensionCoordinate> list = renderData.get(p.getY());
                if (list == null){
                    renderData.put(p.getY(), list = Lists.newArrayList());
                }
                list.add(loc.getPosition());
            }
            needsSorting = false;
            sorted = true;
        }
        long time = System.currentTimeMillis();
        if (sorted || fillDrain || time - lastHeightTime > 300) {
            int total = tank.getFluidAmount();
            List<Integer> list8776 = Lists.newArrayList(renderData.keySet());
            Collections.sort(list8776);
            for (Integer j : list8776) {
                List<DimensionCoordinate> list = renderData.get(j);
                float filled = 0.0f;
                if (total > 0) {
                    int i = list.size();
                    int toAdd = Math.min(total, i * TANK_BUCKETS * 1000);
                    total -= toAdd;
                    filled = (float) toAdd / (i * TANK_BUCKETS * 1000);
                }
                for (DimensionCoordinate loc : list) {
                    TileTank tank = getTank(loc);
                    if (tank != null) {
                        NBTTagCompound nbt = new NBTTagCompound();
                        nbt.setFloat("render", filled);
                        tank.sendPacket(ID_SETHEIGHT, nbt);
                    }
                }
            }
            lastHeightTime = time;
        }
    }

    public void setDataToTile(TileTank tile){
        setDataToTile(tile, getFluidShare(tile));
    }

    private void setDataToTile(TileTank tile, FluidStack share){
        NBTTagCompound tagCompound = new NBTTagCompound();
        Fluid lastSeenFluid = getStoredFluid();
        if (share != null) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            share.writeToNBT(fluidTag);
            tagCompound.setTag("fluid", fluidTag);
        }
        if (lastSeenFluid != null) {
            tagCompound.setString("lastSeenFluid", FluidRegistry.getFluidName(lastSeenFluid));
        }
        tile.setSaveData(tagCompound);
        if (WorldHelper.chunkLoaded(tile.getWorld(), tile.getPos())) {
            tile.markDirty();
        }
    }

    public int getComparatorInputOverride(){
        float f = (float)tank.getFluidAmount()/getCapacity();
        return (int)(f * 15);
    }

    private FluidStack getFluidShare(TileTank tile){
        return tank.getShare(tanks_.size(), tanks_.iterator().next().getPosition().equals(DimensionCoordinate.fromTileEntity(tile)));
    }

    public Fluid getStoredFluid(){
        FluidStack fI = getFluid();
        return fI == null ? null : fI.getFluid();
    }

    //Tank implementation

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return fluidTank.getTankProperties();
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        return fluidTank.getFluid();
    }

    @Override
    public int getFluidAmount() {
        return fluidTank.getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return fluidTank.getCapacity();
    }

    @Override
    public FluidTankInfo getInfo() {
        return fluidTank.getInfo();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int ret = fluidTank.fill(resource, doFill);
        if (doFill){
            setClientRenderFluid(false);
            setTankFluidHeights(true);
            markDirty();
        }
        return ret;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        FluidStack ret = fluidTank.drain(resource, doDrain);
        if (doDrain){
            setClientRenderFluid(false);
            setTankFluidHeights(true);
            markDirty();
        }
        return ret;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack ret = fluidTank.drain(maxDrain, doDrain);
        if (doDrain){
            setClientRenderFluid(false);
            setTankFluidHeights(true);
            markDirty();
        }
        return ret;
    }

    @SuppressWarnings("all")
    private void markAllBlocksForUpdate(){
        for (TankTileLink tank : tanks_){
            WorldHelper.markBlockForUpdate(tank.getTileEntity().getWorld(), tank.getPosition().getPos());
        }
    }

    private void markDirty(){
        for (TankTileLink tank : tanks_){
            markDirty(tank.getTileEntity());
        }
    }

    private void markDirty(TileTank tank){
        if (tank != null && WorldHelper.chunkLoaded(tank.getWorld(), tank.getPos())){
            tank.markDirty();
        }
    }

    private void setClientRenderFluid(boolean force){
        Fluid fluid = getStoredFluid();
        if (force || fluid != lastSendFluid) {
            for (TankTileLink tank : tanks_) {
                setClientRenderFluid(tank.getTileEntity(), fluid);
            }
            lastSendFluid = fluid;
        }
    }

    private void setClientRenderFluid(TileTank tank, Fluid fluid){
        if (tank != null) {
            tank.lastSeenFluid = getStoredFluid();
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setString("fluid", DRFluidRegistry.getFluidName(fluid));
            tank.sendPacket(ID_SETFLUID, nbt);
        }
    }

    @Nullable
    private TileTank getTank(DimensionCoordinate coordinate){
        if (coordinate.isLoaded()){
            TileEntity t = coordinate.getTileEntity();
            if (t instanceof TileTank){
                return (TileTank) t;
            }
        }
        return null;
    }

}
