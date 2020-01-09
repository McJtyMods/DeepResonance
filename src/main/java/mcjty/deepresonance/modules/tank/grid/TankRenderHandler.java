package mcjty.deepresonance.modules.tank.grid;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.world.DimensionCoordinate;
import mcjty.deepresonance.modules.tank.tile.TileEntityTank;
import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;

import static mcjty.deepresonance.modules.tank.grid.TankGrid.TANK_BUCKETS;

/**
 * Created by Elec332 on 9-1-2020
 * <p>
 * Created to un-clutter the main TankGrid class
 */
@SuppressWarnings("WeakerAccess")
public class TankRenderHandler {

    private final TankGrid grid;
    private final Map<Integer, List<DimensionCoordinate>> renderData;
    private Fluid lastSendFluid;
    private boolean needsSorting;
    private long lastHeightTime;

    public TankRenderHandler(TankGrid grid) {
        this.grid = grid;
        this.renderData = Maps.newHashMap();
    }

    public void onFluidContentsChanged() {
        setTankFluidHeights(true);
    }

    public void checkForChanges() {
        setTankFluidHeights(false);
        if (lastSendFluid != grid.getStoredFluid()) {
            lastSendFluid = grid.getStoredFluid();
            grid.getComponents().forEach(ttl -> Optional.ofNullable(ttl.getTileEntity()).ifPresent(tile -> tile.setClientData(-1, lastSendFluid)));
        }
    }

    public void needsResort() {
        needsSorting = true;
    }

    private void setTankFluidHeights(boolean fillDrain) {
        boolean sorted = false;
        if (needsSorting) {
            renderData.clear();
            List<TankTileLink> listT = Lists.newArrayList(grid.getComponents());
            listT.sort(Comparator.comparingInt(o -> o.getPosition().getPos().getY()));
            for (TankTileLink loc : listT) {
                BlockPos p = loc.getPosition().getPos();
                List<DimensionCoordinate> list = renderData.computeIfAbsent(p.getY(), k -> Lists.newArrayList());
                list.add(loc.getPosition());
            }
            needsSorting = false;
            sorted = true;
        }
        long time = System.currentTimeMillis();
        if (sorted || fillDrain || time - lastHeightTime > 300) {
            int total = grid.getFluidAmount();
            List<Integer> list8776 = Lists.newArrayList(renderData.keySet());
            Collections.sort(list8776);
            lastSendFluid = grid.getStoredFluid();
            for (Integer j : list8776) {
                List<DimensionCoordinate> list = renderData.get(j);
                float filled = 0.0f;
                if (total > 0) {
                    int i = list.size();
                    int toAdd = Math.min(total, i * TANK_BUCKETS * 1000);
                    total -= toAdd;
                    filled = (float) toAdd / (i * TANK_BUCKETS * 1000);
                }
                for (DimensionCoordinate loc : list) {
                    TileEntityTank tank = getTank(loc);
                    if (tank != null) {
                        tank.setClientData(filled, lastSendFluid);
                    }
                }
            }
            lastHeightTime = time;
        }
    }

    @Nullable
    private TileEntityTank getTank(DimensionCoordinate coordinate) {
        if (coordinate.isLoaded()) {
            TileEntity t = coordinate.getTileEntity();
            if (t instanceof TileEntityTank) {
                return (TileEntityTank) t;
            }
        }
        return null;
    }

}
