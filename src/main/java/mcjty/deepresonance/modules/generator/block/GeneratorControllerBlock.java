package mcjty.deepresonance.modules.generator.block;

import mcjty.deepresonance.util.TranslationHelper;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;

public class GeneratorControllerBlock extends BaseBlock {

    public GeneratorControllerBlock() {
        super(new BlockBuilder()
                .tileEntitySupplier(GeneratorControllerTileEntity::new)
                .infoShift(TooltipBuilder.key(TranslationHelper.getTooltipKey("generator_controller"))));
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED);
    }

}
