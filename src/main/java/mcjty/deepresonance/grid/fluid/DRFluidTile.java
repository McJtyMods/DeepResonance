package mcjty.deepresonance.grid.fluid;

import elec332.core.grid.basic.AbstractGridTile;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRFluidTile extends AbstractGridTile<DRFluidDuctGrid, DRFluidTile, DRGridTypeHelper, DRFluidWorldGridHolder>{

    public DRFluidTile(TileEntity tileEntity) {
        super(tileEntity, DRGridTypeHelper.instance, DeepResonance.worldGridRegistry);
    }

    @Override
    protected DRFluidDuctGrid newGrid(ForgeDirection direction) {
        return new DRFluidDuctGrid(getTile().getWorldObj(), this, direction);
    }
}
