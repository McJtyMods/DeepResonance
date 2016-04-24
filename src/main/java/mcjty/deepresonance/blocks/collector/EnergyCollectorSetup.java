package mcjty.deepresonance.blocks.collector;

import mcjty.deepresonance.blocks.ModBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
        GameRegistry.addRecipe(new ItemStack(energyCollectorBlock), " q ", "qMq", "ggg", 'M', ModBlocks.machineFrame, 'g', Items.GOLD_INGOT, 'q', Items.QUARTZ);
    }
}
