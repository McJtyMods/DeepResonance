package mcjty.deepresonance.blocks.valve;

import net.minecraftforge.fml.common.registry.GameRegistry;
import mcjty.deepresonance.blocks.ModBlocks;
import mcjty.deepresonance.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ValveSetup {

    public static ValveBlock valveBlock;

    public static void setupBlocks() {
        valveBlock = new ValveBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        valveBlock.initModel();
    }


    public static void setupCrafting() {
        GameRegistry.addRecipe(new ItemStack(valveBlock), "gtg", "fMf", "gCg", 'M', ModBlocks.machineFrame, 'f', ModItems.filterMaterialItem,
                'g', Blocks.glass, 't', Items.quartz, 'C', Items.comparator);
    }

}
