package mcjty.deepresonance.modules.generator.block;

import mcjty.deepresonance.compat.DeepResonanceTOPDriver;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;

import javax.annotation.Nonnull;

public class GeneratorControllerBlock extends BaseBlock {

    public GeneratorControllerBlock() {
        super(new BlockBuilder()
                .tileEntitySupplier(GeneratorControllerTileEntity::new)
                .topDriver(DeepResonanceTOPDriver.DRIVER)
                .info(TooltipBuilder.key("message.deepresonance.shiftmessage"))
                .infoShift(TooltipBuilder.header()));
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED);
    }

}
