package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.multiblock.IMultiblock;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.*;

public class TankBlob implements IMultiblock {
    private LiquidCrystalData data;
    private int tankBlocks;
    private int minY;               // Minimum Y for this tank blob
    private int[] blocksPerLevel;   // Amount of blocks per Y level

    public TankBlob() {
    }

    public TankBlob(TankBlob other) {
        this.data = other.data;
        this.tankBlocks = other.tankBlocks;
        this.minY = other.minY;
        this.blocksPerLevel = other.blocksPerLevel;
    }

    public int getMinY() {
        return minY;
    }

    public int getBlocksAtY(int y) {
        if (blocksPerLevel == null) {
            return 0;
        }
        if (y >= minY && y < minY + blocksPerLevel.length) {
            return blocksPerLevel[y-minY];
        } else {
            return 0;
        }
    }

    public int getBlocksBelowY(int y) {
        int total = 0;
        for (int i = minY ; i < y ; i++) {
            total += getBlocksAtY(i);
        }
        return total;
    }

    public int getCapacityPerTank() {
        return 10 * 1000;   // @todo 1.16 configurable
    }

    public int getCapacity() {
        return tankBlocks * 10 * 1000; // @todo 1.16 configurable!
    }

    public void merge(TankBlob other) {
        other.getData().ifPresent(d -> this.data.merge(d));
        this.tankBlocks += other.getTankBlocks();
        if (this.blocksPerLevel == null) {
            this.minY = other.minY;
            this.blocksPerLevel = other.blocksPerLevel;
        } else if (other.blocksPerLevel != null) {
            int minY = Math.min(this.minY, other.minY);
            int maxY = Math.max(this.minY + this.blocksPerLevel.length, other.minY + other.blocksPerLevel.length);
            int[] mergedBlocksPerLevel = new int[maxY - minY + 1];
            for (int y = minY; y <= maxY; y++) {
                int ithis = y - this.minY;
                int iother = y - other.minY;
                mergedBlocksPerLevel[y - minY] = (ithis >= 0 && ithis < this.blocksPerLevel.length) ? this.blocksPerLevel[ithis] : 0;
                mergedBlocksPerLevel[y - minY] += (iother >= 0 && iother < other.blocksPerLevel.length) ? other.blocksPerLevel[iother] : 0;
            }
            this.minY = minY;
            this.blocksPerLevel = mergedBlocksPerLevel;
        }
    }

    /**
     * Fix the y statistics of this blob
     */
    public void updateDistribution(Set<BlockPos> blocks) {
        int minY = blocks.stream().map(Vector3i::getY).min(Integer::compareTo).orElse(0);
        int maxY = blocks.stream().map(Vector3i::getY).max(Integer::compareTo).orElse(0);
        this.blocksPerLevel = new int[maxY-minY+ 1];
        Arrays.fill(this.blocksPerLevel, 0);
        blocks.forEach(b -> this.blocksPerLevel[b.getY()-minY]++);
        this.minY = minY;
    }

    // Return the amount of liquid filled
    public int fill(FluidStack stack, IFluidHandler.FluidAction action) {
        // @todo 1.16 does not check max capacity yet!
        if (stack.isEmpty()) {
            return 0;
        } else if (data == null || data.toFluidStack().isEmpty()) {
            if (action.execute()) {
                data = LiquidCrystalData.fromStack(stack);
            }
            return stack.getAmount();
        } else if (data.toFluidStack().getFluid() == stack.getFluid()) {
            // @todo 1.16 implement mixing!
            if (action.execute()) {
                data.setAmount(data.getAmount() + stack.getAmount());
            }
            return stack.getAmount();
        } else {
            return 0;
        }
    }

    // Return the fluid that was drained
    @Nonnull
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || data == null || data.toFluidStack().isEmpty()) {
            return FluidStack.EMPTY;
        } else if (resource.getFluid() != data.toFluidStack().getFluid()) {
            return FluidStack.EMPTY;
        } else if (resource.getAmount() >= data.getAmount()) {
            FluidStack result = data.toFluidStack();
            if (action.execute()) {
                data = null;
            }
            return result;
        } else {
            FluidStack result = data.toFluidStack().copy();
            result.setAmount(resource.getAmount());
            if (action.execute()) {
                data.toFluidStack().setAmount(data.toFluidStack().getAmount() - result.getAmount());
            }
            return result;
        }
    }

    // Return the fluid that was drained
    @Nonnull
    public FluidStack drain(int amount, IFluidHandler.FluidAction action) {
        if (amount <= 0 || data == null || data.toFluidStack().isEmpty()) {
            return FluidStack.EMPTY;
        } else if (amount >= data.getAmount()) {
            FluidStack result = data.toFluidStack();
            if (action.execute()) {
                data = null;
            }
            return result;
        } else {
            FluidStack result = data.toFluidStack().copy();
            result.setAmount(amount);
            if (action.execute()) {
                data.toFluidStack().setAmount(data.toFluidStack().getAmount() - amount);
            }
            return result;
        }
    }

    @Nonnull
    public Optional<LiquidCrystalData> getData() {
        return Optional.ofNullable(data);
    }

    public TankBlob copyData(LiquidCrystalData data) {
        this.data = LiquidCrystalData.fromStack(data.toFluidStack());
        return this;
    }

    public void setStack(FluidStack stack) {
        this.data = LiquidCrystalData.fromStack(stack);
    }

    public TankBlob setTankBlocks(int tankBlocks) {
        this.tankBlocks = tankBlocks;
        return this;
    }

    public int getTankBlocks() {
        return tankBlocks;
    }

    public static TankBlob load(CompoundNBT tagCompound) {
        TankBlob blob = new TankBlob();
        blob.data = LiquidCrystalData.fromStack(FluidStack.loadFluidStackFromNBT(tagCompound.getCompound("fluid")));
        blob.setTankBlocks(tagCompound.getInt("refcount"));
        blob.minY = tagCompound.getInt("miny");
        blob.blocksPerLevel = tagCompound.getIntArray("blocksperlevel");
        return blob;
    }

    public static CompoundNBT save(CompoundNBT tagCompound, TankBlob network) {
        if (network.data != null) {
            tagCompound.put("fluid", network.data.toFluidStack().writeToNBT(new CompoundNBT()));
        }
        tagCompound.putInt("refcount", network.tankBlocks);
        tagCompound.putInt("miny", network.minY);
        tagCompound.putIntArray("blocksperlevel", network.blocksPerLevel);
        return tagCompound;
    }
}
