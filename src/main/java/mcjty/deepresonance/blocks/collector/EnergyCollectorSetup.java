package mcjty.deepresonance.blocks.collector;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnergyCollectorSetup {
    public static EnergyCollectorBlock energyCollectorBlock;

    public static void setupBlocks() {
        energyCollectorBlock = new EnergyCollectorBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        energyCollectorBlock.initModel();
    }

    public static void setupCrafting() {
//        GameRegistry.addRecipe(new ItemStack(energyCollectorBlock), " q ", "qMq", "ggg", 'M', ModBlocks.machineFrame, 'g', Items.GOLD_INGOT, 'q', Items.QUARTZ);
    }
}
