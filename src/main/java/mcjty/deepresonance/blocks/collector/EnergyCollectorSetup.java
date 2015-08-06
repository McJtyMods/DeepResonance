package mcjty.deepresonance.blocks.collector;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.container.GenericItemBlock;

public class EnergyCollectorSetup {
    public static EnergyCollectorBlock energyCollectorBlock;

    public static void setupBlocks() {
        energyCollectorBlock = new EnergyCollectorBlock();
        GameRegistry.registerBlock(energyCollectorBlock, GenericItemBlock.class, "energyCollectorBlock");
        GameRegistry.registerTileEntity(EnergyCollectorTileEntity.class, "EnergyCollectorTileEntity");
    }

    public static void setupCrafting() {
    }
}
