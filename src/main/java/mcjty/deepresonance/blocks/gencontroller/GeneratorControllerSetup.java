package mcjty.deepresonance.blocks.gencontroller;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.container.GenericItemBlock;

public class GeneratorControllerSetup {
    public static GeneratorControllerBlock generatorControllerBlock;

    public static void setupBlocks() {
        generatorControllerBlock = new GeneratorControllerBlock();
        GameRegistry.registerBlock(generatorControllerBlock, GenericItemBlock.class, "generatorControllerBlock");
        GameRegistry.registerTileEntity(GeneratorControllerTileEntity.class, "GeneratorControllerTileEntity");
    }

    public static void setupCrafting() {
    }
}
