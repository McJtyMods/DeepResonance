package mcjty.deepresonance.modules.tank.grid;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.core.grid.AbstractGridHandler;
import elec332.core.world.DimensionCoordinate;
import mcjty.deepresonance.modules.tank.tile.TileEntityTank;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

/**
 * Created by Elec332 on 7-1-2020
 */
public class TankGridHandler extends AbstractGridHandler<TankTileLink> {

    private final Set<TankGrid> grids;

    public TankGridHandler() {
        this.grids = Sets.newHashSet();
    }

    @Override
    protected void onObjectRemoved(TankTileLink o, Set<DimensionCoordinate> set) {
        DimensionCoordinate coord = o.getPosition();
        TankGrid grid = o.getGrid();
        if (grid == null) {
            removeObject(coord);
            return;
        }
        for (TankTileLink o2 : grid.getComponents()) {
            grid.onComponentRemoved(o2);
            if (!set.contains(o2.getPosition()) && o != o2) {
                add.add(o2.getPosition());
            }
        }
        grid.invalidate();
        grids.remove(grid);
    }

    @Override
    protected void internalAdd(TankTileLink o) {
        DimensionCoordinate coord = o.getPosition();
        BlockPos pos = coord.getPos();
        o.setGrid(newGrid(o));
        for (Direction facing : Direction.values()) {
            TankTileLink ttl = getDim(coord).get(pos.offset(facing));
            if (ttl != null) {
                TankGrid grid = ttl.getGrid();
                if (grid != null && Preconditions.checkNotNull(o.getGrid()).canMerge(grid)) {
                    o.getGrid().mergeWith(grid);
                    grid.invalidate();
                    grids.remove(grid);
                }
            }
        }
    }

    @Override
    public void tick() {
        grids.forEach(TankGrid::tick);
    }

    @Override
    public boolean isValidObject(TileEntity tileEntity) {
        return tileEntity instanceof TileEntityTank;
    }

    @Override
    protected TankTileLink createNewObject(TileEntity tileEntity) {
        return new TankTileLink((TileEntityTank) tileEntity);
    }

    private TankGrid newGrid(TankTileLink tank) {
        TankGrid ret = new TankGrid(tank);
        grids.add(ret);
        return ret;
    }

}
