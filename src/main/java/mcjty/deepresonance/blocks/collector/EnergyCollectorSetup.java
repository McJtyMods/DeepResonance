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
}
