package mcjty.deepresonance.blocks.crystals;

import mcjty.lib.container.GenericItemBlock;
import net.minecraft.block.Block;

public class ResonatingCrystalItemBlock extends GenericItemBlock {

    public ResonatingCrystalItemBlock(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }
}
