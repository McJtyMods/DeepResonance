package mcjty.deepresonance.grid.tank;

import elec332.core.multiblock.dynamic.AbstractDynamicMultiBlock;
import elec332.core.util.BlockLoc;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.grid.InternalGridTank;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * Created by Elec332 on 10-8-2015.
 */
public class DRTankMultiBlock extends AbstractDynamicMultiBlock<DRTankWorldHolder, DRTankMultiBlock> implements IFluidHandler, IFluidTank{

    public DRTankMultiBlock(TileEntity tile, DRTankWorldHolder worldHolder) {
        super(tile, worldHolder);
        this.tank = new InternalGridTank(9 * FluidContainerRegistry.BUCKET_VOLUME);
        if (tile instanceof TileTank){
            tank.fill(((TileTank) tile).myTank, true);
        }
    }

    private InternalGridTank tank;

    @Override
    public void tick() {
    }

    @Override
    protected void mergeWith(DRTankMultiBlock multiBlock) {
        super.mergeWith(multiBlock);
        tank.merge(multiBlock.tank);
    }

    public int getTankSize(){
        return allLocations.size();
    }

    public FluidStack getFluidShare(TileTank tile){
        return tank.getShare(allLocations.size());
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
        return null;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    public String getTankInfo(){
        return tank.getInfo();
    }

    public int getFreeSpace(){
        return getCapacity() - getFluidAmount();
    }

    private TileTank getTank(BlockLoc loc){
        return (TileTank) WorldHelper.getTileAt(world, loc);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(tank.getStoredFluidStack())) {
            return null;
        }
        return drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[0];
    }
}
