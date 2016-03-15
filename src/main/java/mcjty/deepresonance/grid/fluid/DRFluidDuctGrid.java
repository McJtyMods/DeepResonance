package mcjty.deepresonance.grid.fluid;

import com.google.common.collect.Lists;
import elec332.core.grid.basic.AbstractCableGrid;
import elec332.core.main.ElecCore;
import elec332.core.world.WorldHelper;
import mcjty.deepresonance.DeepResonance;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidAcceptor;
import mcjty.deepresonance.api.fluid.IDeepResonanceFluidProvider;
import mcjty.deepresonance.blocks.duct.TileBasicFluidDuct;
import mcjty.deepresonance.grid.InternalGridTank;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRFluidDuctGrid extends AbstractCableGrid<DRFluidDuctGrid, DRFluidTile, DRGridTypeHelper, DRFluidWorldGridHolder> {

    public DRFluidDuctGrid(World world, DRFluidTile p, EnumFacing direction) {
        super(world, p, direction, DRGridTypeHelper.instance, DeepResonance.worldGridRegistry.getFluidRegistry());
        tank = new InternalGridTank(p.getTankStorage());
        tanks = Lists.newArrayList();
        if (p.getTile() instanceof TileBasicFluidDuct)
            tank.fill(((TileBasicFluidDuct) p.getTile()).intTank, true);
    }

    private InternalGridTank tank;
    private List<BlockPos> tanks;

    @Override
    protected void uponGridMerge(DRFluidDuctGrid grid) {
        super.uponGridMerge(grid);
        tank.merge(grid.tank);
        tanks.addAll(grid.tanks);
    }

    @Override
    public void onTick() {
        for (BlockPos loc : locations)
            ElecCore.systemPrintDebug(loc);
        processLiquids();
    }

    public void addTank(BlockPos tank){
        if (!tanks.contains(tank))
            tanks.add(tank);
    }

    public void removeTank(BlockPos tank){
        tanks.remove(tank);
    }

    private void processLiquids(){
        int requestedRCL = 0;
        int[] va = new int[acceptors.size()];
        //int[] vt = new int[tanks.size()];
        for (GridData gridData : providers) {
            int maxProvide = tank.getMaxAmount()-getStoredAmount();
            tank.fill(((IDeepResonanceFluidProvider) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).getProvidedFluid(maxProvide, gridData.getDirection()), true);
        }
        for (GridData gridData : acceptors) {
            int e = ((IDeepResonanceFluidAcceptor) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).getRequestedAmount(gridData.getDirection());
            va[acceptors.indexOf(gridData)] = e;
            requestedRCL += e;
        }
        for (BlockPos loc : tanks){
            int e = ((IDeepResonanceFluidAcceptor) WorldHelper.getTileAt(world, loc)).getRequestedAmount(null);
            va[tanks.indexOf(loc)] = e;
            requestedRCL += e;
        }
        if (getStoredAmount() >= requestedRCL){
            for (GridData gridData : acceptors)
                ((IDeepResonanceFluidAcceptor) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).acceptFluid(tank.drain(va[acceptors.indexOf(gridData)], true), gridData.getDirection());
            for (BlockPos loc : tanks)
                ((IDeepResonanceFluidAcceptor) WorldHelper.getTileAt(world, loc)).acceptFluid(tank.drain(va[tanks.indexOf(loc)], true), null);
        }else if (getStoredAmount() > 0){
            float diff = (float)getStoredAmount()/(float)requestedRCL;
            for (GridData gridData : acceptors)
                ((IDeepResonanceFluidAcceptor) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).acceptFluid(tank.drain((int) (va[acceptors.indexOf(gridData)] * diff), true), gridData.getDirection());
            for (BlockPos loc : tanks)
                ((IDeepResonanceFluidAcceptor) WorldHelper.getTileAt(world, loc)).acceptFluid(tank.drain((int) (va[tanks.indexOf(loc)] * diff), true), null);

        }
    }

    @Override
    protected void onTileRemoved(DRFluidTile tile) {
        super.onTileRemoved(tile);
        for (GridData gridData : transmitters){
            TileEntity tileEntity = WorldHelper.getTileAt(world, gridData.getLoc());
            if (tileEntity == null)
                tileEntity = tile.getTile();
            if (tileEntity instanceof TileBasicFluidDuct) {
                ((TileBasicFluidDuct) tileEntity).intTank = getFluidShare(tile.getTile());
                ((TileBasicFluidDuct) tileEntity).lastSeenFluid = tank.getStoredFluid();
            }
        }
        if (tile.getTile() instanceof TileBasicFluidDuct) {
            FluidStack stack = ((TileBasicFluidDuct) tile.getTile()).intTank;
            if (stack != null)
                tank.drain(stack.amount, true);
        }
    }

    public Fluid getFluid(){
        return tank.getStoredFluid();
    }

    public FluidStack getFluidShare(TileEntity tile){
        if (tile instanceof TileBasicFluidDuct){
            OptionalInt first = IntStream.range(0, transmitters.size())
                    .filter(i -> tile.getPos().equals(transmitters.get(i).getLoc()))
                    .findFirst();
            return tank.getShare(transmitters.size(), first.isPresent() ? first.getAsInt() == 0 : false);
        }
        return null;
    }

    public int addStackToInternalTank(FluidStack stack, boolean doFill){
        return tank.fill(stack, doFill);
    }

    public String getInfo(){
        return tank.getInfo();
    }

    public int getStoredAmount(){
        return tank.getStoredAmount();
    }

}
