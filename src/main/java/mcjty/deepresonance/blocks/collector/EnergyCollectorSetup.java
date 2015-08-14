package mcjty.deepresonance.blocks.collector;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.container.GenericItemBlock;
import mcjty.deepresonance.blocks.ModBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnergyCollectorSetup {
    public static EnergyCollectorBlock energyCollectorBlock;

    public static void setupBlocks() {
        energyCollectorBlock = new EnergyCollectorBlock();
        GameRegistry.registerBlock(energyCollectorBlock, GenericItemBlock.class, "energyCollectorBlock");
        GameRegistry.registerTileEntity(EnergyCollectorTileEntity.class, "EnergyCollectorTileEntity");
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(energyCollectorBlock), " q ", "qMq", "ggg", 'M', ModBlocks.machineFrame, 'g', Items.gold_ingot, 'q', Items.quartz);
    }
}
