package mcjty.deepresonance.grid.tank;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.multiblock.dynamic.AbstractDynamicMultiBlock;
import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.grid.InternalGridTank;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 10-8-2015.
 */
@SuppressWarnings("unused")
public class DRTankMultiBlock extends AbstractDynamicMultiBlock<DRTankWorldHolder, DRTankMultiBlock> implements IFluidHandler, IFluidTank{

    public static final int TANK_BUCKETS = 16;

    public DRTankMultiBlock(TileEntity tile, DRTankWorldHolder worldHolder) {
        super(tile, worldHolder);
        this.tank = new InternalGridTank(TANK_BUCKETS * FluidContainerRegistry.BUCKET_VOLUME);
        this.renderData = Maps.newHashMap();
        if (tile instanceof TileTank && ((TileTank)tile).getSaveData() != null){
            tank.fill(FluidStack.loadFluidStackFromNBT(((TileTank)tile).getSaveData().getCompoundTag("fluid")), true);
        }
        needsSorting = true;
        setClientRenderFluid();
        markAllBlocksForUpdate();
    }

    private boolean needsSorting;
    private InternalGridTank tank;
    private Map<Integer, List<BlockPos>> renderData;
    private Fluid check;

    @Override
    public void tick() {
        if (world.getTotalWorldTime() % 20L == 0L){
            setTankFluidHeights();
            setClientRenderFluid();
            if (check != tank.getStoredFluid()){
                markAllBlocksForUpdate();
                check = tank.getStoredFluid();
            }
        }
    }

    @Override
    protected void invalidate() {
        super.invalidate();
        for (BlockPos loc : Lists.newArrayList(allLocations)){
            TileTank tank = getTank(loc);
            if (tank != null) {
                setDataToTile(tank);
            }
        }
    }

    @Override
    protected void mergeWith(DRTankMultiBlock multiBlock) {
        super.mergeWith(multiBlock);
        tank.merge(multiBlock.tank);
        setClientRenderFluid();
        needsSorting = true;
        setTankFluidHeights();
        markEverythingDirty();
        markAllBlocksForUpdate();
    }

    public void setDataToTile(TileTank tile){
        NBTTagCompound tagCompound = new NBTTagCompound();
        FluidStack myTank = getFluidShare(tile);
        Fluid lastSeenFluid = getStoredFluid();
        if (myTank != null) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            myTank.writeToNBT(fluidTag);
            tagCompound.setTag("fluid", fluidTag);
        }
        if (lastSeenFluid != null) {
            tagCompound.setString("lastSeenFluid", FluidRegistry.getFluidName(lastSeenFluid));
        }
        tile.setSaveData(tagCompound);
        if (WorldHelper.chunkLoaded(world, tile.getPos())) {
            tile.markDirty();
        }
    }

    public int getComparatorInputOverride(){
        float f = (float)tank.getStoredAmount()/getCapacity();
        return (int)(f * 15);
    }

    public void markEverythingDirty(){
        for (BlockPos loc : allLocations){
            TileTank tank = getTank(loc);
            if (tank != null) {
                tank.markDirty();
            }
        }
    }

    public void markAllBlocksForUpdate(){
        allLocations.forEach(world::markBlockForUpdate);
    }

    private void setTankFluidHeights(){
        if (needsSorting){
            renderData.clear();
            Collections.sort(allLocations, new Comparator<BlockPos>() {
                @Override
                public int compare(BlockPos o1, BlockPos o2) {
                    return o1.getY() - o2.getY();
                }
            });
            for (BlockPos loc : allLocations){
                List<BlockPos> list = renderData.get(loc.getY());
                if (list == null){
                    renderData.put(loc.getY(), list = Lists.newArrayList());
                }
                list.add(loc);
            }
            needsSorting = false;
        }
        int total = tank.getStoredAmount();
        List<Integer> list8776 = Lists.newArrayList(renderData.keySet());
        Collections.sort(list8776);
        for (Integer j : list8776){
            List<BlockPos> list = renderData.get(j);
            float filled = 0.0f;
            if (total > 0) {
                int i = list.size();
                int toAdd = Math.min(total, i * TANK_BUCKETS * FluidContainerRegistry.BUCKET_VOLUME);
                total -= toAdd;
                filled = (float) toAdd / (i * TANK_BUCKETS * FluidContainerRegistry.BUCKET_VOLUME);
            }
            for (BlockPos loc : list) {
                TileTank tank = getTank(loc);
                if (tank != null) {
                    tank.sendPacket(3, new NBTHelper().addToTag(filled, "render").serializeNBT());
                }
            }
        }
    }

    public FluidStack getFluidShare(TileTank tile){
        return tank.getShare(allLocations.size(), allLocations.indexOf(tile.getPos()) == 0);
    }

    public Fluid getStoredFluid(){
        return tank.getStoredFluid();
    }

    @Override
    public FluidStack getFluid() {
        return tank.getStoredFluidStack();
    }

    @Override
    public int getFluidAmount() {
        return tank.getStoredAmount();
    }

    @Override
    public int getCapacity() {
        return tank.getMaxAmount();
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(getFluid(), getCapacity());
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int ret = tank.fill(resource, doFill);
        if (doFill) {
            setClientRenderFluid();
            setTankFluidHeights();
        }
        return ret;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack ret = tank.drain(maxDrain, doDrain);
        if (doDrain)
            setTankFluidHeights();
        return ret;
    }

    public String getTankInfo(){
        return tank.getInfo();
    }

    public int getFreeSpace(){
        return getCapacity() - getFluidAmount();
    }

    private TileTank getTank(BlockPos loc){
        TileEntity tile = WorldHelper.chunkLoaded(world, loc) ? WorldHelper.getTileAt(world, loc) : null;
        return tile instanceof TileTank ? (TileTank) tile : null;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        int ret = fill(resource, doFill);
        if (doFill) {
            setClientRenderFluid();
            setTankFluidHeights();
            markEverythingDirty();
        }
        return ret;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(tank.getStoredFluidStack())) {
            return null;
        }
        FluidStack ret = drain(resource.amount, doDrain);
        if (doDrain) {
            setTankFluidHeights();
            markEverythingDirty();
        }
        return ret;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        FluidStack ret = drain(maxDrain, doDrain);
        if (doDrain) {
            setTankFluidHeights();
            markEverythingDirty();
        }
        return ret;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[] {
            getInfo()
        };
    }

    private void setClientRenderFluid() {
        markEverythingDirty();
        for (BlockPos loc : allLocations) {
            TileTank tank = getTank(loc);
            if (tank != null) {
                tank.lastSeenFluid = getStoredFluid();
                tank.sendPacket(1, new NBTHelper().addToTag(DRFluidRegistry.getFluidName(getStoredFluid()), "fluid").serializeNBT());
            }
        }
    }

}
