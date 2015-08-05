package mcjty.deepresonance.grid.fluid;

import elec332.core.grid.basic.AbstractWorldGridHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRFluidWorldGridHolder extends AbstractWorldGridHolder<DRFluidWorldGridHolder, DRFluidDuctGrid, DRFluidTile, DRGridTypeHelper> {

    public DRFluidWorldGridHolder(World world) {
        super(world, DRGridTypeHelper.instance);
    }

    @Override
    protected DRFluidTile newGridTile(TileEntity tile) {
        return new DRFluidTile(tile);
    }
}
