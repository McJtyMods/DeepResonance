package mcjty.deepresonance.blocks.smelter;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SmelterSetup {
    public static SmelterBlock smelter;

    public static void setupBlocks() {
        smelter = new SmelterBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        smelter.initModel();
    }
}
