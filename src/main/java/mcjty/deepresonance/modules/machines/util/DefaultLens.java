package mcjty.deepresonance.modules.machines.util;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.api.laser.ILens;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.util.DeepResonanceFluidHelper;
import mcjty.deepresonance.util.LiquidCrystalData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.function.Consumer;

public class DefaultLens implements ILens {

    private static final VoxelShape SHAPE = VoxelShapes.box(2.1f / 16, 0, 2.1f / 16, 13.9f / 16, 1.7f / 16, 13.9f / 16);
    private LazyOptional<IFluidHandler> fluidHandler = LazyOptional.empty();

    @Override
    public void checkNeighbors(IWorld world, BlockPos pos, Direction side) {
        if (!fluidHandler.isPresent()) {
            TileEntity tile = world.getBlockEntity(pos.relative(side));
            if (tile != null) {
                LazyOptional<IFluidHandler> handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
                if (handler.isPresent()) {
                    fluidHandler = handler;
                }
            }
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
    }

    @Override
    public VoxelShape getHitbox() {
        return SHAPE;
    }

    @Override
    public ItemStack getPickBlock() {
        return new ItemStack(MachinesModule.LENS_ITEM.get());
    }

    @Override
    public void infuseFluid(int amount, Consumer<ILiquidCrystalData> modifier) {
        if (amount > 0 && fluidHandler.isPresent()) {
            fluidHandler.ifPresent(tank -> {
                FluidStack fluid = tank.drain(amount, IFluidHandler.FluidAction.SIMULATE);
                if (DeepResonanceFluidHelper.isValidLiquidCrystalStack(fluid)) {
                    fluid = tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                    LiquidCrystalData data = DeepResonanceFluidHelper.readCrystalDataFromStack(fluid);
                    modifier.accept(data);
                    if (data.getAmount() > 0) {
                        tank.fill(data.getFluidStack(), IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            });
        }
    }

}
