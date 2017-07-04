package mcjty.deepresonance.blocks.crystalizer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CrystalizerSetup {
    public static CrystalizerBlock crystalizer;

    public static void setupBlocks() {
        crystalizer = new CrystalizerBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        crystalizer.initModel();
    }
}
