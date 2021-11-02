package mcjty.deepresonance.modules.tank.data;

import mcjty.deepresonance.util.LiquidCrystalData;
import mcjty.lib.multiblock.IMultiblock;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Optional;

public class TankBlob implements IMultiblock {
    private LiquidCrystalData data;
    private int tankBlocks;

    public TankBlob() {
    }

    public TankBlob(TankBlob other) {
        this.data = other.data;
        this.tankBlocks = other.tankBlocks;
    }

    public int getCapacity() {
        return tankBlocks * 10 * 1000; // @todo 1.16 configurable!
    }

    public void merge(TankBlob other) {
        other.getData().ifPresent(d -> this.data.merge(d));
        this.tankBlocks += other.getTankBlocks();
    }

    // Return the amount of liquid filled
    public int fill(FluidStack stack, IFluidHandler.FluidAction action) {
        // @todo 1.16 does not check max capacity yet!
        if (stack.isEmpty()) {
            return 0;
        } else if (data == null || data.getStack().isEmpty()) {
            if (action.execute()) {
                data = LiquidCrystalData.fromStack(stack);
            }
            return stack.getAmount();
        } else if (data.getStack().getFluid() == stack.getFluid()) {
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
        if (resource.isEmpty() || data == null || data.getStack().isEmpty()) {
            return FluidStack.EMPTY;
        } else if (resource.getFluid() != data.getStack().getFluid()) {
            return FluidStack.EMPTY;
        } else if (resource.getAmount() >= data.getAmount()) {
            FluidStack result = data.getStack();
            if (action.execute()) {
                data = null;
            }
            return result;
        } else {
            FluidStack result = data.getStack().copy();
            result.setAmount(resource.getAmount());
            if (action.execute()) {
                data.getStack().setAmount(data.getStack().getAmount() - result.getAmount());
            }
            return result;
        }
    }

    // Return the fluid that was drained
    @Nonnull
    public FluidStack drain(int amount, IFluidHandler.FluidAction action) {
        if (amount <= 0 || data == null || data.getStack().isEmpty()) {
            return FluidStack.EMPTY;
        } else if (amount >= data.getAmount()) {
            FluidStack result = data.getStack();
            if (action.execute()) {
                data = null;
            }
            return result;
        } else {
            FluidStack result = data.getStack().copy();
            result.setAmount(amount);
            if (action.execute()) {
                data.getStack().setAmount(data.getStack().getAmount() - amount);
            }
            return result;
        }
    }

    @Nonnull
    public Optional<LiquidCrystalData> getData() {
        return Optional.ofNullable(data);
    }

    public TankBlob copyData(LiquidCrystalData data) {
        this.data = new LiquidCrystalData(data);
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
        return blob;
    }

    public static CompoundNBT save(CompoundNBT tagCompound, TankBlob network) {
        if (network.data != null) {
            tagCompound.put("fluid", network.data.toFluidStack().writeToNBT(new CompoundNBT()));
        }
        tagCompound.putInt("refcount", network.tankBlocks);
        return tagCompound;
    }
}
