package mcjty.deepresonance.tanks;

import elec332.core.grid.DefaultTileEntityLink;
import mcjty.deepresonance.blocks.tank.TileTank;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 9-8-2016.
 */
public class TankTileLink extends DefaultTileEntityLink {

    protected TankTileLink(TileTank tile) {
        super(tile);
    }

    private TankGrid grid;

    @Nullable
    public TankGrid getGrid(){
        return grid;
    }

    protected void setGrid(TankGrid grid){
        this.grid = grid;
        getTileEntity().setTank(grid);
    }

    @Nullable
    @Override
    public TileTank getTileEntity() {
        return (TileTank) super.getTileEntity();
    }

}
