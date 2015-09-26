package mcjty.deepresonance.blocks.pedestal;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.container.GenericItemBlock;
import mcjty.deepresonance.blocks.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class PedestalSetup {
    public static PedestalBlock pedestalBlock;

    public static void setupBlocks() {
        pedestalBlock = new PedestalBlock();
        GameRegistry.registerBlock(pedestalBlock, GenericItemBlock.class, "pedestalBlock");
        GameRegistry.registerTileEntity(PedestalTileEntity.class, "PedestalTileEntity");
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(pedestalBlock), "idi", "iMi", "iCi", 'M', ModBlocks.machineFrame, 'r', Items.redstone,
                'd', Blocks.dispenser, 'C', Items.comparator, 'i', Items.iron_ingot);
    }
}
