package mcjty.deepresonance.modules.tank.grid;

import com.google.common.base.Preconditions;
import elec332.core.grid.DefaultTileEntityLink;
import mcjty.deepresonance.modules.tank.tile.TileEntityTank;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 7-1-2020
 */
class TankTileLink extends DefaultTileEntityLink {

    private TankGrid grid;

    TankTileLink(TileEntityTank tile) {
        super(tile);
    }

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
