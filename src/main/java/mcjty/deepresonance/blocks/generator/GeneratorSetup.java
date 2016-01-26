package mcjty.deepresonance.blocks.generator;

import net.minecraftforge.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.lib.container.GenericItemBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GeneratorSetup {
    public static GeneratorBlock generatorBlock;

    public static void setupBlocks() {
        generatorBlock = new GeneratorBlock();
        GameRegistry.registerBlock(generatorBlock, GenericItemBlock.class, "generatorBlock");
        GameRegistry.registerTileEntity(GeneratorTileEntity.class, "GeneratorTileEntity");
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(generatorBlock), "nRn", "iMi", "nRn", 'M', ModBlocks.machineFrame, 'n', Items.gold_nugget, 'R', Blocks.redstone_block,
                'i', Items.iron_ingot);
    }
}
