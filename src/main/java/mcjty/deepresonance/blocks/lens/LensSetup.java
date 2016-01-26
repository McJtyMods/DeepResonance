package mcjty.deepresonance.blocks.lens;

import net.minecraftforge.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.blocks.collector.EnergyCollectorBlock;
import mcjty.deepresonance.blocks.collector.EnergyCollectorTileEntity;
import mcjty.deepresonance.items.ModItems;
import mcjty.lib.container.GenericItemBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class LensSetup {
    public static LensBlock lensBlock;

    public static void setupBlocks() {
        lensBlock = new LensBlock();
        GameRegistry.registerBlock(lensBlock, LensItemBlock.class, "lensBlock");
        GameRegistry.registerTileEntity(LensTileEntity.class, "LensTileEntity");
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(lensBlock), "gpg", "pXp", "gpg", 'g', Blocks.glass_pane, 'p', ModItems.resonatingPlateItem, 'X', Items.emerald);
    }
}
