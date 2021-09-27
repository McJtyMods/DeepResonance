package mcjty.deepresonance.modules.generator.block;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class BlockCollector extends BaseBlock {

    private static final VoxelShape AABB = VoxelShapes.box(0, 0, 0, 1, 5 / 16f, 1);

    public BlockCollector(BlockBuilder builder) {
        super(builder);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return AABB;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

}
