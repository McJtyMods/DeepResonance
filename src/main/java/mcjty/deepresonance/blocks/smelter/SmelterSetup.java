package mcjty.deepresonance.blocks.smelter;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SmelterSetup {
    public static Block smelter;

    public static void setupBlocks() {
        smelter = new SmelterBlock("smelter").registerTile().register();
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(smelter), "ppp", "iMi", "nnn", 'M', ModBlocks.machineFrame, 'n', Blocks.nether_brick, 'p', ModItems.resonatingPlateItem,
                'i', Items.iron_ingot);
    }
}
