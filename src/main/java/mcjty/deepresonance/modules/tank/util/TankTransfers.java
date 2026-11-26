package mcjty.deepresonance.modules.tank.util;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class TankTransfers {

    private TankTransfers() {
    }

    public static FluidStack contents(ResourceHandler<FluidResource> tank) {
        return tank.size() == 0 ? FluidStack.EMPTY : FluidUtil.getStack(tank, 0);
    }

    public static long capacity(ResourceHandler<FluidResource> tank) {
        return tank.size() == 0 ? 0 : tank.getCapacityAsLong(0, tank.getResource(0));
    }

    public static int canInsert(ResourceHandler<FluidResource> tank, FluidStack stack) {
        return insert(tank, stack, false);
    }

    public static int insert(ResourceHandler<FluidResource> tank, FluidStack stack) {
        return insert(tank, stack, true);
    }

    public static boolean insertAll(ResourceHandler<FluidResource> tank, FluidStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        int amount = stack.getAmount();
        try (Transaction transaction = Transaction.openRoot()) {
            if (tank.insert(FluidResource.of(stack), amount, transaction) != amount) {
                return false;
            }
            transaction.commit();
            return true;
        }
    }

    private static int insert(ResourceHandler<FluidResource> tank, FluidStack stack, boolean execute) {
        if (stack.isEmpty()) {
            return 0;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = tank.insert(FluidResource.of(stack), stack.getAmount(), transaction);
            if (execute) {
                transaction.commit();
            }
            return inserted;
        }
    }

    public static FluidStack simulateExtract(ResourceHandler<FluidResource> tank, int amount) {
        return extract(tank, contents(tank).copyWithAmount(amount), false);
    }

    public static FluidStack extract(ResourceHandler<FluidResource> tank, int amount) {
        return extract(tank, contents(tank).copyWithAmount(amount));
    }

    public static FluidStack extract(ResourceHandler<FluidResource> tank, FluidStack requested) {
        return extract(tank, requested, true);
    }

    private static FluidStack extract(ResourceHandler<FluidResource> tank, FluidStack requested, boolean execute) {
        FluidStack stored = contents(tank);
        if (requested.isEmpty() || stored.isEmpty() || requested.getFluid() != stored.getFluid()) {
            return FluidStack.EMPTY;
        }
        FluidResource resource = FluidResource.of(stored);
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = tank.extract(resource, requested.getAmount(), transaction);
            if (execute) {
                transaction.commit();
            }
            return resource.toStack(extracted);
        }
    }

    public static void move(ResourceHandler<FluidResource> from, ResourceHandler<FluidResource> to, FluidStack stack) {
        if (stack.isEmpty() || from == to) {
            return;
        }
        FluidResource resource = FluidResource.of(stack);
        int amount = stack.getAmount();
        try (Transaction transaction = Transaction.openRoot()) {
            if (from.extract(resource, amount, transaction) == amount && to.insert(resource, amount, transaction) == amount) {
                transaction.commit();
            }
        }
    }
}
