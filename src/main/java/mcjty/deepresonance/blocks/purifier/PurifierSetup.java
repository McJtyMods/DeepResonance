package mcjty.deepresonance.blocks.purifier;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PurifierSetup {
    public static PurifierBlock purifierBlock;

    public static void setupBlocks() {
        purifierBlock = new PurifierBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        purifierBlock.initModel();
    }
}
