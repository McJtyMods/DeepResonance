package mcjty.deepresonance.tanks;

import com.google.common.base.Preconditions;
import elec332.core.grid.DefaultTileEntityLink;
import mcjty.deepresonance.tile.TileEntityTank;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 7-1-2020
 */
class TankTileLink extends DefaultTileEntityLink {

    TankTileLink(TileEntityTank tile) {
        super(tile);
    }

    private TankGrid grid;

    @Nullable
    TankGrid getGrid() {
        return grid;
    }

    void setGrid(TankGrid grid) {
        this.grid = grid;
        Preconditions.checkNotNull(getTileEntity()).setGrid(grid);
    }

    @Nullable
    @Override
    public TileEntityTank getTileEntity() {
        return (TileEntityTank) super.getTileEntity();
    }

}
