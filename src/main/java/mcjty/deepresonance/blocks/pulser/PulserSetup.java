package mcjty.deepresonance.blocks.pulser;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PulserSetup {
    public static PulserBlock pulserBlock;

    public static void setupBlocks() {
        pulserBlock = new PulserBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        pulserBlock.initModel();
    }
}
