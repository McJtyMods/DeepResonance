package mcjty.deepresonance.grid.fluid;

import elec332.core.grid.basic.AbstractCableGrid;
import elec332.core.main.ElecCore;
import elec332.core.util.BlockLoc;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidAcceptor;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidProvider;
import mcjty.deepresonance.blocks.duct.TileBasicFluidDuct;
import mcjty.deepresonance.fluid.DRFluidRegistry;
import mcjty.deepresonance.fluid.LiquidCrystalFluidTagData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRFluidDuctGrid extends AbstractCableGrid<DRFluidDuctGrid, DRFluidTile, DRGridTypeHelper, DRFluidWorldGridHolder> {
    public DRFluidDuctGrid(World world, DRFluidTile p, ForgeDirection direction) {
        super(world, p, direction, DRGridTypeHelper.instance, DeepResonance.worldGridRegistry);
        tank = new InternalGridTank(p.getTankStorage());
        if (p.getTile() instanceof TileBasicFluidDuct)
            tank.add(((TileBasicFluidDuct) p.getTile()).intTank);
    }

    private InternalGridTank tank;

    @Override
    protected void uponGridMerge(DRFluidDuctGrid grid) {
        super.uponGridMerge(grid);
        tank.merge(grid.tank);
    }

    @Override
    public void onTick() {
        for (BlockLoc loc : locations)
            ElecCore.systemPrintDebug(loc);
        processLiquids();
    }

    private void processLiquids(){
        int requestedRCL = 0;
        int[] va = new int[acceptors.size()];
        for (GridData gridData : providers) {
            int maxProvide = tank.maxAmount-getStoredAmount();
            tank.add(((IDeepResonanceFluidProvider) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).getProvidedFluid(maxProvide, gridData.getDirection()));
        }
        for (GridData gridData : acceptors) {
            int e = ((IDeepResonanceFluidAcceptor) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).getRequestedAmount(gridData.getDirection());
            va[acceptors.indexOf(gridData)] = e;
            requestedRCL += e;
        }
        if (getStoredAmount() >= requestedRCL){
            for (GridData gridData : acceptors)
                ((IDeepResonanceFluidAcceptor) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).acceptFluid(tank.remove(va[acceptors.indexOf(gridData)]), gridData.getDirection());
        }else if (getStoredAmount() > 0){
            float diff = (float)getStoredAmount()/(float)requestedRCL;
            for (GridData gridData : acceptors)
                ((IDeepResonanceFluidAcceptor) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).acceptFluid(tank.remove((int)(va[acceptors.indexOf(gridData)] * diff)), gridData.getDirection());
        }
    }

    @Override
    protected void onTileRemoved(DRFluidTile tile) {
        super.onTileRemoved(tile);
        for (GridData gridData : transmitters){
            TileEntity tileEntity = WorldHelper.getTileAt(world, gridData.getLoc());
            if (tileEntity == null)
                tileEntity = tile.getTile();
            if (tileEntity instanceof TileBasicFluidDuct)
                ((TileBasicFluidDuct) tileEntity).intTank = getFluidShare(tile.getTile());
        }
        if (tile.getTile() instanceof TileBasicFluidDuct)
            tank.remove(((TileBasicFluidDuct)tile.getTile()).intTank.amount);
    }

    public FluidStack getFluidShare(TileEntity tile){
        if (tile instanceof TileBasicFluidDuct){
            return tank.getShare(transmitters.size());
        }
        return null;
    }

    public FluidStack addStackToInternalTank(FluidStack stack){
        if (!DRFluidRegistry.isValidLiquidCrystalStack(stack))
            return stack;
        return tank.add(stack);
    }

    public String getInfo(){
        return tank.tank.toString();
    }

    public int getStoredAmount(){
        return tank.getStoredAmount();
    }

    private static final class InternalGridTank{
        private InternalGridTank(int maxAmount){
            this.maxAmount = maxAmount;
            this.tank = LiquidCrystalFluidTagData.fromNBT(new NBTTagCompound(), 0);
        }

        private int maxAmount;
        private LiquidCrystalFluidTagData tank;

        public int getStoredAmount(){
            return tank.getInternalTankAmount();
        }

        public FluidStack add(FluidStack stack){
            if (!DRFluidRegistry.isValidLiquidCrystalStack(stack))
                return stack;
            FluidStack ret = null;
            int compare = tank.getInternalTankAmount() + stack.amount;
            FluidStack toAdd = stack.copy();
            if (compare > maxAmount){
                toAdd.amount = maxAmount - tank.getInternalTankAmount();
                ret = stack;
                ret.amount -= toAdd.amount;
            }
            tank.merge(LiquidCrystalFluidTagData.fromStack(toAdd));
            return ret;
        }

        public FluidStack remove(int toRemove){
            NBTTagCompound tag = new NBTTagCompound();
            tank.writeDataToNBT(tag);
            int stored = tank.getInternalTankAmount();
            if (toRemove > stored)
                toRemove = stored;
            tank.setInternalAmount(stored - toRemove);
            return new FluidStack(DRFluidRegistry.liquidCrystal, toRemove, tag);
        }

        private FluidStack getShare(int i){
            NBTTagCompound tag = new NBTTagCompound();
            tank.writeDataToNBT(tag);
            int ret = getStoredAmount()/i;
            return new FluidStack(DRFluidRegistry.liquidCrystal, ret, tag);
        }

        private void merge(InternalGridTank otherTank){
            maxAmount += otherTank.maxAmount;
            tank.merge(otherTank.tank);
        }

    }

}
