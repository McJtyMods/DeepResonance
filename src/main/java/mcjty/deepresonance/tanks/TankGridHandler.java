package mcjty.deepresonance.tanks;

import com.google.common.collect.Sets;
import elec332.core.grid.v2.AbstractGridHandler;
import elec332.core.world.DimensionCoordinate;
import mcjty.deepresonance.blocks.tank.TileTank;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

/**
 * Created by Elec332 on 9-8-2016.
 */
public class TankGridHandler extends AbstractGridHandler<TankTileLink> {

    public TankGridHandler(){
        this.grids = Sets.newHashSet();
    }

    private final Set<TankGrid> grids;

    @Override
    protected void onObjectRemoved(TankTileLink o, Set<DimensionCoordinate> set) {
        DimensionCoordinate coord = o.getPosition();
        TankGrid grid = o.getGrid();
        if (grid == null) {
            removeObject(coord);
            return;
        }
        for (TankTileLink o2 : grid.getConnections()) {
            grid.onRemoved(o2);
            if (!set.contains(o2.getPosition()) && o != o2) {
                add.add(o2.getPosition());
            }
        }
        grid.invalidate();
        grids.remove(grid);
    }

    @Override
    @SuppressWarnings("all")
    protected void internalAdd(TankTileLink o) {
        DimensionCoordinate coord = o.getPosition();
        BlockPos pos = coord.getPos();
        o.setGrid(newGrid(o));
        for (EnumFacing facing : EnumFacing.VALUES){
            TankTileLink ttl = getDim(coord).get(pos.offset(facing));
            if (ttl != null){
                TankGrid grid = ttl.getGrid();
                if (grid != null && o.getGrid().canMerge(grid)){
                    o.getGrid().mergeWith(grid);
                    grid.invalidate();
                    grids.remove(grid);
                }
            }
        }
    }

    @Override
    public void tick() {
        for (TankGrid grid : grids){
            grid.tick();
        }
    }

    @Override
    public boolean isValidObject(TileEntity tileEntity) {
        return tileEntity instanceof TileTank;
    }

    @Override
    protected TankTileLink createNewObject(TileEntity tileEntity) {
        return new TankTileLink((TileTank) tileEntity);
    }

    private TankGrid newGrid(TankTileLink tank){
        TankGrid ret = new TankGrid(tank);
        grids.add(ret);
        return ret;
    }

}
