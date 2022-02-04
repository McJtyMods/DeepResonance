package mcjty.deepresonance.modules.machines.block;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class LensBlock extends BaseBlock {

    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.box(.2, .2, .9, .8, .8, 1);
    private static final VoxelShape SHAPE_NORTH = VoxelShapes.box(.2, .2, 0, .8, .8, .1);
    private static final VoxelShape SHAPE_EAST = VoxelShapes.box(.9, .2, .2, 1, .8, .8);
    private static final VoxelShape SHAPE_WEST = VoxelShapes.box(0, .2, .2, .1, .8, .8);

    public LensBlock() {
        super(new BlockBuilder()
                .properties(Block.Properties.of(Material.METAL).strength(2.0F).sound(SoundType.METAL).noOcclusion().noCollission())
                .tileEntitySupplier(LensTileEntity::new));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        switch (facing) {
            case DOWN:
            case UP:
                return SHAPE_WEST;
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
        }
        return SHAPE_WEST;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        Direction opposite = stateForPlacement.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
        return stateForPlacement.setValue(BlockStateProperties.HORIZONTAL_FACING, opposite);
    }
}
