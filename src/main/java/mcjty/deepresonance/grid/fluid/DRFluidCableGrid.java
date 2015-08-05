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
public class DRFluidCableGrid extends AbstractCableGrid<DRFluidCableGrid, DRFluidTile, DRWiringTypeHelper, DRFluidWorldGridHolder> {
    public DRFluidCableGrid(World world, DRFluidTile p, ForgeDirection direction) {
        super(world, p, direction, DRWiringTypeHelper.instance, DeepResonance.worldGridRegistry);
    }

    @Override
    public void onTick() {
        for (BlockLoc loc : locations)
            ElecCore.systemPrintDebug(loc);
    }

}
