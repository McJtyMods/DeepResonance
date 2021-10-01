package mcjty.deepresonance.modules.machines.tile;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.block.AbstractBlock;

public class LensBlock extends BaseBlock {

    public LensBlock(AbstractBlock.Properties properties) {
        super(new BlockBuilder()
                .properties(properties)
                .tileEntitySupplier(TileEntityLens::new));
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }
}
