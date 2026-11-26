package mcjty.deepresonance.modules.tank.data;

import mcjty.lib.varia.FluidStack;
import static mcjty.deepresonance.modules.tank.util.TankTransfers.DROPLETS_PER_MB;
import static mcjty.deepresonance.modules.tank.util.TankTransfers.millibuckets;
import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.util.Constants;
import mcjty.deepresonance.util.LiquidCrystalData;
import net.minecraft.world.level.Level;
import mcjty.lib.container.IndexedStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public final class DRTankHandler implements IndexedStorage<FluidVariant> {

    private final TankTileEntity tank;

    public DRTankHandler(TankTileEntity tank) {
        this.tank = tank;
    }

    private TankBlob getBlob() {
        Level level = tank.getLevel();
        return level == null || level.isClientSide() ? null : tank.getBlob();
    }

    private LiquidCrystalData getData() {
        Level level = tank.getLevel();
        if (level == null || level.isClientSide()) {
            return tank.getClientLiquidData();
        }
        TankBlob blob = getBlob();
        return blob == null ? LiquidCrystalData.EMPTY : blob.getData();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidVariant getResource(int index) {
        return getData().getFluidStack().variant();
    }

    @Override
    public long getAmountAsLong(int index) {
        return getData().getAmount() * DROPLETS_PER_MB;
    }

    @Override
    public long getCapacityAsLong(int index, FluidVariant resource) {
        TankBlob blob = getBlob();
        if (blob != null) {
            return blob.getCapacity() * DROPLETS_PER_MB;
        }
        Level level = tank.getLevel();
        return level == null || level.isClientSide() ? Constants.TANK_BUCKETS * 1000 * DROPLETS_PER_MB : 0;
    }

    @Override
    public boolean isValid(int index, FluidVariant resource) {
        return !resource.isBlank();
    }

    @Override
    public long insert(int index, FluidVariant resource, long amount, TransactionContext transaction) {
        TankBlob blob = getBlob();
        if (blob == null || resource.isBlank() || amount < DROPLETS_PER_MB) {
            return 0;
        }
        var stack = new FluidStack(resource, millibuckets(amount));
        int inserted = blob.fill(stack, false);
        if (inserted > 0) {
            blob.beginTransfer(transaction, tank);
            blob.fill(stack, true);
        }
        return inserted * DROPLETS_PER_MB;
    }

    @Override
    public long extract(int index, FluidVariant resource, long amount, TransactionContext transaction) {
        TankBlob blob = getBlob();
        if (blob == null || amount < DROPLETS_PER_MB || !resource.equals(blob.getData().getFluidStack().variant())) {
            return 0;
        }
        blob.beginTransfer(transaction, tank);
        return blob.drain(millibuckets(amount), true).getAmount() * DROPLETS_PER_MB;
    }
}
