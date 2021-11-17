package mcjty.deepresonance.modules.generator.block;

import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GeneratorPartBlock extends BaseBlock {

    public GeneratorPartBlock() {
        super(new BlockBuilder()
                .tileEntitySupplier(GeneratorPartTileEntity::new)
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("generator_part"))));
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return state.setValue(BlockStateProperties.UP, world.getBlockState(pos.above()).getBlock() == this)
                .setValue(BlockStateProperties.DOWN, world.getBlockState(pos.below()).getBlock() == this);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull IWorld world, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        if (facing == Direction.UP) {
            return state.setValue(BlockStateProperties.UP, facingState.getBlock() == this);
        }
        if (facing == Direction.DOWN) {
            return state.setValue(BlockStateProperties.DOWN, facingState.getBlock() == this);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED, BlockStateProperties.UP, BlockStateProperties.DOWN);
    }

}
