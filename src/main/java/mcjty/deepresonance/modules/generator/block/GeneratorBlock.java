package mcjty.deepresonance.modules.generator.block;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GeneratorBlock extends BaseBlock {

    public GeneratorBlock(BlockBuilder builder) {
        super(builder);
    }

    @Override
    public void onPlace(BlockState state, World level, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        super.onPlace(state, level, pos, p_220082_4_, p_220082_5_);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED, BlockStateProperties.UP, BlockStateProperties.DOWN);
    }

}
