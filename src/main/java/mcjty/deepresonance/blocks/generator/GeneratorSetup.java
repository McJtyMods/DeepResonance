package mcjty.deepresonance.blocks.generator;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.container.GenericItemBlock;

public class GeneratorSetup {
    public static GeneratorBlock generatorBlock;

    public static void setupBlocks() {
        generatorBlock = new GeneratorBlock();
        GameRegistry.registerBlock(generatorBlock, GenericItemBlock.class, "generatorBlock");
        GameRegistry.registerTileEntity(GeneratorTileEntity.class, "GeneratorTileEntity");
    }

    public static void setupCrafting() {
    }
}
