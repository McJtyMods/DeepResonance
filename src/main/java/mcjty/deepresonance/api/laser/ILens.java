package mcjty.deepresonance.api.laser;

import mcjty.deepresonance.api.fluid.ILiquidCrystalData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 28-7-2020
 */
public interface ILens extends INBTSerializable<CompoundNBT> {

    default void onNeighborUpdate(IWorld world, BlockPos pos, Direction side) {
    }

    VoxelShape getHitbox();

    ItemStack getPickBlock();

    void infuseFluid(int amount, Consumer<ILiquidCrystalData> modifier);

}
