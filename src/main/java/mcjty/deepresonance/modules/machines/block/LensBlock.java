package mcjty.deepresonance.modules.machines.block;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

public class LensBlock extends BaseBlock {

    private static final VoxelShape SHAPE_SOUTH = Shapes.box(.2, .2, .9, .8, .8, 1);
    private static final VoxelShape SHAPE_NORTH = Shapes.box(.2, .2, 0, .8, .8, .1);
    private static final VoxelShape SHAPE_EAST = Shapes.box(.9, .2, .2, 1, .8, .8);
    private static final VoxelShape SHAPE_WEST = Shapes.box(0, .2, .2, .1, .8, .8);

    public LensBlock() {
        super(new BlockBuilder()
                .properties(Block.Properties.of(Material.METAL).strength(2.0F).sound(SoundType.METAL).noOcclusion().noCollission())
                .tileEntitySupplier(LensTileEntity::new));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return switch (facing) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_WEST;
        };
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        Direction opposite = stateForPlacement.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
        return stateForPlacement.setValue(BlockStateProperties.HORIZONTAL_FACING, opposite);
    }
}
