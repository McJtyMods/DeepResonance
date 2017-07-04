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

    public static void setupCrafting() {
//        GameRegistry.addRecipe(new ItemStack(crystalizer), "ggg", "qMq", "iii", 'M', ModBlocks.machineFrame, 'g', Blocks.GLASS, 'q', Items.QUARTZ, 'i', Items.IRON_INGOT);
    }
}
