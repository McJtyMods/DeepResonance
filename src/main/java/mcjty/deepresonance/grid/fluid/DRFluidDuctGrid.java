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
import mcjty.deepresonance.grid.InternalGridTank;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRFluidDuctGrid extends AbstractCableGrid<DRFluidDuctGrid, DRFluidTile, DRGridTypeHelper, DRFluidWorldGridHolder> {
    public DRFluidDuctGrid(World world, DRFluidTile p, ForgeDirection direction) {
        super(world, p, direction, DRGridTypeHelper.instance, DeepResonance.worldGridRegistry.getFluidRegistry());
        tank = new InternalGridTank(p.getTankStorage());
        if (p.getTile() instanceof TileBasicFluidDuct)
            tank.fill(((TileBasicFluidDuct) p.getTile()).intTank, true);
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
            int maxProvide = tank.getMaxAmount()-getStoredAmount();
            tank.fill(((IDeepResonanceFluidProvider) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).getProvidedFluid(maxProvide, gridData.getDirection()), true);
        }
        for (GridData gridData : acceptors) {
            int e = ((IDeepResonanceFluidAcceptor) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).getRequestedAmount(gridData.getDirection());
            va[acceptors.indexOf(gridData)] = e;
            requestedRCL += e;
        }
        if (getStoredAmount() >= requestedRCL){
            for (GridData gridData : acceptors)
                ((IDeepResonanceFluidAcceptor) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).acceptFluid(tank.drain(va[acceptors.indexOf(gridData)], true), gridData.getDirection());
        }else if (getStoredAmount() > 0){
            float diff = (float)getStoredAmount()/(float)requestedRCL;
            for (GridData gridData : acceptors)
                ((IDeepResonanceFluidAcceptor) getWorldHolder().getPowerTile(gridData.getLoc()).getTile()).acceptFluid(tank.drain((int) (va[acceptors.indexOf(gridData)] * diff), true), gridData.getDirection());
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
        if (tile.getTile() instanceof TileBasicFluidDuct)
            tank.drain(((TileBasicFluidDuct) tile.getTile()).intTank.amount, true);
    }

    public Fluid getFluid(){
        return tank.getStoredFluid();
    }

    public FluidStack getFluidShare(TileEntity tile){
        if (tile instanceof TileBasicFluidDuct){
            return tank.getShare(transmitters.size());
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
