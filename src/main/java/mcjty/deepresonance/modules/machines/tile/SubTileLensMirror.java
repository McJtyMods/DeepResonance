package mcjty.deepresonance.modules.machines.tile;

import elec332.core.tile.sub.SubTileLogicBase;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 28-7-2020
 * <p>
 * Todo: lens mirrors
 */
public class SubTileLensMirror extends SubTileLogicBase {

    public SubTileLensMirror(Data data) {
        super(data);
    }

    @Override
    public VoxelShape getShape(BlockState state, int data) {
        return VoxelShapes.empty();
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
    }

    @Nonnull
    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT compound) {
        return compound;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return LazyOptional.empty();
    }

}
