package mcjty.deepresonance.blocks.collector;

import mcjty.deepresonance.blocks.ModBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnergyCollectorSetup {
    public static EnergyCollectorBlock energyCollectorBlock;

    public static void setupBlocks() {
        energyCollectorBlock = new EnergyCollectorBlock();
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(energyCollectorBlock), " q ", "qMq", "ggg", 'M', ModBlocks.machineFrame, 'g', Items.gold_ingot, 'q', Items.quartz);
    }
}
