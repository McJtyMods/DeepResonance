package mcjty.deepresonance.blocks.generator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GeneratorSetup {
    public static GeneratorBlock generatorBlock;

    public static void setupBlocks() {
        generatorBlock = new GeneratorBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        generatorBlock.initModel();
    }
}
