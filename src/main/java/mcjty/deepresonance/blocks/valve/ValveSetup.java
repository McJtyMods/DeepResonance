package mcjty.deepresonance.blocks.valve;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ValveSetup {
    public static ValveBlock valveBlock;

    public static void setupBlocks() {
        valveBlock = new ValveBlock("valveBlock");
        valveBlock.registerTile().register();
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(valveBlock), "gtg", "fMf", "gCg", 'M', ModBlocks.machineFrame, 'f', ModItems.filterMaterialItem,
                'g', Blocks.glass, 't', Items.quartz, 'C', Items.comparator);
    }
}
