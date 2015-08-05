package mcjty.deepresonance.grid.fluid;

import elec332.core.grid.basic.AbstractCableGrid;
import elec332.core.main.ElecCore;
import elec332.core.util.BlockLoc;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRFluidDuctGrid extends AbstractCableGrid<DRFluidDuctGrid, DRFluidTile, DRGridTypeHelper, DRFluidWorldGridHolder> {
    public DRFluidDuctGrid(World world, DRFluidTile p, ForgeDirection direction) {
        super(world, p, direction, DRGridTypeHelper.instance, DeepResonance.worldGridRegistry);
    }

    @Override
    public void onTick() {
        for (BlockLoc loc : locations)
            ElecCore.systemPrintDebug(loc);
    }

}
