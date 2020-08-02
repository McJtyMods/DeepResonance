package mcjty.deepresonance.util;

import elec332.core.world.WorldHelper;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Created by Elec332 on 27-7-2020
 */
public class DeepResonanceBlock extends BaseBlock {

    public DeepResonanceBlock(BlockBuilder builder) {
        super(builder);
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof AbstractTileEntity) {
            ((AbstractTileEntity) tile).onNeighborChange(state, neighbor);
        }
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    public DeepResonanceBlock modify(Consumer<DeepResonanceBlock> mod) {
        if (mod != null) {
            mod.accept(this);
        }
        return this;
    }

    public DeepResonanceBlock modifyDefaultState(UnaryOperator<BlockState> modifier) {
        if (modifier != null) {
            setDefaultState(modifier.apply(getDefaultState()));
        }
        return this;
    }

}
