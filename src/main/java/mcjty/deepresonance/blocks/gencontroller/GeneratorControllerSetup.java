package mcjty.deepresonance.blocks.gencontroller;

import cpw.mods.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.lib.container.GenericItemBlock;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GeneratorControllerSetup {
    public static GeneratorControllerBlock generatorControllerBlock;

    public static void setupBlocks() {
        generatorControllerBlock = new GeneratorControllerBlock();
        GameRegistry.registerBlock(generatorControllerBlock, GenericItemBlock.class, "generatorControllerBlock");
        GameRegistry.registerTileEntity(GeneratorControllerTileEntity.class, "GeneratorControllerTileEntity");
    }

    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(generatorControllerBlock), "rCr", "iMi", "rir", 'M', ModBlocks.machineFrame, 'r', Items.redstone,
                'C', Items.comparator, 'i', Items.iron_ingot);
    }
}
