package mcjty.deepresonance.grid.tank;

import elec332.core.multiblock.dynamic.AbstractDynamicMultiBlockWorldHolder;
import mcjty.deepresonance.blocks.tank.TileTank;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

/**
 * Created by Elec332 on 10-8-2015.
 */
public class DRTankWorldHolder extends AbstractDynamicMultiBlockWorldHolder<DRTankWorldHolder, DRTankMultiBlock> {

    public DRTankWorldHolder(World world) {
        super(world);
    }

    @Override
    public boolean isTileValid(TileEntity tile) {
        return tile instanceof TileTank;
    }

    @Override
    public boolean canConnect(TileEntity main, ForgeDirection direction, TileEntity otherTile) {
        Fluid fluid1 = ((TileTank)main).lastSeenFluid;
        Fluid fluid2 = ((TileTank)otherTile).lastSeenFluid;
        return fluid1 == fluid2 || (fluid1 == null || fluid2 == null);
    }

    @Override
    public DRTankMultiBlock newMultiBlock(TileEntity tile) {
        return new DRTankMultiBlock(tile, this);
    }
}
