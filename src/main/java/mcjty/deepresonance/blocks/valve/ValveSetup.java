package mcjty.deepresonance.blocks.valve;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ValveSetup {

    public static ValveBlock valveBlock;

    public static void setupBlocks() {
        valveBlock = new ValveBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        valveBlock.initModel();
    }
}
