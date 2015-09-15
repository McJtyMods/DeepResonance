package mcjty.deepresonance.blocks.purifier;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PurifierSetup {
    public static PurifierBlock purifierBlock;

    public static void setupBlocks() {
        purifierBlock = new PurifierBlock("purifierBlock");
        purifierBlock.registerTile().register();
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(purifierBlock), "ggg", "iMi", "ggg", 'M', ModBlocks.machineFrame, 'g', ModItems.filterMaterialItem, 'i', Items.iron_ingot);
    }
}
