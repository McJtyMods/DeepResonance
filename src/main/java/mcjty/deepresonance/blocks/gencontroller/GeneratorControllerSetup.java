package mcjty.deepresonance.blocks.gencontroller;

import net.minecraftforge.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.lib.container.GenericItemBlock;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GeneratorControllerSetup {
    public static GeneratorControllerBlock generatorControllerBlock;

    public static void setupBlocks() {
        generatorControllerBlock = new GeneratorControllerBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        generatorControllerBlock.initModel();
    }



    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(generatorControllerBlock), "rCr", "iMi", "rir", 'M', ModBlocks.machineFrame, 'r', Items.redstone,
                'C', Items.comparator, 'i', Items.iron_ingot);
    }
}
