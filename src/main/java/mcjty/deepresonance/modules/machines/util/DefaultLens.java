package mcjty.deepresonance.modules.machines.util;

import elec332.core.world.WorldHelper;
import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import mcjty.deepresonance.api.laser.ILens;
import mcjty.deepresonance.fluids.LiquidCrystalData;
import mcjty.deepresonance.modules.machines.MachinesModule;
import mcjty.deepresonance.setup.FluidRegister;
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

/**
 * Created by Elec332 on 28-7-2020
 */
public class DefaultLens implements ILens {

    private static final VoxelShape SHAPE = VoxelShapes.create(2.1f / 16, 0, 2.1f / 16, 13.9f / 16, 1.7f / 16, 13.9f / 16);
    private LazyOptional<IFluidHandler> fluidHandler = LazyOptional.empty();

    @Override
    public void checkNeighbors(IWorld world, BlockPos pos, Direction side) {
        if (!fluidHandler.isPresent()) {
            TileEntity tile = WorldHelper.getTileAt(world, pos.offset(side));
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
                if (FluidRegister.isValidLiquidCrystalStack(fluid)) {
                    fluid = tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                    ILiquidCrystalData data = LiquidCrystalData.fromStack(fluid);
                    if (data != null) {
                        modifier.accept(data);
                        if (data.getAmount() > 0) {
                            tank.fill(data.toFluidStack(), IFluidHandler.FluidAction.EXECUTE);
                        }
                    } else {
                        tank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            });
        }
    }

}
