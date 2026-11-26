package mcjty.deepresonance.modules.tank.util;

import mcjty.lib.varia.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public final class TankTransfers {

    public static final long DROPLETS_PER_MB = FluidConstants.BUCKET / 1000;

    private TankTransfers() {
    }

    public static FluidStack contents(Storage<FluidVariant> tank) {
        for (StorageView<FluidVariant> view : tank) {
            if (!view.isResourceBlank()) {
                return new FluidStack(view.getResource(), millibuckets(view.getAmount()));
            }
        }
        return FluidStack.EMPTY;
    }

    public static long capacity(Storage<FluidVariant> tank) {
        var views = tank.iterator();
        return views.hasNext() ? millibuckets(views.next().getCapacity()) : 0;
    }

    public static int canInsert(Storage<FluidVariant> tank, FluidStack stack) {
        return insert(tank, stack, false);
    }

    public static int insert(Storage<FluidVariant> tank, FluidStack stack) {
        return insert(tank, stack, true);
    }

    public static boolean insertAll(Storage<FluidVariant> tank, FluidStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        long amount = stack.getAmount() * DROPLETS_PER_MB;
        try (Transaction transaction = Transaction.openOuter()) {
            if (tank.insert(stack.variant(), amount, transaction) != amount) {
                return false;
            }
            transaction.commit();
            return true;
        }
    }

    private static int insert(Storage<FluidVariant> tank, FluidStack stack, boolean execute) {
        if (stack.isEmpty()) {
            return 0;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = tank.insert(stack.variant(), stack.getAmount() * DROPLETS_PER_MB, transaction);
            if (execute) {
                transaction.commit();
            }
            return millibuckets(inserted);
        }
    }

    public static FluidStack simulateExtract(Storage<FluidVariant> tank, int amount) {
        return extract(tank, contents(tank).copyWithAmount(amount), false);
    }

    public static FluidStack extract(Storage<FluidVariant> tank, int amount) {
        return extract(tank, contents(tank).copyWithAmount(amount));
    }

    public static FluidStack extract(Storage<FluidVariant> tank, FluidStack requested) {
        return extract(tank, requested, true);
    }

    private static FluidStack extract(Storage<FluidVariant> tank, FluidStack requested, boolean execute) {
        FluidStack stored = contents(tank);
        if (requested.isEmpty() || stored.isEmpty() || requested.getFluid() != stored.getFluid()) {
            return FluidStack.EMPTY;
        }
        FluidVariant resource = stored.variant();
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = tank.extract(resource, requested.getAmount() * DROPLETS_PER_MB, transaction);
            if (execute) {
                transaction.commit();
            }
            return new FluidStack(resource, millibuckets(extracted));
        }
    }

    public static int millibuckets(long droplets) {
        return (int) Math.min(Integer.MAX_VALUE, droplets / DROPLETS_PER_MB);
    }

    public static void move(Storage<FluidVariant> from, Storage<FluidVariant> to, FluidStack stack) {
        if (stack.isEmpty() || from == to) {
            return;
        }
        FluidVariant resource = stack.variant();
        long amount = stack.getAmount() * DROPLETS_PER_MB;
        try (Transaction transaction = Transaction.openOuter()) {
            if (from.extract(resource, amount, transaction) == amount && to.insert(resource, amount, transaction) == amount) {
                transaction.commit();
            }
        }
    }
}
