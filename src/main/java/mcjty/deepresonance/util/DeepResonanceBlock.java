package mcjty.deepresonance.util;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 27-7-2020
 */
public class DeepResonanceBlock extends BaseBlock {

    public DeepResonanceBlock(BlockBuilder builder) {
        super(builder);
    }

    public void addProperties(Consumer<IProperty<?>> stateContainer) {
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        addProperties(builder::add);
    }

}
