package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.modules.tank.blocks.TankTileEntity;
import mcjty.deepresonance.util.Constants;
import mcjty.deepresonance.util.LiquidCrystalData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class DRTankHandler implements ResourceHandler<FluidResource> {

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
    public FluidResource getResource(int index) {
        return FluidResource.of(getData().getFluidStack());
    }

    @Override
    public long getAmountAsLong(int index) {
        return getData().getAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        TankBlob blob = getBlob();
        if (blob != null) {
            return blob.getCapacity();
        }
        Level level = tank.getLevel();
        return level == null || level.isClientSide() ? Constants.TANK_BUCKETS * 1000 : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return !resource.isEmpty();
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TankBlob blob = getBlob();
        if (blob == null || resource.isEmpty() || amount == 0) {
            return 0;
        }
        var stack = resource.toStack(amount);
        int inserted = blob.fill(stack, false);
        if (inserted > 0) {
            blob.beginTransfer(transaction, tank);
            blob.fill(stack, true);
        }
        return inserted;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TankBlob blob = getBlob();
        if (blob == null || amount == 0 || !resource.matches(blob.getData().getFluidStack())) {
            return 0;
        }
        blob.beginTransfer(transaction, tank);
        return blob.drain(amount, true).getAmount();
    }
}
