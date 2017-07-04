package mcjty.deepresonance.blocks.gencontroller;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GeneratorControllerSetup {
    public static GeneratorControllerBlock generatorControllerBlock;

    public static void setupBlocks() {
        generatorControllerBlock = new GeneratorControllerBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        generatorControllerBlock.initModel();
    }
}
