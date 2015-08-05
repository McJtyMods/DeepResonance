package mcjty.deepresonance.grid.fluid;

import elec332.core.grid.basic.AbstractGridTile;
import mcjty.deepresonance.DeepResonance;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 3-8-2015.
 */
public class DRFluidTile extends AbstractGridTile<DRFluidCableGrid, DRFluidTile, DRWiringTypeHelper, DRFluidWorldGridHolder>{

    public DRFluidTile(TileEntity tileEntity) {
        super(tileEntity, DRWiringTypeHelper.instance, DeepResonance.worldGridRegistry);
    }

    @Override
    protected DRFluidCableGrid newGrid(ForgeDirection direction) {
        return new DRFluidCableGrid(getTile().getWorldObj(), this, direction);
    }
}
