package mcjty.deepresonance.blocks.lens;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LensSetup {
    public static LensBlock lensBlock;

    public static void setupBlocks() {
        lensBlock = new LensBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        lensBlock.initModel();
    }
}
